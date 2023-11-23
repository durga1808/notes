package com.zaga.kafka.consumer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
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
            System.out.println("consumed Kepler data ------------------");
            keplerMetricCommandHandler.createKeplerMetric(keplerMetric);
        } else {
            System.out.println("Received null message. Check serialization/deserialization.");
        }
    }

    public static void main(String[] ar) throws URISyntaxException {

        Gson gson = new Gson();

        File file = new File("./src/main/java/com/zaga/kafka/consumer/keplerdata.json");

        try (Reader reader1 = new FileReader(file)) {

            KeplerMetric keplerMetric = gson.fromJson(reader1, KeplerMetric.class);

            KeplerMetricCommandHandler keplerMetricCommandHandler = new KeplerMetricCommandHandler();

            List<KeplerMetricDTO> keplerMetricDTOlst = keplerMetricCommandHandler.extractAndMapData(keplerMetric);

            System.out.println(keplerMetricDTOlst.size());

            for (KeplerMetricDTO keplerMetricDTO : keplerMetricDTOlst) {

                System.out.println(keplerMetricDTO.toString());

            }
            System.out.println(keplerMetricDTOlst.size());

            // keplerMetricCommandHandler.createKeplerMetric(keplerMetric);

        } catch (IOException e) {
            System.out.println("ERROR " + e.getLocalizedMessage());
        }

    }

}
