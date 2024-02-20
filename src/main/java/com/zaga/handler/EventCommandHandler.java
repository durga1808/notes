package com.zaga.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zaga.entity.otelevent.OtelEvents;
import com.zaga.entity.otelevent.Resource;
import com.zaga.entity.otelevent.ResourceLogs;
import com.zaga.entity.otelevent.ScopeLogs;
import com.zaga.entity.otelevent.scopeLogs.LogRecords;
import com.zaga.entity.queryentity.events.EventsDTO;
import com.zaga.repo.EventRepo;
import com.zaga.repo.EventsDTORepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class EventCommandHandler {
    
    @Inject
    EventRepo repo ;

    @Inject
    EventsDTORepo eventsDTORepo;

    // public OtelEvents createEvents (OtelEvents events) {
    //     repo.persist(events);
    //     return events;
    // }
    public void handleEventData(OtelEvents events) {
    List<ResourceLogs> resourceLogsList = events.getResourceLogs();

    for (ResourceLogs resourceLogs : resourceLogsList) {
        Resource resource = resourceLogs.getResource();
        String nodeName = getNodeName(resource);
        String objectKind = getObjectKind(resource);
        String objectName = getObjectName(resource);

        List<ScopeLogs> scopeLogsList = resourceLogs.getScopeLogs();
        for (ScopeLogs scopeLogs : scopeLogsList) {
            String timeUnixNano = getTimeUnixNano(scopeLogs);

            List<LogRecords> logRecordsList = scopeLogs.getLogRecords();
            Set<String> uniqueBodies = new HashSet<>(); // Keep track of unique body values
            for (LogRecords logRecord : logRecordsList) {
                // Extract relevant information from logRecord
                String severityText = logRecord.getSeverityText();
                String spanId = logRecord.getSpanId();
                String traceId = logRecord.getTraceId();
                String body = logRecord.getBody().getStringValue();  // Assuming Body has a stringValue field
                System.out.println("scopeLogs------"+ scopeLogs);
                // Convert timeUnixNano to Date in UTC
                Date createdTime = new Date(Long.parseLong(timeUnixNano) / 1_000_000); // Divide by 1_000_000 to convert nanoseconds to milliseconds

                // Check if the body value is unique
                if (uniqueBodies.add(body)) {
                    // Create EventsDTO object and set the extracted information
                    EventsDTO eventsDTO = new EventsDTO();
                    eventsDTO.setSeverityText(severityText);
                    eventsDTO.setCreatedTime(createdTime);
                    eventsDTO.setNodeName(nodeName);
                    eventsDTO.setObjectKind(objectKind);
                    eventsDTO.setObjectName(objectName);
                    
                    // Create a new list containing only the current scopeLogs
                    List<ScopeLogs> singleScopeLogsList = new ArrayList<>();
                    singleScopeLogsList.add(scopeLogs);
                    eventsDTO.setScopeLogs(singleScopeLogsList);

                    // Store the EventsDTO object in the database
                    eventsDTORepo.persist(eventsDTO);
                }
            }
        }
    }
}

    private String getNodeName(Resource resource) {
        return resource.getAttributes()
                .stream()
                .filter(attribute -> "k8s.node.name".equals(attribute.getKey()))
                .findFirst()
                .map(attribute -> attribute.getValue().getStringValue())
                .orElse(null);
    }

    private String getObjectKind(Resource resource) {
        return resource.getAttributes()
                .stream()
                .filter(attribute -> "k8s.object.kind".equals(attribute.getKey()))
                .findFirst()
                .map(attribute -> attribute.getValue().getStringValue())
                .orElse(null);
    }

    private String getObjectName(Resource resource) {
        return resource.getAttributes()
                .stream()
                .filter(attribute -> "k8s.object.name".equals(attribute.getKey()))
                .findFirst()
                .map(attribute -> attribute.getValue().getStringValue())
                .orElse(null);
    }

    private String getTimeUnixNano(ScopeLogs scopeLogs) {
        return scopeLogs.getLogRecords().get(0).getTimeUnixNano();
    }
}
