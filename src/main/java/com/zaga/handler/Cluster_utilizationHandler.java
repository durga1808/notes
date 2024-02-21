package com.zaga.handler;

import com.zaga.entity.cluster_utilization.OtelCluster_utilization;
import com.zaga.repo.Cluster_utilizationRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Cluster_utilizationHandler {

    @Inject
    Cluster_utilizationRepo cluster_utilizationRepo;
    
    public OtelCluster_utilization createcCluster_utilization (OtelCluster_utilization cluster_utilization) {
        cluster_utilizationRepo.persist(cluster_utilization);
        return cluster_utilization;
    }
}
