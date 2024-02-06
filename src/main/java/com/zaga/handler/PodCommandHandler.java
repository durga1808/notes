package com.zaga.handler;

import com.zaga.entity.pod.OtelPodMetric;
import com.zaga.entity.pod.ResourceMetric;
import com.zaga.entity.pod.ScopeMetrics;
import com.zaga.entity.pod.scopeMetric.Metric;
import com.zaga.entity.pod.scopeMetric.gauge.Gauge;
import com.zaga.entity.pod.scopeMetric.gauge.GaugeDataPoint;
import com.zaga.entity.queryentity.pod.MetricDTO;
import com.zaga.entity.queryentity.pod.PodMetricDTO;
import com.zaga.repo.PodCommandRepo;
import com.zaga.repo.PodMetricDTORepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class PodCommandHandler {

  @Inject
  PodCommandRepo podCommandRepo;

  @Inject
  PodMetricDTORepo podMetricDTORepo;

  public void createPodMetric(OtelPodMetric metrics) {
    podCommandRepo.persist(metrics);

    Map<String, PodMetricDTO> metricDTOs = extractAndMapData(metrics);
    System.out.println("------------------------------------------PODMetricDTOs:-------------------------------------- " + metricDTOs);
  }


public Map<String, PodMetricDTO> extractAndMapData(OtelPodMetric metrics) {
    Map<String, PodMetricDTO> podMetricsMap = new HashMap<>();

    try {
        for (ResourceMetric resourceMetric : metrics.getResourceMetrics()) {
            String podName = getPodName(resourceMetric);
            if (podName != null) {
                PodMetricDTO podMetricDTO = podMetricsMap.computeIfAbsent(podName, k -> new PodMetricDTO());
                podMetricDTO.setPodName(podName);

                List<MetricDTO> metricDTOs = podMetricDTO.getMetrics();

                for (ScopeMetrics scopeMetric : resourceMetric.getScopeMetrics()) {
                    Date createdTime = null;
                    Double cpuUsage = null;
                    Long memoryUsage = 0L;

                    String name = scopeMetric.getScope().getName();

                    if (name != null && name.contains("otelcol/kubeletstatsreceiver")) {
                        List<Metric> metricsList = scopeMetric.getMetrics();

                        for (Metric metric : metricsList) {
                            String metricName = metric.getName();

                            if (metric.getGauge() != null) {
                                Gauge metricGauge = metric.getGauge();
                                List<GaugeDataPoint> gaugeDataPoints = metricGauge.getDataPoints();

                                for (GaugeDataPoint gaugeDataPoint : gaugeDataPoints) {
                                    String startTimeUnixNano = gaugeDataPoint.getTimeUnixNano();
                                    createdTime = convertUnixNanoToLocalDateTime(startTimeUnixNano);

                                    if (isCpuMetric(metricName)) {
                                        cpuUsage = gaugeDataPoint.getAsDouble();
                                    }

                                    // Assuming the memory metric is also present in GaugeDataPoint, adjust as needed
                                    String memoryValue = gaugeDataPoint.getAsInt();
                                    if (isMemoryMetric(metricName)) {
                                        long currentMemoryUsage = Long.parseLong(memoryValue);
                                        memoryUsage += currentMemoryUsage;
                                    }
                                }
                            }
                        }
                    }
                

                MetricDTO metricDTO = new MetricDTO();
                metricDTO.setDate(createdTime != null ? createdTime : new Date()); // Provide a default value
                metricDTO.setMemoryUsage(memoryUsage / (1024 * 1024)); // Convert to MB
                metricDTO.setCpuUsage(cpuUsage != null ? cpuUsage : 0.0); // Provide a default value

                metricDTOs.add(metricDTO);
            }
        }
    }

        List<PodMetricDTO> aggregatedPodMetrics = new ArrayList<>(podMetricsMap.values());
        podMetricDTORepo.persist(aggregatedPodMetrics);
        System.out.println("Aggregated-----------------------"+aggregatedPodMetrics);
        
    } catch (Exception e) {
        e.printStackTrace();
    }

    return podMetricsMap;
}













 
//   public List<PodMetricDTO> extractAndMapData(OtelPodMetric metrics) {
//     List<PodMetricDTO> podMetricDTOs = new ArrayList<>();

//     try {
//         Map<String, PodMetricDTO> podMetricsMap = new HashMap<>();

//         for (ResourceMetric resourceMetric : metrics.getResourceMetrics()) {
//             String podName = getPodName(resourceMetric);
//             if (podName != null) {
//                 PodMetricDTO podMetricDTO = podMetricsMap.computeIfAbsent(podName, k -> new PodMetricDTO());
//                 podMetricDTO.setPodName(podName);

//                 List<MetricDTO> metricDTOs = podMetricDTO.getMetrics();

//                 for (ScopeMetrics scopeMetric : resourceMetric.getScopeMetrics()) {
//                     Date createdTime = null;
//                     Double cpuUsage = null;
//                     Long memoryUsage = 0L;

//                     String name = scopeMetric.getScope().getName();

//                     if (name != null && name.contains("otelcol/kubeletstatsreceiver")) {
//                         List<Metric> metricsList = scopeMetric.getMetrics();

//                         for (Metric metric : metricsList) {
//                             String metricName = metric.getName();

//                             if (metric.getGauge() != null) {
//                                 Gauge metricGauge = metric.getGauge();
//                                 List<GaugeDataPoint> gaugeDataPoints = metricGauge.getDataPoints();

//                                 for (GaugeDataPoint gaugeDataPoint : gaugeDataPoints) {
//                                     String startTimeUnixNano = gaugeDataPoint.getTimeUnixNano();
//                                     createdTime = convertUnixNanoToLocalDateTime(startTimeUnixNano);

//                                     if (isCpuMetric(metricName)) {
//                                         cpuUsage = gaugeDataPoint.getAsDouble();
//                                     }

//                                     // Assuming the memory metric is also present in GaugeDataPoint, adjust as needed
//                                     String memoryValue = gaugeDataPoint.getAsInt();
//                                     if (isMemoryMetric(metricName)) {
//                                         long currentMemoryUsage = Long.parseLong(memoryValue);
//                                         memoryUsage += currentMemoryUsage;
//                                     }
//                                 }
//                             }

//                             Long memoryUsageInMb = memoryUsage / (1024 * 1024);

//                             MetricDTO metricDTO = new MetricDTO();
//                             metricDTO.setDate(createdTime);
//                             metricDTO.setMemoryUsage(memoryUsageInMb);
//                             metricDTO.setCpuUsage(cpuUsage);

//                             metricDTOs.add(metricDTO);
//                         }
//                     }
//                 }
//             }
//         }

//         podMetricDTOs.addAll(podMetricsMap.values());
//         podMetricDTORepo.persist(podMetricDTOs);
//     } catch (Exception e) {
//         e.printStackTrace(); // Handle the exception appropriately
//     }

//     return podMetricDTOs;
// }
 



// s
  //     private boolean isSupportedMetric(String metricName) {
  //     return Set.of(
  //         "otelcol/kubeletstatsreceiver").contains(metricName);
  // }

  private boolean isMemoryMetric(String metricName) {
    return Set.of("k8s.pod.memory.usage").contains(metricName);
  }

  private boolean isCpuMetric(String metricName) {
    return Set.of("k8s.pod.cpu.utilization").contains(metricName);
  }

  private String getPodName(ResourceMetric resourceMetric) {
    return resourceMetric
      .getResource()
      .getAttributes()
      .stream()
      .filter(attribute -> "k8s.pod.name".equals(attribute.getKey()))
      .findFirst()
      .map(attribute -> attribute.getValue().getStringValue())
      .orElse(null);
  }

  private static Date convertUnixNanoToLocalDateTime(String startTimeUnixNano) {
    long observedTimeMillis = Long.parseLong(startTimeUnixNano) / 1_000_000;

    Instant instant = Instant.ofEpochMilli(observedTimeMillis);

    ZoneId istZone = ZoneId.of("Asia/Kolkata");
    LocalDateTime istDateTime = LocalDateTime.ofInstant(instant, istZone);

    return Date.from(istDateTime.atZone(istZone).toInstant());
  }
}
