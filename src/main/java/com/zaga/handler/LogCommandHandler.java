package com.zaga.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    

// public List<LogDTO> marshalLogData(OtelLog logs) {
   
//     Map<String, LogDTO> logDTOMap = new HashMap<>();

//     try {
//         for (ResourceLogs resourceLog : logs.getResourceLogs()) {
//             String serviceName = getServiceName(resourceLog);

//             for (ScopeLogs scopeLog : resourceLog.getScopeLogs()) {
//                 for (LogRecord logRecord : scopeLog.getLogRecords()) {
//                     String traceId = logRecord.getTraceId();

//                     LogDTO logDTO = logDTOMap.get(traceId);

//                     if (logDTO == null) {
//                         logDTO = new LogDTO();
//                         logDTO.setServiceName(serviceName);
//                         logDTO.setTraceId(traceId);
//                         logDTO.setScopeLogs(new ArrayList<>());

//                         logDTOMap.put(traceId, logDTO);
//                     }

//                     logDTO.getScopeLogs().add(scopeLog);
//                 }
//             }
//         }

//         if (!logDTOMap.isEmpty()) {
//             logQueryRepo.persist(new ArrayList<>(logDTOMap.values()));
//         }

//     } catch (Exception e) {
//         e.printStackTrace();
//     }

//     return new ArrayList<>(logDTOMap.values());
// }

// Import statements here

public List<LogDTO> marshalLogData(OtelLog otelLog) {
    List<LogDTO> logDTOs = new ArrayList<>();

    for (ResourceLogs resourceLogs : otelLog.getResourceLogs()) {
        for (ScopeLogs scopeLogs : resourceLogs.getScopeLogs()) {
            String serviceName = getServiceName(resourceLogs);

            Map<String, List<LogRecord>> groupedByTraceId = scopeLogs.getLogRecords().stream()
                    .collect(Collectors.groupingBy(LogRecord::getTraceId));

            for (Map.Entry<String, List<LogRecord>> entry : groupedByTraceId.entrySet()) {
                String traceId = entry.getKey();
                List<LogRecord> logRecordDTOs = entry.getValue();

                LogDTO logDTO = new LogDTO();
                logDTO.setServiceName(serviceName);
                logDTO.setTraceId(traceId);
                
                ScopeLogs scopeLogsDTO = new ScopeLogs();
                scopeLogsDTO.setScope(scopeLogs.getScope());
                scopeLogsDTO.setLogRecords(logRecordDTOs);

                logDTO.setScopeLogs(Collections.singletonList(scopeLogsDTO));

                logDTOs.add(logDTO);


                
                // System.out.println("LogDTO: " + logDTO);
                // System.out.println("ScopeLogsDTO: " + scopeLogsDTO);
                // System.out.println("LogRecordDTOs: " + logRecordDTOs);
            
            }
        }
    }

   for(LogDTO logDTO : logDTOs){
    System.out.println("Persisting LogDTOs++++++++++++++++++++++: " + logDTO);
   }
    logQueryRepo.persist(logDTOs);

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
