package com.zaga.handler;

import com.zaga.entity.clusterutilization.OtelClusterUutilization;
import com.zaga.repo.ClusterUtilizationRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ClusterUtilizationHandler {

    @Inject
    ClusterUtilizationRepo cluster_utilizationRepo;
    
    public OtelClusterUutilization createcCluster_utilization (OtelClusterUutilization cluster_utilization) {
        cluster_utilizationRepo.persist(cluster_utilization);
        return cluster_utilization;
    }
}
