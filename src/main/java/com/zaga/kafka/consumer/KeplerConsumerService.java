package com.zaga.kafka.consumer;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.zaga.entity.kepler.KeplerMetric;
import com.zaga.entity.queryentity.kepler.KeplerMetricDTO;
import com.zaga.handler.KeplerMetricCommandHandler;

import jakarta.inject.Inject;

import com.google.gson.Gson;

public class KeplerConsumerService {
    
    @Inject
    KeplerMetricCommandHandler keplerMetricCommandHandler;

    @Incoming("kepler-in")
    public void consumeKeplerDetails(KeplerMetric keplerMetric) {
         
        if (keplerMetric != null) {
            System.out.println("consumer++++++++++++++" + keplerMetric);
            keplerMetricCommandHandler.createKeplerMetric(keplerMetric);
        } else {
            System.out.println("Received null message. Check serialization/deserialization.");
        }
    }
    // public static void main (String [] ar){


    //         Gson gson = new Gson();
    
           

    //         try (Reader reader1 = new FileReader("D:/observai/newone/kepler/observAi-backend/src/main/java/com/zaga/kafka/consumer/keplerdata.json")) {

    //             KeplerMetric keplerMetric = gson.fromJson(reader1, KeplerMetric.class);

			
    //             KeplerMetricCommandHandler keplerMetricCommandHandler = new KeplerMetricCommandHandler();

    //             List<KeplerMetricDTO>  keplerMetricDTOlst = keplerMetricCommandHandler.extractAndMapData(keplerMetric);

    //             System.out.println(keplerMetricDTOlst.size());

    //             for ( KeplerMetricDTO keplerMetricDTO : keplerMetricDTOlst){

    //                 System.out.println(keplerMetricDTO.toString());

    //             }
    //             System.out.println(keplerMetricDTOlst.size());


    //            // keplerMetricCommandHandler.createKeplerMetric(keplerMetric);

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
        
    // }
    

    
}
