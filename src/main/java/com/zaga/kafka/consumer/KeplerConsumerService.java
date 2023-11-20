package com.zaga.kafka.consumer;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.kafka.common.metrics.Gauge;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.zaga.entity.kepler.KeplerMetric;
import com.zaga.entity.otelmetric.ResourceMetric;
import com.zaga.entity.otelmetric.ScopeMetric;
import com.zaga.entity.otelmetric.scopeMetric.Metric;
import com.zaga.entity.otelmetric.scopeMetric.MetricGauge;
import com.zaga.entity.otelmetric.scopeMetric.MetricHistogram;
import com.zaga.entity.otelmetric.scopeMetric.MetricSum;
import com.zaga.entity.otelmetric.scopeMetric.sum.SumDataPoint;
import com.zaga.entity.otelmetric.scopeMetric.sum.SumDataPointAttribute;
import com.zaga.entity.queryentity.metrics.KeplerMetricDTO;
import com.zaga.handler.KeplerMetricCommandHandler;

import jakarta.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class KeplerConsumerService {
    
    @Inject
    KeplerMetricCommandHandler keplerMetricCommandHandler;

    @Incoming("kepler-in")
    public void consumeKeplerDetails(KeplerMetric keplerMetric) {
        // System.out.println("Received message: " + keplerMetric); 
    
        if (keplerMetric != null) {
            System.out.println("consumer++++++++++++++" + keplerMetric);
            keplerMetricCommandHandler.createKeplerMetric(keplerMetric);
        } else {
            System.out.println("Received null message. Check serialization/deserialization.");
        }
    }
    public static void main (String [] ar){


            Gson gson = new Gson();
    
           

            try (Reader reader1 = new FileReader("/Users/jpaulraj/zaga/observAi-backend/src/main/java/com/zaga/kafka/consumer/keplerdata.json")) {

                KeplerMetric keplerMetric = gson.fromJson(reader1, KeplerMetric.class);

			
                KeplerMetricCommandHandler keplerMetricCommandHandler = new KeplerMetricCommandHandler();

                List<KeplerMetricDTO>  keplerMetricDTOlst = keplerMetricCommandHandler.extractAndMapData(keplerMetric);

                System.out.println(keplerMetricDTOlst.size());

                for ( KeplerMetricDTO keplerMetricDTO : keplerMetricDTOlst){

                    System.out.println(keplerMetricDTO.toString());

                }
                System.out.println(keplerMetricDTOlst.size());


               // keplerMetricCommandHandler.createKeplerMetric(keplerMetric);

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    


    
}
