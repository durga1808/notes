package com.zaga.handler;


import com.zaga.entity.kepler.KeplerMetric;
import com.zaga.repo.KeplerMetricRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class KeplerMetricCommandHandler {
    
    @Inject
    KeplerMetricRepo keplerMetricRepo;

    public void createKeplerMetric(KeplerMetric metric) {
        keplerMetricRepo.persist(metric);
        // List<KeplerMetricDTO> metricDTOs = extractAndMapData(metric);
        // System.out.println("---------MetricDTOs:---------- " + metricDTOs.size());
     }

}
