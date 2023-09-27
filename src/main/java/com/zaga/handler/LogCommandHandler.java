package com.zaga.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zaga.entity.otellog.OtelLog;
import com.zaga.entity.otellog.ResourceLogs;
import com.zaga.entity.otellog.ScopeLogs;
import com.zaga.entity.otellog.scopeLogs.LogRecord;
import com.zaga.entity.queryentity.log.LogDTO;
import com.zaga.repo.LogCommandRepo;
import com.zaga.repo.LogQueryRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LogCommandHandler {

    @Inject
    LogCommandRepo logCommandRepo;

    @Inject
    LogQueryRepo logQueryRepo;

    public void createLogProduct(OtelLog logs) {
        logCommandRepo.persist(logs);

        List<LogDTO> logDTOs = marshalLogData(logs);
        System.out.println("log sizes"+logDTOs.size());
    }

    public List<LogDTO> marshalLogData(OtelLog logs) {
        Set<LogDTO> logDTOs = new HashSet<>(); // Use a Set to prevent duplicates
    
        try {
            for (ResourceLogs resourceLog : logs.getResourceLogs()) {
                String serviceName = getServiceName(resourceLog);
    
                for (ScopeLogs scopeLog : resourceLog.getScopeLogs()) {
                    for (LogRecord logRecord : scopeLog.getLogRecords()) {
                        LogDTO logDTO = new LogDTO();
                        logDTO.setServiceName(serviceName);
                        logDTO.setTraceId(logRecord.getTraceId());
    
                        // Include all scopeLogs for each logDTO
                        logDTO.setScopeLogs(resourceLog.getScopeLogs());
    
                        logDTOs.add(logDTO);
                    }
                }
            }
    
            if (!logDTOs.isEmpty()) {
                logQueryRepo.persist(new ArrayList<>(logDTOs)); // Convert Set to List for persistence
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return new ArrayList<>(logDTOs); // Convert Set to List for return
    }
    
    
    

     private String getServiceName(ResourceLogs resourceLog) {
    return resourceLog
      .getResource()
      .getAttributes()
      .stream()
      .filter(attribute -> "service.name".equals(attribute.getKey()))
      .findFirst()
      .map(attribute -> attribute.getValue().getStringValue())
      .orElse(null);
  }

}
