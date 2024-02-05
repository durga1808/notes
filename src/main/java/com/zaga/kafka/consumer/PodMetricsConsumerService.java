package com.zaga.kafka.consumer;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.zaga.entity.pod.OtelPodMetric;
import com.zaga.handler.PodCommandHandler;

import jakarta.inject.Inject;

public class PodMetricsConsumerService {
  
    @Inject
    PodCommandHandler podCommandHandler;

  @Incoming("pod-in")
  public void consumeMetricDetails(OtelPodMetric podMetrics) {
    System.out.println("consumed metric data----------------");
    if (podMetrics != null) {
      podCommandHandler.createPodMetric(podMetrics);
    } else {
      System.out.println("Received null message. Check serialization/deserialization.");
    }
  }



}
