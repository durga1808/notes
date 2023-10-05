package com.zaga.handler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
        // System.out.println("MetricDTOs: " + metricDTOs);
    }

    // private List<MetricDTO> extractAndMapData(OtelMetric metrics) {
    //     List<MetricDTO> metricDTOs = new ArrayList<>();
    //     try {
    //         for (ResourceMetric resourceMetric : metrics.getResourceMetrics()) {
    //             String serviceName = getServiceName(resourceMetric);
    //             for (ScopeMetric scopeMetric : resourceMetric.getScopeMetrics()) {
    //                     String name = scopeMetric.getScope().getName();
    //                     if (name != null && name.contains("io.opentelemetry.runtime")) {
    //                         List<Metric> metricsList = scopeMetric.getMetrics();
    //                         for (Metric metric : metricsList) {
    //                             String metricName = metric.getName();
    //                             if (
    //                                 metricName.equals("process.runtime.jvm.threads.count")
    //                                 || metricName.equals("process.runtime.jvm.system.cpu.utilization")
    //                                 || metricName.equals("process.runtime.jvm.system.cpu.load_1m")
    //                                 || metricName.equals("process.runtime.jvm.memory.usage")
    //                                 || metricName.equals("process.runtime.jvm.memory.limit")
    //                             ) {
    //                                 LocalDateTime createdTime = null;
    //                                 Long memoryUsage = null; 
    //                                 Long cpuUsage = null; 
    
    //                                 if (metric.getSum() != null) {
    //                                     MetricSum metricSum = metric.getSum();
    //                                     List<SumDataPoint> sumDataPoints = metricSum.getDataPoints();
    
    //                                     for (SumDataPoint sumDataPoint : sumDataPoints) {
    //                                         String startTimeUnixNano = sumDataPoint.getTimeUnixNano();
    //                                         createdTime = convertUnixNanoToLocalDateTime(startTimeUnixNano);
    
    //                                         if (
    //                                             metricName.equals("process.runtime.jvm.memory.usage")
    //                                             || metricName.equals("process.runtime.jvm.memory.limit")
    //                                         ) {
    //                                             if (sumDataPoint.getAsInt() != null && !sumDataPoint.getAsInt().isEmpty()) {
    //                                                 String asInt = sumDataPoint.getAsInt();
    //                                                 memoryUsage = Long.parseLong(asInt);
    //                                             }
    //                                         }
    //                                     }
    //                                     if (metric.getGauge() != null) {
    //                                         MetricGauge metricGauge = metric.getGauge();
    //                                         List<GaugeDataPoint> gaugeDataPoints = metricGauge.getDataPoints();
    
    //                                         for (GaugeDataPoint gaugeDataPoint : gaugeDataPoints) {
    //                                             if (metricName.equals("process.runtime.jvm.cpu.utilization")) {
    //                                                 if (gaugeDataPoint.getAsDouble() != null) {
    //                                                     String asDouble = gaugeDataPoint.getAsDouble();
    //                                                     System.out.println("---cpu------------" + asDouble);
    //                                                     double cpuUsagePercentage = Double.parseDouble(asDouble) * 100;
    //                                                     long roundedCpuUsage = Math.round(cpuUsagePercentage);
    //                                                     cpuUsage = Long.valueOf(roundedCpuUsage);
    
    //                                                     System.out.println("------Calculated CPU Usage:------- " + cpuUsage); // Add this line for debugging
    //                                                 }
    //                                             }
    //                                         }
    //                                     }
    
    //                                     // Move metricDTO creation here, so it uses the updated values
    //                                     MetricDTO metricDTO = new MetricDTO();
    //                                     metricDTO.setMemoryUsage(memoryUsage != null ? memoryUsage : 0L);
    //                                     metricDTO.setDate(createdTime);
    //                                     metricDTO.setServiceName(serviceName);
    //                                     metricDTO.setCpuUsage(cpuUsage != null ? cpuUsage : 0L);
    //                                     metricDTOs.add(metricDTO);
    //                                 }
    //                             }
    //                         }
    //                     }
                   
    //             }
    //         }
    
    //         if (!metricDTOs.isEmpty()) {
    //             // Persist the data
    //             metricDtoRepo.persist(metricDTOs);
    //             System.out.println("MetricDTOs:----------------- " + metricDTOs);
    //         }
    //     } catch (Exception e) {
    //     }
    
    //     return metricDTOs;
    // }
    

    private List<MetricDTO> extractAndMapData(OtelMetric metrics) {
        List<MetricDTO> metricDTOs = new ArrayList<>();
        try {
            for (ResourceMetric resourceMetric : metrics.getResourceMetrics()) {
                String serviceName = getServiceName(resourceMetric);
                for (ScopeMetric scopeMetric : resourceMetric.getScopeMetrics()) {
                    LocalDateTime createdTime = null;
                    Long memoryUsage = null;
                    Long cpuUsage = null;
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
                                                memoryUsage = Long.parseLong(asInt);
                                                System.out.println("--------Memory usage:----- " + memoryUsage);
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
                                                cpuUsage = Long.valueOf(asDouble);
                                                System.out.println("--------cpuUsage-------" + cpuUsage);
                                            }
                                        }
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
            
            if (!metricDTOs.isEmpty()) {
                // Persist the data after all iterations are complete and the list is not empty
                metricDtoRepo.persist(metricDTOs);
                System.out.println("MetricDTOs:----------------- " + metricDTOs);
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

private LocalDateTime convertUnixNanoToLocalDateTime(String startTimeUnixNano) {
    long nanoValue = Long.parseLong(startTimeUnixNano);
    Instant instant = Instant.ofEpochMilli(nanoValue / 1_000_000); 
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
}
}