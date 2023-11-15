package com.zaga.kafka.consumer;

import com.zaga.entity.otelmetric.OtelMetric;
import com.zaga.handler.MetricCommandHandler;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

public class MetricConsumerService {

  @Inject
  MetricCommandHandler metricCommandHandler;

  @Incoming("metric-in")
  public void consumeMetricDetails(OtelMetric metrics) {
    // System.out.println("consumer++++++++++++++" + metrics);
    metricCommandHandler.createMetricProduct(metrics);
  }
}
