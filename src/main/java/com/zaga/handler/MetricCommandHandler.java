package com.zaga.handler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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
        // System.out.println("MetricDTOs: " + metricDTOs);
    }

    private List<MetricDTO> extractAndMapData(OtelMetric metrics) {
        List<MetricDTO> metricDTOs = new ArrayList<>();
        try {
            for (ResourceMetric resourceMetric : metrics.getResourceMetrics()) {
                String serviceName = getServiceName(resourceMetric);
                for (ScopeMetric scopeMetric : resourceMetric.getScopeMetrics()) {
                    try {
                        String name = scopeMetric.getScope().getName();
                        if (name != null && name.contains("io.opentelemetry.runtime")) {
                            List<Metric> metricsList = scopeMetric.getMetrics();
                            for (Metric metric : metricsList) {
                                String metricName = metric.getName();
                                if (
                                    metricName.equals("process.runtime.jvm.threads.count")
                                    || metricName.equals("process.runtime.jvm.system.cpu.utilization")
                                    || metricName.equals("process.runtime.jvm.system.cpu.load_1m")
                                    || metricName.equals("process.runtime.jvm.memory.usage")
                                    || metricName.equals("process.runtime.jvm.memory.limit")
                                ) {
                                    LocalDateTime createdTime = null; 
                                    Long memoryUsage = 0L;
                                    Long cpuUsage = 0L; 
        
                                    if (metric.getSum() != null) {
                                        MetricSum metricSum = metric.getSum();
                                        List<SumDataPoint> sumDataPoints = metricSum.getDataPoints();
        
                                        for (SumDataPoint sumDataPoint : sumDataPoints) {
                                            String startTimeUnixNano = sumDataPoint.getTimeUnixNano();
                                            createdTime = convertUnixNanoToLocalDateTime(startTimeUnixNano);
        
                                            if (
                                                metricName.equals("process.runtime.jvm.memory.usage")
                                                || metricName.equals("process.runtime.jvm.memory.limit")
                                            ) {
                                                if (sumDataPoint.getAsInt() != null && !sumDataPoint.getAsInt().isEmpty()) {
                                                    String asInt = sumDataPoint.getAsInt();
                                                    memoryUsage += Long.parseLong(asInt);
                                                }
                                            }
                                        }
                                        if (metric.getGauge() != null) {
                                            MetricGauge metricGauge = metric.getGauge();
                                            List<GaugeDataPoint> gaugeDataPoints = metricGauge.getDataPoints();
                                        
                                            for (GaugeDataPoint gaugeDataPoint : gaugeDataPoints) {
                                                if (metricName.equals("process.runtime.jvm.cpu.utilization")) {
                                                    if (gaugeDataPoint.getAsDouble() != null) {
                                                        String asDouble = gaugeDataPoint.getAsDouble();
                                                        System.out.println("---cpu------------" + asDouble);
                                                        double cpuUsagePercentage = Double.parseDouble(asDouble) * 100;
                                                        long roundedCpuUsage = Math.round(cpuUsagePercentage);
                                                        cpuUsage = Long.valueOf(roundedCpuUsage);

                                                        System.out.println("------Calculated CPU Usage:------- " + cpuUsage); // Add this line for debugging
                                                    }
                                                }                                            
                                            }
                                        }
                                        
                                        
                                        
                                        MetricDTO metricDTO = new MetricDTO();
                                        metricDTO.setMemoryUsage(memoryUsage);
                                        metricDTO.setDate(createdTime); 
                                        metricDTO.setServiceName(serviceName);
                                        metricDTO.setCpuUsage(cpuUsage); 
                                        metricDTOs.add(metricDTO);
                                    }
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                    }
                }
            }
            
        if (!metricDTOs.isEmpty()) {
            // Persist the data
            metricDtoRepo.persist(metricDTOs);
            System.out.println("MetricDTOs:----------------- " + metricDTOs);
        }
        } catch (Exception e) {
        }
    
        return metricDTOs;
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

private LocalDateTime convertUnixNanoToLocalDateTime(String startTimeUnixNano) {
    long nanoValue = Long.parseLong(startTimeUnixNano);
    Instant instant = Instant.ofEpochMilli(nanoValue / 1_000_000); 
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
}
}