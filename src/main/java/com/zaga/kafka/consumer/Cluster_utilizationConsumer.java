package com.zaga.kafka.consumer;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.zaga.entity.cluster_utilization.OtelCluster_utilization;
import com.zaga.handler.Cluster_utilizationHandler;

import jakarta.inject.Inject;

public class Cluster_utilizationConsumer {
    @Inject
    private Cluster_utilizationHandler cluster_utilizationHandler;

     @Incoming("cluser_utilization-in")
      public void consumeCluster_utilizationDetails(OtelCluster_utilization cluster_utilization) {
        System.out.println("consumed cluster_utilization -----------");
        cluster_utilizationHandler.createcCluster_utilization(cluster_utilization);
     }
}
