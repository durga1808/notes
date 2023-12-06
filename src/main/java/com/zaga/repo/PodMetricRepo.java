package com.zaga.repo;

import com.zaga.entity.podMetric.PodMetric;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PodMetricRepo implements PanacheMongoRepository<PodMetric>{
    
}