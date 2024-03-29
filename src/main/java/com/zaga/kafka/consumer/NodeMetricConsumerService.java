package com.zaga.kafka.consumer;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.zaga.entity.node.OtelNode;
import com.zaga.handler.NodeCommandHandler;
import com.zaga.repo.NodeMetricRepo;
import jakarta.inject.Inject;

public class NodeMetricConsumerService {
    
    
    @Inject
    NodeMetricRepo podCommandRepo;

    @Inject
    NodeCommandHandler nodeCommandHandler;

  @Incoming("node-in")
  public void consumePodMetricDetails(OtelNode podMetrics) {
    System.out.println("consumed podmetric data----------------"+podMetrics);
    if (podMetrics != null) {
      nodeCommandHandler.createNodeMetric(podMetrics);

    } else {
      System.out.println("Received null message. Check serialization/deserialization.");
    }
  }

}
