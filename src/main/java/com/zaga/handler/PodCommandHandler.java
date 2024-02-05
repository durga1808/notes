// package com.zaga.handler;

// import java.time.Instant;
// import java.time.LocalDateTime;
// import java.time.ZoneId;
// import java.util.ArrayList;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;

// import com.zaga.entity.MetricDTO;
// import com.zaga.entity.PodMetricDTO;
// import com.zaga.entity.auth.ServiceListNew;
// import com.zaga.entity.otelmetric.OtelMetric;
// import com.zaga.entity.otelmetric.ResourceMetric;
// import com.zaga.entity.otelmetric.ScopeMetric;
// import com.zaga.entity.otelmetric.scopeMetric.Metric;
// import com.zaga.entity.otelmetric.scopeMetric.MetricGauge;
// import com.zaga.entity.otelmetric.scopeMetric.MetricSum;
// import com.zaga.entity.otelmetric.scopeMetric.gauge.GaugeDataPoint;
// import com.zaga.entity.otelmetric.scopeMetric.sum.SumDataPoint;
// // import com.zaga.entity.queryentity.metrics.MetricDTO;
// import com.zaga.repo.PodCommandRepo;
// import com.zaga.repo.PodMetricDTORepo;

// import jakarta.inject.Inject;

// public class PodCommandHandler {

//     @Inject
//     PodCommandRepo podCommandRepo;

//     @Inject 
//     PodMetricDTORepo podMetricDTORepo;

//         public void createPodMetric(OtelMetric metrics) {
//             podCommandRepo.persist(metrics);

//         List<PodMetricDTO> metricDTOs = extractAndMapData(metrics);
//                 System.out.println("---------MetricDTOs:---------- " + metricDTOs.size());
//     }
//     public List<PodMetricDTO> extractAndMapData(OtelMetric metrics) {
//         List<PodMetricDTO> podMetricDTOs = new ArrayList<>();

//         try {
//             Map<String, PodMetricDTO> podMetricsMap = new HashMap<>();
    
//             for (ResourceMetric resourceMetric : metrics.getResourceMetrics()) {
//                 String podName = getPodName(resourceMetric);
//                 if (podName != null) {
//                     PodMetricDTO podMetricDTO = podMetricsMap.get(podName);
//                     if (podMetricDTO == null) {
//                         podMetricDTO = new PodMetricDTO();
//                         podMetricDTO.setPodName(podName);
//                         podMetricsMap.put(podName, podMetricDTO);
//                     }

//                     for (ScopeMetric scopeMetric : resourceMetric.getScopeMetrics()) {
//                         Date createdTime = null;
//                         Double cpuUsage = null;
//                         Integer memoryUsage = 0;
    
//                         String name = scopeMetric.getScope().getName();
    
//                         if (name != null && name.contains("otelcol/kubeletstatsreceiver")) {
//                             List<Metric> metricsList = scopeMetric.getMetrics();
    
//                             for (Metric metric : metricsList) {
//                                 String metricName = metric.getName();
    
//                                 if (metric.getGauge() != null) {
//                                     MetricGauge metricGauge = metric.getGauge();
//                                     List<GaugeDataPoint> gaugeDataPoints = metricGauge.getDataPoints();
                                
//                                     for (GaugeDataPoint gaugeDataPoint : gaugeDataPoints) {
//                                         String asDouble = gaugeDataPoint.getAsDouble();
//                                         System.out.println("--------asDOUBLE------" + asDouble);
//                                         cpuUsage = Double.parseDouble(asDouble);
//                                         System.out.println("--------cpuUsage-------" + cpuUsage);
                                
//                                         // Assuming the memory metric is also present in GaugeDataPoint, adjust as needed
//                                         String memoryValue = gaugeDataPoint.getAsInt();
//                                         if (memoryValue != null && !memoryValue.isEmpty()) {
//                                             int currentMemoryUsage = Integer.parseInt(memoryValue);
//                                             System.out.println("--------Memory usage:----- " + currentMemoryUsage);
//                                             memoryUsage += currentMemoryUsage;
//                                         }
//                                     }
//                                 }
                                
//                             Integer memoryUsageInMb = memoryUsage / (1024 * 1024);
    
//                             if (podMetricDTO == null) {
//                                 podMetricDTO = new PodMetricDTO();
//                                 podMetricDTO.setPodName(podName);
//                                 podMetricsMap.put(podName, podMetricDTO);
//                             }
                            
//                             MetricDTO metricDTO = new MetricDTO();
//                             metricDTO.setDate(createdTime);
//                             metricDTO.setMemoryUsage(memoryUsageInMb);
//                             metricDTO.setCpuUsage(cpuUsage);
                            
//                             podMetricDTO.getMetrics().add(metricDTO);
    
//                          }
//                     }
//                 }
//             }
//         }
//     }
//          catch (Exception e) {
//             e.printStackTrace(); // Handle the exception appropriately
//         }
    
//         return podMetricDTOs;
//     }
    
//     private PodMetricDTO findOrCreatePodMetricDTO(List<PodMetricDTO> podMetricDTOs, String podName) {
//         for (PodMetricDTO podMetricDTO : podMetricDTOs) {
//             if (podMetricDTO.getPodName().equals(podName)) {
//                 return podMetricDTO;
//             }
//         }
    
//         PodMetricDTO newPodMetricDTO = new PodMetricDTO();
//         newPodMetricDTO.setPodName(podName);
//         podMetricDTOs.add(newPodMetricDTO);
    
//         return newPodMetricDTO;
//     }
    

//     //     private boolean isSupportedMetric(String metricName) {
//     //     return Set.of(
//     //         "otelcol/kubeletstatsreceiver").contains(metricName);
//     // }

//     private boolean isMemoryMetric(String metricName) {
//         return Set.of("k8s.pod.memory.usage").contains(metricName);
//     }

//     private boolean isCpuMetric(String metricName) {
//         return Set.of("k8s.pod.cpu.utilization")
//                 .contains(metricName);
//     }

//     private String getPodName(ResourceMetric resourceMetric) {
//         return resourceMetric
//                 .getResource()
//                 .getAttributes()
//                 .stream()
//                 .filter(attribute -> "k8s.pod.name".equals(attribute.getKey()))
//                 .findFirst()
//                 .map(attribute -> attribute.getValue().getStringValue())
//                 .orElse(null);
//     }


//         private static Date convertUnixNanoToLocalDateTime(String startTimeUnixNano) {
//         long observedTimeMillis = Long.parseLong(startTimeUnixNano) / 1_000_000;

//         Instant instant = Instant.ofEpochMilli(observedTimeMillis);

//         ZoneId istZone = ZoneId.of("Asia/Kolkata");
//         LocalDateTime istDateTime = LocalDateTime.ofInstant(instant, istZone);

//         return Date.from(istDateTime.atZone(istZone).toInstant());



// }
// }
