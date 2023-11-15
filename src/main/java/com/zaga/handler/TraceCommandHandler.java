package com.zaga.handler;

import com.zaga.entity.oteltrace.OtelTrace;
import com.zaga.entity.oteltrace.ResourceSpans;
import com.zaga.entity.oteltrace.ScopeSpans;
import com.zaga.entity.oteltrace.scopeSpans.Spans;
import com.zaga.entity.oteltrace.scopeSpans.spans.Attributes;
import com.zaga.entity.queryentity.trace.TraceDTO;
import com.zaga.repo.TraceCommandRepo;
import com.zaga.repo.TraceQueryRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class TraceCommandHandler {

  @Inject
  TraceCommandRepo traceCommandRepo;

  @Inject
  TraceQueryRepo traceQueryRepo;

  public void createTraceProduct(OtelTrace trace) {
    System.out.println("Tracesss" + trace);
    traceCommandRepo.persist(trace);

    List<TraceDTO> traceDTOs = extractAndMapData(trace);
    System.out.println(traceDTOs);
  }

  // logic for getting serviceName
  private String getServiceName(ResourceSpans resourceSpans) {
    return resourceSpans
      .getResource()
      .getAttributes()
      .stream()
      .filter(attribute -> "service.name".equals(attribute.getKey()))
      .findFirst()
      .map(attribute -> attribute.getValue().getStringValue())
      .orElse(null);
  }

  // logic for calculating the createdtime
  private Date calculateCreatedTime(Spans span) {
    String startTimeUnixNano = span.getStartTimeUnixNano();
    long startUnixNanoTime = Long.parseLong(startTimeUnixNano);
    Instant startInstant = Instant.ofEpochSecond(
      startUnixNanoTime / 1_000_000_000L,
      startUnixNanoTime % 1_000_000_000L
    );
    ZoneId istZone = ZoneId.of("Asia/Kolkata");

    ZonedDateTime istTime = startInstant.atZone(istZone);

    Date date = Date.from(istTime.toInstant());
    System.out.println("Date--------" + date);
    return date;
  }

  private Long calculateDuration(Spans span) {
    long covertedStartTime = Long.parseLong(span.getStartTimeUnixNano(), 10);
    long convertedEndTime = Long.parseLong(span.getEndTimeUnixNano(), 10);

    Date startTime = new Date(covertedStartTime / 1000000);
    Date endTime = new Date(convertedEndTime / 1000000);

    // Calculate the duration in milliseconds
    long duration = endTime.getTime() - startTime.getTime();

    // System.out.println("-----------duration:------------- " + duration);
    return duration;
  }

  // extraction and marshelling of data and persistance for trace
  private List<TraceDTO> extractAndMapData(OtelTrace trace) {
    List<TraceDTO> traceDTOs = new ArrayList<>();

    try {
      for (ResourceSpans resourceSpans : trace.getResourceSpans()) {
        String serviceName = getServiceName(resourceSpans);

        List<String> traceIdList = new ArrayList<>();

        for (ScopeSpans scopeSpans : resourceSpans.getScopeSpans()) {
          List<Spans> spans = scopeSpans.getSpans();

          for (Spans span : spans) {
            String traceId = span.getTraceId();

            if (!traceIdList.contains(traceId)) {
              traceIdList.add(traceId);
            }
          }
        }

        for (String traceIdLoop : traceIdList) {
          TraceDTO traceDTO = new TraceDTO();
          List<Spans> objectList = new ArrayList<Spans>();

          for (ScopeSpans scopeSpans : resourceSpans.getScopeSpans()) {
            List<Spans> spans = scopeSpans.getSpans();
        
            for (Spans span : spans) {
                String traceId = span.getTraceId();
        
                if (traceId.contains(traceIdLoop)) {
                    traceDTO.setServiceName(serviceName);
                    traceDTO.setTraceId(traceId);
        
                    if (span.getParentSpanId() == null || span.getParentSpanId().isEmpty()) {
                        traceDTO.setOperationName(span.getName());
                        traceDTO.setCreatedTime(calculateCreatedTime(span));
                        traceDTO.setDuration(calculateDuration(span));
        
                        // Check for "http.status_code" attribute
                        List<Attributes> attributes = span.getAttributes();
                        for (Attributes attribute : attributes) {
                            if ("http.status_code".equals(attribute.getKey())) {
                                String statusCodeString = attribute.getValue().getIntValue();
        
                                if (statusCodeString != null) {
                                    try {
                                        Long statusCode = Long.parseLong(statusCodeString);
                                        traceDTO.setStatusCode(statusCode);
                                        System.out.println("Status Code stored successfully: " + statusCode);
                                    } catch (NumberFormatException e) {
                                        System.err.println("Failed to parse status code: " + statusCodeString);
                                        e.printStackTrace();
                                    }
                                } else {
                                    System.err.println("Status code is null. Cannot parse.");
                                }
                            }
                        }
                    } else {
                        // Code to handle when span.getParentSpanId() is not empty (if needed)
                    }
        
                    // Rest of your code
                    List<Attributes> attributes = span.getAttributes();
                    for (Attributes attribute : attributes) {
                        if ("http.method".equals(attribute.getKey())) {
                            traceDTO.setMethodName(attribute.getValue().getStringValue());
                        }
                        // Handle other attributes as needed
                    }
                    objectList.add(span);
                    traceDTO.setSpanCount(String.valueOf(objectList.size()));
                    traceDTO.setSpans(objectList);
                }
            }
        }
        
          traceQueryRepo.persist(traceDTO);
          System.out.println("TraceDto: " + traceDTO.toString());
        }
      }
      return traceDTOs;
    } catch (Exception e) {
      e.printStackTrace();
      return traceDTOs;
    }
  }

  // get all trace data
  public List<OtelTrace> getTraceProduct(OtelTrace trace) {
    return traceCommandRepo.listAll();
  }
}
