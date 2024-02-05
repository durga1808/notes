package com.zaga.kafka.consumer;

import com.zaga.entity.pod.OtelPodMetric;

import io.smallrye.common.annotation.Identifier;
import io.smallrye.reactive.messaging.kafka.DeserializationFailureHandler;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.common.header.Headers;

@ApplicationScoped
@Identifier("pod-metric-failure-fallback")
public class PodMetricsDeserializerFailureHandler implements DeserializationFailureHandler<OtelPodMetric>  {
  
    @Override
    public OtelPodMetric handleDeserializationFailure(
      final String topic,
      final boolean isKey,
      final String deserializer,
      final byte[] data,
      final Exception exception,
      final Headers headers
    ) {
      // Log the deserialization failure with the relevant information
      System.err.println("Deserialization failed for message in topic: " + topic);
      System.err.println("Exception: " + exception.getMessage());
  
      // Return null to skip the problematic message and
      // continue processing the next message.
      return null;
    }
}
