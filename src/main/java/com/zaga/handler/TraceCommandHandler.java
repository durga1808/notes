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
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
      
      return date;
  }
  


  private Long calculateDuration(Spans span) {
    String startTimeUnixNano = span.getStartTimeUnixNano();
    String endTimeUnixNano = span.getEndTimeUnixNano();

    long startUnixNanoTime = Long.parseLong(startTimeUnixNano);
    long endUnixNanoTime = Long.parseLong(endTimeUnixNano);

    Instant startInstant = Instant.ofEpochSecond(
      startUnixNanoTime / 1_000_000_000L,
      startUnixNanoTime % 1_000_000_000L
    );
    Instant endInstant = Instant.ofEpochSecond(
      endUnixNanoTime / 1_000_000_000L,
      endUnixNanoTime % 1_000_000_000L
    );

    Duration duration = Duration.between(startInstant, endInstant);

    return (Long) duration.toMillis();
  }

  // Helper method to find a span by its spanId within a ResourceSpans object
private Spans findParentSpan(ResourceSpans resourceSpans, String spanId) {
  for (ScopeSpans scopeSpans : resourceSpans.getScopeSpans()) {
      for (Spans span : scopeSpans.getSpans()) {
          if (span.getSpanId().equals(spanId)) {
              return span;
          }
      }
  }
  return null; // Return null if parent span is not found
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
                            } else {
                                // String parentSpanId = span.getParentSpanId();
                                // Spans parentSpan = findParentSpan(resourceSpans, parentSpanId);

                                // if (parentSpan != null) {
                                //     traceDTO.setCreatedTime(calculateCreatedTime(parentSpan));
                                //     traceDTO.setOperationName(parentSpan.getName());
                                // }
                            }
                            
                            List<Attributes> attributes = span.getAttributes();
                            
                            for (Attributes attribute : attributes) {
                              if ("http.method".equals(attribute.getKey())) {
                                traceDTO.setMethodName(
                                  attribute.getValue().getStringValue()
                                );
                              } else if ("http.status_code".equals(attribute.getKey())) {
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
                            
                             else {
                              }
                            }
                            traceDTO.setDuration(calculateDuration(span));
                            objectList.add(span);
                            traceDTO.setSpanCount(String.valueOf(objectList.size()));
                            traceDTO.setSpans(objectList);
                          }
                        }
                      }
                      traceQueryRepo.persist(traceDTO);
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
