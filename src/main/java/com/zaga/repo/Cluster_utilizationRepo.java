package com.zaga.repo;

import com.zaga.entity.cluster_utilization.OtelCluster_utilization;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Cluster_utilizationRepo implements PanacheMongoRepository<OtelCluster_utilization> {
    
}
