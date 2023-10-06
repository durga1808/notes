package com.zaga.handler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.zaga.entity.otelmetric.OtelMetric;
import com.zaga.entity.otelmetric.ResourceMetric;
import com.zaga.entity.otelmetric.ScopeMetric;
import com.zaga.entity.otelmetric.scopeMetric.Metric;
import com.zaga.entity.otelmetric.scopeMetric.MetricGauge;
import com.zaga.entity.otelmetric.scopeMetric.MetricSum;
import com.zaga.entity.otelmetric.scopeMetric.gauge.GaugeDataPoint;
import com.zaga.entity.otelmetric.scopeMetric.sum.SumDataPoint;
import com.zaga.entity.queryentity.metrics.MetricDTO;
import com.zaga.repo.MetricCommandRepo;
import com.zaga.repo.MetricDTORepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MetricCommandHandler {

    @Inject
    MetricCommandRepo metricCommandRepo;

    @Inject
    MetricDTORepo metricDtoRepo;

    public void createMetricProduct(OtelMetric metrics) {
        metricCommandRepo.persist(metrics);
        List<MetricDTO> metricDTOs = extractAndMapData(metrics);
        System.out.println("---------MetricDTOs:---------- " + metricDTOs.size());
    }

    private List<MetricDTO> extractAndMapData(OtelMetric metrics) {
        // Initialize a list to store MetricDTOs
        List<MetricDTO> metricDTOs = new ArrayList<>();
    // Initialize memoryUsage as a running total
Integer memoryUsage = 0;

try {
    for (ResourceMetric resourceMetric : metrics.getResourceMetrics()) {
        String serviceName = getServiceName(resourceMetric);
        for (ScopeMetric scopeMetric : resourceMetric.getScopeMetrics()) {
            Date createdTime = null;
            Double cpuUsage = null;
            String name = scopeMetric.getScope().getName();
            if (name != null && name.contains("io.opentelemetry.runtime")) {
                List<Metric> metricsList = scopeMetric.getMetrics();
                for (Metric metric : metricsList) {
                    String metricName = metric.getName();
                    if (isSupportedMetric(metricName)) {
                        if (metric.getSum() != null) {
                            MetricSum metricSum = metric.getSum();
                            List<SumDataPoint> sumDataPoints = metricSum.getDataPoints();

                            for (SumDataPoint sumDataPoint : sumDataPoints) {
                                String startTimeUnixNano = sumDataPoint.getTimeUnixNano();
                                createdTime = convertUnixNanoToLocalDateTime(startTimeUnixNano);
                                if (isMemoryMetric(metricName)) {
                                    if (sumDataPoint.getAsInt() != null && !sumDataPoint.getAsInt().isEmpty()) {
                                        String asInt = sumDataPoint.getAsInt();
                                        int currentMemoryUsage = Integer.parseInt(asInt);
                                        System.out.println("--------Memory usage:----- " + currentMemoryUsage);

                                        memoryUsage += currentMemoryUsage;
                                    }
                                }
                            }
                        }
                        if (metric.getGauge() != null) {
                            MetricGauge metricGauge = metric.getGauge();
                            List<GaugeDataPoint> gaugeDataPoints = metricGauge.getDataPoints();
                            for (GaugeDataPoint gaugeDataPoint : gaugeDataPoints) {
                                if (isCpuMetric(metricName)) {
                                    if (gaugeDataPoint.getAsDouble() != null) {
                                        String asDouble = gaugeDataPoint.getAsDouble();
                                        System.out.println("--------asDOUBLE------" + asDouble);
                                        cpuUsage = Double.parseDouble(asDouble);
                                        System.out.println("--------cpuUsage-------" + cpuUsage);
                                    }
                                }
                            }
                        }

                        Integer memoryUsageInMb = (memoryUsage / (1024 * 1024));

                        // Create a MetricDTO and add it to the list
                        MetricDTO metricDTO = new MetricDTO();
                        metricDTO.setMemoryUsage(memoryUsageInMb);
                        metricDTO.setDate(createdTime);
                        metricDTO.setServiceName(serviceName);
                        metricDTO.setCpuUsage(cpuUsage);
                        metricDTOs.add(metricDTO);
                    }
                }
            }
        }
    }

    if (!metricDTOs.isEmpty()) {
        // Only persist the last MetricDTO, outside the loop
        metricDtoRepo.persist(metricDTOs.subList(metricDTOs.size() - 1, metricDTOs.size()));
        System.out.println("Last MetricDTO: " + metricDTOs.get(metricDTOs.size() - 1));
    }
} catch (Exception e) {
    // Handle exceptions here
}

return metricDTOs;
}
    
        
private boolean isSupportedMetric(String metricName) {
    return Set.of(
        "process.runtime.jvm.threads.count",
        "process.runtime.jvm.system.cpu.utilization",
        "process.runtime.jvm.system.cpu.load_1m",
        "process.runtime.jvm.memory.usage",
        "process.runtime.jvm.memory.limit"
    ).contains(metricName);
}

private boolean isMemoryMetric(String metricName) {
    return Set.of("process.runtime.jvm.memory.usage", "process.runtime.jvm.memory.limit").contains(metricName);
}

private boolean isCpuMetric(String metricName) {
    return Set.of("process.runtime.jvm.cpu.utilization","process.runtime.jvm.system.cpu.utilization").contains(metricName);
}

private String getServiceName(ResourceMetric resourceMetric){
    return resourceMetric
    .getResource()
    .getAttributes()
    .stream()
    .filter(attribute -> "service.name".equals(attribute.getKey()))
    .findFirst()
    .map(attribute -> attribute.getValue().getStringValue())
    .orElse(null);
}

private Date convertUnixNanoToLocalDateTime(String startTimeUnixNano) {
    long nanoValue = Long.parseLong(startTimeUnixNano);
    
    // Convert Unix Nano timestamp to Instant
    Instant instant = Instant.ofEpochMilli(nanoValue / 1_000_000);
    
    // Convert Instant to Date
    Date date = Date.from(instant);
    
    // Return the Date object
    return date;
}
}