package com.zaga.kafka.consumer;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.zaga.entity.kepler.KeplerMetric;
import com.zaga.handler.KeplerMetricCommandHandler;

import jakarta.inject.Inject;

public class KeplerConsumerService {
    
    @Inject
    KeplerMetricCommandHandler keplerMetricCommandHandler;

    // @Incoming("kepler-in")
    public void consumeKeplerDetails(KeplerMetric keplerMetric) {
        // System.out.println("Received message: " + keplerMetric); 
    
        if (keplerMetric != null) {
            System.out.println("consumer++++++++++++++" + keplerMetric);
            keplerMetricCommandHandler.createKeplerMetric(keplerMetric);
        } else {
            System.out.println("Received null message. Check serialization/deserialization.");
        }
    }
    
}
