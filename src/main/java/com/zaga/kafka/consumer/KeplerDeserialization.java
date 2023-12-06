package com.zaga.kafka.consumer;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaga.entity.kepler.KeplerMetric;



public class KeplerDeserialization implements Deserializer<KeplerMetric>  {
    
    private final ObjectMapper objectMapper;

     public KeplerDeserialization(){
    this.objectMapper= new ObjectMapper();
   }
    
   @Override
   public KeplerMetric deserialize(String topic, byte[] data) {
    if (data == null) {
        return null; // or throw an appropriate exception
    }
    try {
        return objectMapper.readValue(data, KeplerMetric.class);
    } catch (Exception e) {
        // Log the exception
        System.out.println("Error deserializing JSON"+e);
        throw new RuntimeException("Error deserializing JSON", e);
    }
}
}
 
  
    