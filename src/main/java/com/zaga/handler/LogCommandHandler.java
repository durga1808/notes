package com.zaga.handler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
        System.out.println("log sizes" + logDTOs.size());
    }

    public List<LogDTO> marshalLogData(OtelLog logs) {
        List<LogDTO> logDTOs = new ArrayList<>();
        LogDTO logDTO = new LogDTO();

        try {
            for (ResourceLogs resourceLog : logs.getResourceLogs()) {
                String serviceName = getServiceName(resourceLog);
                logDTO.setServiceName(serviceName);
                for (ScopeLogs scopeLog : resourceLog.getScopeLogs()) {
                    for (LogRecord logRecord : scopeLog.getLogRecords()) {
                        String traceId = logRecord.getTraceId();
                        List<LogRecord> mockLog = new ArrayList<LogRecord>();

                        // Create a new LogDTO for each LogRecord

                        logDTO.setTraceId(traceId);
                        logDTO.setSpanId(logRecord.getSpanId());
                        logDTO.setSeverityText(logRecord.getSeverityText());
                        logDTO.setCreatedTime(convertObservedTimeToIST(logRecord.getObservedTimeUnixNano()));

                        mockLog.add(logRecord);

                        ScopeLogs newScopeLogs = new ScopeLogs();
                        newScopeLogs.setScope(scopeLog.getScope());
                        newScopeLogs.setLogRecords(mockLog);

                        logDTO.setScopeLogs(Collections.singletonList(newScopeLogs));

                        System.out.println("Log Data DTo " + logDTO);
                        // logDTOs.add(logDTO);
                        logQueryRepo.persist(logDTO);

                    }

                }
            }

            if (!logDTOs.isEmpty()) {
                // logQueryRepo.persist(logDTOs);
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

    private static Date convertObservedTimeToIST(String observedTimeUnixNano) {
        long observedTimeMillis = Long.parseLong(observedTimeUnixNano) / 1_000_000;

        Instant instant = Instant.ofEpochMilli(observedTimeMillis);

        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        LocalDateTime istDateTime = LocalDateTime.ofInstant(instant, istZone);

        return Date.from(istDateTime.atZone(istZone).toInstant());
    }

}
