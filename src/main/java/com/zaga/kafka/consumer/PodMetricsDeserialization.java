package com.zaga.kafka.consumer;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaga.entity.pod.OtelPodMetric;

public class PodMetricsDeserialization implements Deserializer<OtelPodMetric> {

   private final ObjectMapper objectMapper;

   public PodMetricsDeserialization(){
    this.objectMapper= new ObjectMapper();
   }
    @Override
    public OtelPodMetric deserialize(String topic, byte[] data) {
         try {
         return objectMapper.readValue(data, OtelPodMetric.class);
       } catch (Exception e) {
          throw new RuntimeException("Error deserializing JSON", e);
       }
    }
    
}
