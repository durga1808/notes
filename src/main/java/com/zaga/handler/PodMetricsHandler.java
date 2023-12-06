package com.zaga.handler;

import com.zaga.entity.podMetric.PodMetric;
import com.zaga.repo.PodMetricRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PodMetricsHandler {

    @Inject
    PodMetricRepo podMetricRepo;

       public void createPodMetrics(PodMetric metric) {
        podMetricRepo.persist(metric);
    }
    
}
