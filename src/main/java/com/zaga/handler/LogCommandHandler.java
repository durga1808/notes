package com.zaga.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.zaga.entity.otellog.OtelLog;
import com.zaga.entity.otellog.ResourceLogs;
import com.zaga.entity.otellog.ScopeLogs;
import com.zaga.entity.otellog.scopeLogs.LogRecord;
import com.zaga.entity.otellog.scopeLogs.logRecord.Body;
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
    Map<String, LogDTO> logDTOMap = new HashMap<>();

    try {
        for (ResourceLogs resourceLog : logs.getResourceLogs()) {
            String serviceName = getServiceName(resourceLog);

            for (ScopeLogs scopeLog : resourceLog.getScopeLogs()) {
                for (LogRecord logRecord : scopeLog.getLogRecords()) {
                    String traceId = logRecord.getTraceId();

                    LogDTO logDTO = logDTOMap.get(traceId);

                    if (logDTO == null) {
                        logDTO = new LogDTO();
                        logDTO.setServiceName(serviceName);
                        logDTO.setTraceId(traceId);
                        logDTO.setScopeLogs(new ArrayList<>());

                        logDTOMap.put(traceId, logDTO);
                    }

                    logDTO.getScopeLogs().add(scopeLog);
                }
            }
        }

        if (!logDTOMap.isEmpty()) {
            List<LogDTO> logDTOs = new ArrayList<>(logDTOMap.values());
            logQueryRepo.persist(logDTOs);
            return logDTOs;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return new ArrayList<>();
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
