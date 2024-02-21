package com.zaga.kafka.consumer;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.zaga.entity.clusterutilization.OtelClusterUutilization;
import com.zaga.handler.ClusterUtilizationHandler;

import jakarta.inject.Inject;

public class ClusterUtilizationConsumer {
    @Inject
    private ClusterUtilizationHandler cluster_utilizationHandler;

     @Incoming("cluser_utilization-in")
      public void consumeCluster_utilizationDetails(OtelClusterUutilization cluster_utilization) {
        System.out.println("consumed cluster_utilization -----------");
        cluster_utilizationHandler.createcCluster_utilization(cluster_utilization);
     }
}
