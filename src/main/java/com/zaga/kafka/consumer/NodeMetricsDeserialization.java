package com.zaga.kafka.consumer;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaga.entity.node.OtelNode;

public class NodeMetricsDeserialization implements Deserializer<OtelNode>{
             
   private final ObjectMapper objectMapper;

   public NodeMetricsDeserialization(){
    this.objectMapper= new ObjectMapper();
   }
    @Override
    public OtelNode deserialize(String topic, byte[] data) {
         try {
         return objectMapper.readValue(data, OtelNode.class);
       } catch (Exception e) {
          throw new RuntimeException("Error deserializing JSON", e);
       }
    }
    
}
