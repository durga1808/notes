package com.zaga.handler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JacksonException;
import com.zaga.entity.otelmetric.OtelMetric;
import com.zaga.entity.otelmetric.ResourceMetric;
import com.zaga.entity.otelmetric.ScopeMetric;
import com.zaga.entity.otelmetric.scopeMetric.Metric;
import com.zaga.entity.otelmetric.scopeMetric.MetricGauge;
import com.zaga.entity.otelmetric.scopeMetric.MetricHistogram;
import com.zaga.entity.otelmetric.scopeMetric.MetricSum;
import com.zaga.entity.otelmetric.scopeMetric.gauge.GaugeDataPoint;
import com.zaga.entity.otelmetric.scopeMetric.histogram.HistogramDataPoint;
import com.zaga.entity.otelmetric.scopeMetric.sum.SumDataPoint;
import com.zaga.entity.queryentity.metrics.MetricDTO;
import com.zaga.entity.queryentity.trace.TraceDTO;
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
        System.out.println("MetricDTOs: " + metricDTOs);
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
                                MetricDTO metricDTO = new MetricDTO();

                                if (metric.getSum() != null) {
                                    MetricSum metricSum = metric.getSum();
                                    List<SumDataPoint> sumDataPoints = metricSum.getDataPoints();
                                    Long value = 0L;

                                    for (SumDataPoint sumDataPoint : sumDataPoints) {
                                        Long memoryUsage = 0L;
                                        String startTimeUnixNano = sumDataPoint.getStartTimeUnixNano();
                                        LocalDateTime createdTime = convertUnixNanoToLocalDateTime(startTimeUnixNano);
                                        System.out.println("---serviceName:-- " + serviceName);
                                        System.out.println("---createdTime:-- " + createdTime);

                                        if (
                                            metricName.equals("process.runtime.jvm.memory.usage")
                                            || metricName.equals("process.runtime.jvm.memory.limit")
                                        ) 
                                            if (sumDataPoint.getAsInt() != null && !sumDataPoint.getAsInt().isEmpty()) {
                                                String asInt = sumDataPoint.getAsInt();
                                                memoryUsage += Long.parseLong(asInt);
                                            }
                                            metricDTO.setMemoryUsage(memoryUsage);
                                        }

                                    } else if (metric.getHistogram() != null) {
                                    MetricHistogram metricHistogram = metric.getHistogram();
                                    List<HistogramDataPoint> histogramDataPoints = metricHistogram.getDataPoints();
                                    System.out.println("HistogramDataPoints: " + histogramDataPoints);
                                } else if (metric.getGauge() != null) {
                                   MetricGauge metricGauge = metric.getGauge();
                                    List<GaugeDataPoint> gaugeDataPoints = metricGauge.getDataPoints();
                                    System.out.println("GaugeDataPoint: " + gaugeDataPoints);
                                } else {
                                    System.out.println("No specific metric type found for: " + metricName);
                                }
                                
                                metricDTOs.add(metricDTO);
                            }
                        }
                    }
                } catch (NullPointerException e) {
                }
            }
        }
        } catch (Exception e) {
    }
    if (!metricDTOs.isEmpty()) {
            metricDtoRepo.persist(metricDTOs);
      
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