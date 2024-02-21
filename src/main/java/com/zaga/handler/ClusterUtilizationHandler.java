package com.zaga.handler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.zaga.entity.clusterutilization.OtelClusterUutilization;
import com.zaga.entity.clusterutilization.ResourceMetric;
import com.zaga.entity.clusterutilization.scopeMetric.Metric;
import com.zaga.entity.clusterutilization.scopeMetric.MetricGauge;
import com.zaga.entity.clusterutilization.scopeMetric.gauge.GaugeDataPoint;
import com.zaga.entity.node.OtelNode;
import com.zaga.entity.node.ResourceMetrics;
import com.zaga.entity.node.ScopeMetric;
import com.zaga.entity.node.scopeMetrics.Metrics;
import com.zaga.entity.node.scopeMetrics.gauge.Gauge;
import com.zaga.entity.node.scopeMetrics.gauge.GaugeDataPoints;
import com.zaga.entity.queryentity.cluster_utilization.ClusterUtilizationDTO;
import com.zaga.entity.queryentity.cluster_utilization.ClusterUtilizationDTO;
import com.zaga.repo.ClusterUtilizationDTORepo;
import com.zaga.repo.ClusterUtilizationRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ClusterUtilizationHandler {

    @Inject
    ClusterUtilizationRepo cluster_utilizationRepo;

    @Inject
    ClusterUtilizationDTORepo clusterUtilizationDTORepo;
    
    // public OtelClusterUutilization createCluster_utilization (OtelClusterUutilization cluster_utilization) {
    //     cluster_utilizationRepo.persist(cluster_utilization);
    //     return cluster_utilization;
    // }


    public void createCluster_utilization(OtelClusterUutilization cluster_utilization) {
        cluster_utilizationRepo.persist(cluster_utilization);

        List<ClusterUtilizationDTO> metricDTOs = extractAndMapClusterData(cluster_utilization);
        System.out.println("------------------------------------------NodeMetricDTOs:-------------------------------------- " + metricDTOs.size());
    }

    public List<ClusterUtilizationDTO> extractAndMapClusterData(OtelClusterUutilization cluster_utilization) {
        List<ClusterUtilizationDTO> clusterDTOs = new ArrayList<>();
    
        try {
            for (ResourceMetric resourceMetric : cluster_utilization.getResourceMetrics()) {
                String nodeName = getNodeName(resourceMetric);
                if (nodeName != null) {
                    // NodeMetricDTO nodeMetricDTO = new NodeMetricDTO();
                  
    
                    for (com.zaga.entity.clusterutilization.ScopeMetric scopeMetric : resourceMetric.getScopeMetrics()) {
                        Date createdTime = null;
                        String cpuUsage = null;
                        Long memoryUsage = 0L;
    
                        String name = scopeMetric.getScope().getName();
    
                        if (name != null && name.contains("otelcol/kubeletstatsreceiver")) {
                            List<Metric> metricsList = scopeMetric.getMetrics();
    
                            for (Metric metric : metricsList) {
                                String metricName = metric.getName();
    
                                if (metric.getGauge() != null) {
                                    MetricGauge metricGauge = metric.getGauge();
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
    
                        ClusterUtilizationDTO clusterDTO = new ClusterUtilizationDTO();
                        clusterDTO.setNodeName(nodeName);
                        clusterDTO.setDate(createdTime != null ? createdTime : new Date());
                        clusterDTO.setMemoryUsage(memoryUsage / (1024 * 1024));
                        clusterDTO.setCpuUsage(cpuUsage != null ? cpuUsage : 0.0);
    
                        clusterDTOs.add(clusterDTO);
                        
                    }
                }
            }
            clusterUtilizationDTORepo.persist(clusterDTOs);
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return clusterDTOs;
    }
    private boolean isMemoryMetric(String metricName) {
        return Set.of("k8s.node.memory.usage").contains(metricName);
    }

    private boolean isCpuMetric(String metricName) {
        return Set.of("k8s.node.cpu.utilization").contains(metricName);
    }

    private String getNodeName(ResourceMetric resourceMetric) {
        return resourceMetric
                .getResource()
                .getAttributes()
                .stream()
                .filter(attribute -> "k8s.node.name".equals(attribute.getKey()))
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
