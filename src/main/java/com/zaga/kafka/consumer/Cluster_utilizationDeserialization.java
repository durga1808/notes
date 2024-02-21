package com.zaga.kafka.consumer;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaga.entity.cluster_utilization.OtelCluster_utilization;

public class Cluster_utilizationDeserialization implements Deserializer<OtelCluster_utilization>{
      private final ObjectMapper objectMapper;

      public Cluster_utilizationDeserialization(){
        this.objectMapper=new ObjectMapper();
      }
      
 @Override
    public OtelCluster_utilization deserialize(String topic, byte[] data) {
      try {
         return objectMapper.readValue(data, OtelCluster_utilization.class);
       } catch (Exception e) {
          throw new RuntimeException("Error deserializing JSON", e);
       }
      }
}
