package com.zaga.handler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.zaga.entity.auth.Rule;
import com.zaga.entity.auth.ServiceListNew;
import com.zaga.entity.otelmetric.OtelMetric;
import com.zaga.entity.otelmetric.ResourceMetric;
import com.zaga.entity.otelmetric.ScopeMetric;
import com.zaga.entity.otelmetric.scopeMetric.Metric;
import com.zaga.entity.otelmetric.scopeMetric.MetricGauge;
import com.zaga.entity.otelmetric.scopeMetric.MetricSum;
import com.zaga.entity.otelmetric.scopeMetric.gauge.GaugeDataPoint;
import com.zaga.entity.otelmetric.scopeMetric.sum.SumDataPoint;
import com.zaga.entity.queryentity.metrics.MetricDTO;
import com.zaga.kafka.websocket.WebsocketAlertProducer;
import com.zaga.repo.MetricCommandRepo;
import com.zaga.repo.MetricDTORepo;
import com.zaga.repo.ServiceListRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.EncodeException;

@ApplicationScoped
public class MetricCommandHandler {

    @Inject
    MetricCommandRepo metricCommandRepo;

    @Inject
    MetricDTORepo metricDtoRepo;

    @Inject
    private WebsocketAlertProducer sessions;

    @Inject
    ServiceListRepo serviceListRepo;

    public void createMetricProduct(OtelMetric metrics) {
        // metricCommandRepo.persist(metrics);
        List<MetricDTO> metricDTOs = extractAndMapData(metrics);
        ServiceListNew serviceListData1 = new ServiceListNew();
        for (MetricDTO metricDTOSingle : metricDTOs) {
            serviceListData1 = serviceListRepo.find("serviceName = ?1", metricDTOSingle.getServiceName()).firstResult();
            break;
        }
        // System.out.println("Queried Data " + serviceListData1);
        for (MetricDTO metricDTO : metricDTOs) {
            // System.out.println("Metric DTOS " + metricDTO.toString());
            processRuleManipulation(metricDTO, serviceListData1);

            // break;
        }
        System.out.println("---------MetricDTOs:---------- " + metricDTOs.size());
    }

    public void processRuleManipulation(MetricDTO metricDTO, ServiceListNew serviceListData) {
        // System.out.println("Output " + metricDTO + " sdata " + serviceListData);
        // Gson gson = new Gson();

        // File file = new
        // File("./src/main/java/com/zaga/kafka/consumer/MetricRule.json");

        try {

            // ServiceListNew serviceListData = gson.fromJson(reader1,
            // ServiceListNew.class);
            // System.out.println("Service Data " + serviceListData.getServiceName());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            LocalDateTime currentDateTime = LocalDateTime.now();
            if (serviceListData.getRules().size() != 0) {
                for (Rule sData : serviceListData.getRules()) {
                    if (sData.getRuleType().equals("metric")) {
                        LocalDateTime startDate = sData.getStartDateTime();
                        LocalDateTime expiryDate = sData.getExpiryDateTime();
                        if (startDate != null && expiryDate != null) {
                            // Existing formatting and printing
                            String startDateTimeString = startDate.format(formatter);
                            System.out.println("Start DateTime: " + startDateTimeString);
                            String expiryDateTimeString = expiryDate.format(formatter);
                            System.out.println("Expiry DateTime: " + expiryDateTimeString);

                            // Update the startDateTime and expiryDateTime parsing
                            // Parse startDateTime
                            LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeString, formatter);
                            // Set startDateTime in your Rule object
                            sData.setStartDateTime(startDateTime);

                            // Parse expiryDateTime
                            LocalDateTime expiryDateTime = LocalDateTime.parse(expiryDateTimeString, formatter);
                            // Set expiryDateTime in your Rule object
                            sData.setExpiryDateTime(expiryDateTime);

                            // Other computations based on your conditions
                            Double cpuUsage = metricDTO.getCpuUsage();
                            Double cpuLimitMilliCores = sData.getCpuLimit() * 1000;
                            Integer memoryUsage = metricDTO.getMemoryUsage();

                            System.out.println("CPU USAGE " + cpuUsage + " MEMORY USAGE " + memoryUsage + " MILLICORES "
                                    + cpuLimitMilliCores);

                            Map<String, String> alertPayload = new HashMap<>();

                            alertPayload.put("serviceName", metricDTO.getServiceName());

                            boolean isMetricExceeded = false;

                            // if ((cpuUsage != null && cpuUsage != 0) && (memoryUsage != null &&
                            // memoryUsage != 0)) {
                            // if ((cpuUsage >= cpuLimitMilliCores || memoryUsage >= sData.getMemoryLimit())
                            // &&
                            // currentDateTime.isAfter(sData.getStartDateTime()) &&
                            // currentDateTime.isBefore(sData.getExpiryDateTime())) {
                            // System.out.println(
                            // "Metric Data values " + cpuUsage + " " + memoryUsage + " exceed limits.");
                            // sessions.getSessions().forEach(session -> {
                            // try {
                            // if (session == null) {
                            // System.out.println("No session");
                            // } else {
                            // session.getBasicRemote().sendObject("Alert for Metric");
                            // System.out.println("Message Metric sent");
                            // }
                            // } catch (IOException | EncodeException e) {
                            // e.printStackTrace();
                            // }
                            // });
                            // // Perform actions when values exceed limits and fall within the date range
                            // }
                            // }

                            // Check for memoryUsage
                            if (memoryUsage != null && memoryUsage != 0) {
                                if (memoryUsage >= sData.getMemoryLimit() &&
                                        currentDateTime.isAfter(sData.getStartDateTime()) &&
                                        currentDateTime.isBefore(sData.getExpiryDateTime())) {
                                    isMetricExceeded = true;
                                    alertPayload.put("isMemoryExceeded", "true");
                                    System.out.println("Memory Usage exceeds limit.");
                                    // Any specific action for memory usage exceeding the limit
                                }
                            }

                            // Check for cpuUsage
                            if (cpuUsage != null && cpuUsage != 0) {
                                if (cpuUsage >= cpuLimitMilliCores &&
                                        currentDateTime.isAfter(sData.getStartDateTime()) &&
                                        currentDateTime.isBefore(sData.getExpiryDateTime())) {
                                    isMetricExceeded = true;
                                    alertPayload.put("isCpuExceeded", "true");
                                    System.out.println("CPU Usage exceeds limit.");
                                    // Any specific action for CPU usage exceeding the limit
                                }
                            }

                            if (isMetricExceeded) {
                                // Add other relevant details to the alertPayload if necessary
                                alertPayload.put("isMetricExceeded", "true");

                                // Send alerts to sessions
                                sessions.getSessions().forEach(session -> {
                                    try {
                                        if (session == null) {
                                            System.out.println("No session");
                                        } else {
                                            session.getBasicRemote().sendObject(alertPayload);
                                            System.out.println("Message Metric sent");
                                        }
                                    } catch (IOException | EncodeException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }

                        }

                    }
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR " + e.getLocalizedMessage());
        }

    }

    public List<MetricDTO> extractAndMapData(OtelMetric metrics) {

        List<MetricDTO> metricDTOs = new ArrayList<>();

        Integer memoryUsage = 0;

        try {
            for (ResourceMetric resourceMetric : metrics.getResourceMetrics()) {
                String serviceName = getServiceName(resourceMetric);
                for (ScopeMetric scopeMetric : resourceMetric.getScopeMetrics()) {
                    Date createdTime = null;
                    Double cpuUsage = null;
                    String name = scopeMetric.getScope().getName();
                    if (name != null && name.contains("io.opentelemetry.runtime-telemetry")) {
                        List<Metric> metricsList = scopeMetric.getMetrics();
                        for (Metric metric : metricsList) {
                            String metricName = metric.getName();
                            if (isSupportedMetric(metricName)) {
                                if (metric.getSum() != null) {
                                    MetricSum metricSum = metric.getSum();
                                    List<SumDataPoint> sumDataPoints = metricSum.getDataPoints();

                                    for (SumDataPoint sumDataPoint : sumDataPoints) {
                                        String startTimeUnixNano = sumDataPoint.getTimeUnixNano();
                                        createdTime = convertUnixNanoToLocalDateTime(startTimeUnixNano);
                                        if (isMemoryMetric(metricName)) {
                                            if (sumDataPoint.getAsInt() != null && !sumDataPoint.getAsInt().isEmpty()) {
                                                String asInt = sumDataPoint.getAsInt();
                                                int currentMemoryUsage = Integer.parseInt(asInt);
                                                System.out.println("--------Memory usage:----- " + currentMemoryUsage);

                                                memoryUsage += currentMemoryUsage;
                                            }
                                        }
                                    }
                                }
                                if (metric.getGauge() != null) {
                                    MetricGauge metricGauge = metric.getGauge();
                                    List<GaugeDataPoint> gaugeDataPoints = metricGauge.getDataPoints();
                                    for (GaugeDataPoint gaugeDataPoint : gaugeDataPoints) {
                                        if (isCpuMetric(metricName)) {
                                            if (gaugeDataPoint.getAsDouble() != null) {
                                                String asDouble = gaugeDataPoint.getAsDouble();
                                                System.out.println("--------asDOUBLE------" + asDouble);
                                                cpuUsage = Double.parseDouble(asDouble);
                                                System.out.println("--------cpuUsage-------" + cpuUsage);
                                            }
                                        }
                                    }
                                }

                                Integer memoryUsageInMb = (memoryUsage / (1024 * 1024));

                                // Create a MetricDTO and add it to the list
                                MetricDTO metricDTO = new MetricDTO();
                                metricDTO.setMemoryUsage(memoryUsageInMb);
                                metricDTO.setDate(createdTime);
                                metricDTO.setServiceName(serviceName);
                                metricDTO.setCpuUsage(cpuUsage);
                                metricDTOs.add(metricDTO);
                            }
                        }
                    }
                }
            }

            if (!metricDTOs.isEmpty()) {
                // Only persist the last MetricDTO, outside the loop
                metricDtoRepo.persist(metricDTOs.subList(metricDTOs.size() - 1, metricDTOs.size()));

                // System.out.println("----Last MetricDTO----: " + metricDTOs);
                // System.out.println("Last MetricDTO: " + metricDTOs.get(metricDTOs.size() -
                // 1));
            }
        } catch (Exception e) {
            // Handle exceptions here
        }

        return metricDTOs;
    }

    private boolean isSupportedMetric(String metricName) {
        return Set.of(
                "process.runtime.jvm.threads.count",
                "process.runtime.jvm.system.cpu.utilization",
                "process.runtime.jvm.system.cpu.load_1m",
                "process.runtime.jvm.memory.usage",
                "process.runtime.jvm.memory.limit").contains(metricName);
    }

    private boolean isMemoryMetric(String metricName) {
        return Set.of("process.runtime.jvm.memory.usage").contains(metricName);
    }

    private boolean isCpuMetric(String metricName) {
        return Set.of("process.runtime.jvm.cpu.utilization", "process.runtime.jvm.system.cpu.utilization")
                .contains(metricName);
    }

    private String getServiceName(ResourceMetric resourceMetric) {
        return resourceMetric
                .getResource()
                .getAttributes()
                .stream()
                .filter(attribute -> "service.name".equals(attribute.getKey()))
                .findFirst()
                .map(attribute -> attribute.getValue().getStringValue())
                .orElse(null);
    }

    // private Date convertUnixNanoToLocalDateTime(String startTimeUnixNano) {
    // long nanoValue = Long.parseLong(startTimeUnixNano);

    // // Convert Unix Nano timestamp to Instant
    // Instant instant = Instant.ofEpochSecond(nanoValue / 1_000_000_000, nanoValue
    // % 1_000_000_000);

    // // Convert Instant to Date
    // Date date = Date.from(instant);

    // // Return the Date object
    // return date;
    // }
    private static Date convertUnixNanoToLocalDateTime(String startTimeUnixNano) {
        long observedTimeMillis = Long.parseLong(startTimeUnixNano) / 1_000_000;

        Instant instant = Instant.ofEpochMilli(observedTimeMillis);

        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        LocalDateTime istDateTime = LocalDateTime.ofInstant(instant, istZone);

        return Date.from(istDateTime.atZone(istZone).toInstant());

    }
}