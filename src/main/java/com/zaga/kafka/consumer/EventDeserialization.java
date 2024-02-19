package com.zaga.kafka.consumer;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaga.entity.otelevent.OtelEvents;

public class EventDeserialization implements Deserializer<OtelEvents>{

     private final ObjectMapper objectMapper;

   public EventDeserialization(){
    this.objectMapper= new ObjectMapper();
   }


    @Override
    public OtelEvents deserialize(String topic, byte[] data) {
      try {
         return objectMapper.readValue(data, OtelEvents.class);
       } catch (Exception e) {
          throw new RuntimeException("Error deserializing JSON", e);
       }
      
    }
    
}
