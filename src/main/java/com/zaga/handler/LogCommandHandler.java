package com.zaga.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        List<LogDTO> logDTOs = new ArrayList<>();
    
        try {
            for (ResourceLogs resourceLog : logs.getResourceLogs()) {
                for (ScopeLogs scopeLog : resourceLog.getScopeLogs()) {
                    for (LogRecord logRecord : scopeLog.getLogRecords()){
                    LogDTO logDTO = new LogDTO();
                    String serviceName = getServiceName(resourceLog);
                    logDTO.setServiceName(serviceName);
                    logDTO.setTraceId(logRecord.getTraceId());
    
                    // Set the entire list of scopeLogs
                    logDTO.setScopeLogs(resourceLog.getScopeLogs());
    
                    logDTOs.add(logDTO);
                }
            }
            }
    
            if (!logDTOs.isEmpty()) {
                logQueryRepo.persist(logDTOs);
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return logDTOs;
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
