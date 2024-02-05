package com.zaga.repo;

import com.zaga.entity.otelmetric.OtelMetric;
import com.zaga.entity.pod.OtelPodMetric;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PodCommandRepo implements PanacheMongoRepository<OtelPodMetric> {
    
}
