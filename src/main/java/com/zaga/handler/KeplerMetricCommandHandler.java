package com.zaga.handler;

import com.zaga.entity.kepler.KeplerMetric;
import com.zaga.entity.otelmetric.ResourceMetric;
import com.zaga.entity.otelmetric.ScopeMetric;
import com.zaga.entity.otelmetric.scopeMetric.Metric;
import com.zaga.entity.otelmetric.scopeMetric.MetricGauge;
import com.zaga.entity.otelmetric.scopeMetric.MetricHistogram;
import com.zaga.entity.otelmetric.scopeMetric.MetricSum;
import com.zaga.entity.otelmetric.scopeMetric.gauge.GaugeDataPoint;
import com.zaga.entity.otelmetric.scopeMetric.gauge.GaugeDataPointAttribute;
import com.zaga.entity.otelmetric.scopeMetric.gauge.GaugeDataPointAttributeValue;
import com.zaga.entity.otelmetric.scopeMetric.histogram.HistogramDataPoint;
import com.zaga.entity.otelmetric.scopeMetric.histogram.HistogramDataPointAttribute;
import com.zaga.entity.otelmetric.scopeMetric.histogram.HistogramDataPointAttributeValue;
import com.zaga.entity.otelmetric.scopeMetric.sum.SumDataPoint;
import com.zaga.entity.otelmetric.scopeMetric.sum.SumDataPointAttribute;
import com.zaga.entity.otelmetric.scopeMetric.sum.SumDataPointAttributeValue;
import com.zaga.entity.queryentity.kepler.KeplerMetricDTO;
import com.zaga.repo.KeplerMetricDTORepo;
import com.zaga.repo.KeplerMetricRepo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class KeplerMetricCommandHandler {

  @Inject
  KeplerMetricRepo keplerMetricRepo;

  @Inject
  KeplerMetricDTORepo KeplerMetricDTORepo;

  public void createKeplerMetric(KeplerMetric metric) {
    keplerMetricRepo.persist(metric);

    List<KeplerMetricDTO> metricDTOs = extractAndMapData(metric);

    if (!metricDTOs.isEmpty()) {
      for (KeplerMetricDTO keplerMetDTO : metricDTOs) {
        KeplerMetricDTORepo.persist(keplerMetDTO);
      }
    }
  }

  public List<KeplerMetricDTO> extractAndMapData(KeplerMetric keplerMetric) {

    List<KeplerMetricDTO> keplerMetricDTOLst = new ArrayList<>();

    List<ResourceMetric> resourceMetrics = keplerMetric.getResourceMetrics();

    for (ResourceMetric resourceMetric : resourceMetrics) {

      List<ScopeMetric> scopeMetrics = resourceMetric.getScopeMetrics();

      ScopeMetric scopeMetric = scopeMetrics.get(0);

      List<Metric> metrics = scopeMetric.getMetrics();

      List<KeplerMetricDTO> keplerMetricDTOList = new ArrayList<>();

      for (Metric metric : metrics) {

        String metricName = metric.getName();

        if ("kepler_container_cpu_cycles_total".equals(metricName)) {

          MetricGauge metricGauge = metric.getGauge();
          MetricSum metricSum = metric.getSum();
          MetricHistogram histogram = metric.getHistogram();

          if (metricGauge != null) {

            keplerMetricDTOList.addAll(
              processGaugeMetric(metricGauge, metricName)
            );
          } else if (metricSum != null) {

            keplerMetricDTOList.addAll(processSumMetric(metricSum, metricName));
          } else if (histogram != null) {

            keplerMetricDTOList.addAll(
              processHistogramMetric(histogram, metricName)
            );
          }
        } else {
          // System.out.println("not Found");
        }
      }

      keplerMetricDTOLst.addAll(keplerMetricDTOList);
    }

    return keplerMetricDTOLst;
  }

  private List<KeplerMetricDTO> processSumMetric(
    MetricSum metricSum,
    String metricName
  ) {
    List<KeplerMetricDTO> keplerMetricDTOList = new ArrayList<>();
    List<SumDataPoint> sumDataPoints = metricSum.getDataPoints();

    for (SumDataPoint sumDataPoint : sumDataPoints) {
      StringBuffer keys = new StringBuffer();
      double usage = 0;
      Date createdTime = null;
      String sumType = "Sum";

      String startTime = sumDataPoint.getTimeUnixNano();
      if (startTime != null) {
        createdTime = convertUnixNanoToLocalDateTime(startTime);
      }

      // String time = sumDataPoint.getTimeUnixNano();

      List<SumDataPointAttribute> sumAtt = sumDataPoint.getAttributes();
      String podName = null;
      String containerNamespace = null;
      String containerName = null;

      for (SumDataPointAttribute sumDataPointAttribute : sumAtt) {
        String keyValue = sumDataPointAttribute.getKey();
        String attvalue = null;
        SumDataPointAttributeValue gAttValue = sumDataPointAttribute.getValue();

        if (gAttValue != null) {
          attvalue = gAttValue.getStringValue();
        }

        if ("pod_name".equals(keyValue)) {
          podName = attvalue;
        } else if ("container_namespace".equals(keyValue)) {
          containerNamespace = attvalue;
        } else if ("container_name".equals(keyValue)) {
          containerName = attvalue;
        }
      }

      // keys.append("serviceName=");
      if (podName != null) {
        // keys.append("/");
        keys.append(podName);
      }
      if (containerNamespace != null) {
        keys.append("/");
        keys.append(containerNamespace);
      }
      if (containerName != null) {
        keys.append("/");
        keys.append(containerName);
      }

      KeplerMetricDTO keplerMetricDTO = new KeplerMetricDTO();
      keplerMetricDTO.setDate(createdTime);
      keplerMetricDTO.setPowerConsumption(usage);
      keplerMetricDTO.setServiceName(
        keys.toString().isEmpty() ? metricName : keys.toString()
      );
      // keplerMetricDTO.setType(sumType);

      keplerMetricDTOList.add(keplerMetricDTO);
    }

    return keplerMetricDTOList;
  }

  private List<KeplerMetricDTO> processGaugeMetric(
    MetricGauge metricGauge,
    String metricName
  ) {
    List<KeplerMetricDTO> keplerMetricDTOList = new ArrayList<>();
    List<GaugeDataPoint> gaugeDataPointLst = metricGauge.getDataPoints();

    for (GaugeDataPoint gaugeDataPoint : gaugeDataPointLst) {
      StringBuffer keys = null;
      String dobulevle = gaugeDataPoint.getAsDouble();
      String type = "Gauge";
      double usage = 0;
      Date createdTime = null;
      // long observedTimeMillis = 0;

      if (dobulevle != null) {
        usage = Double.parseDouble(dobulevle);
      }

      String startTimeStm = gaugeDataPoint.getStartTimeUnixNano();
      if (startTimeStm != null) {
        createdTime = convertUnixNanoToLocalDateTime(startTimeStm);
      }

      String time = gaugeDataPoint.getTimeUnixNano();

      List<GaugeDataPointAttribute> gaugeDataPointAttributes = gaugeDataPoint.getAttributes();

      for (GaugeDataPointAttribute gAtt : gaugeDataPointAttributes) {
        if (keys == null) {
          keys = new StringBuffer();
        }

        String keyValue = gAtt.getKey();
        String attvalue = null;
        GaugeDataPointAttributeValue gAttValue = gAtt.getValue();

        if (gAttValue != null) {
          attvalue = gAttValue.getStringValue();
        } else {
          attvalue = null;
        }

        if (keyValue != null && attvalue != null) {
          keys.append("( ");
          keys.append(keyValue);
          keys.append(":");
          keys.append(attvalue);
          keys.append(" )");
        }
      }

      KeplerMetricDTO keplerMetricDTO = new KeplerMetricDTO();
      keplerMetricDTO.setDate(createdTime);
      keplerMetricDTO.setPowerConsumption(usage);
      keplerMetricDTO.setServiceName(
        keys != null && !keys.equals("") ? keys.toString() : metricName
      );
      // keplerMetricDTO.setType(type);

      keplerMetricDTOList.add(keplerMetricDTO);
    }

    return keplerMetricDTOList;
  }

  private List<KeplerMetricDTO> processHistogramMetric(
    MetricHistogram histogram,
    String metricName
  ) {
    List<KeplerMetricDTO> keplerMetricDTOList = new ArrayList<>();
    List<HistogramDataPoint> histogramDataPoints = histogram.getDataPoints();

    for (HistogramDataPoint histogramDataPoint : histogramDataPoints) {
      StringBuffer keys = null;
      String type = "Histogram";

      String startTime = histogramDataPoint.getTimeUnixNano();
      Date createdTime = convertUnixNanoToLocalDateTime(startTime);

      // String time = histogramDataPoint.getTimeUnixNano();

      List<HistogramDataPointAttribute> histogramDataPointAttributes = histogramDataPoint.getAttributes();

      for (HistogramDataPointAttribute histogramDataPointAttribute : histogramDataPointAttributes) {
        if (keys == null) {
          keys = new StringBuffer();
        }

        String keyValue = histogramDataPointAttribute.getKey();
        String attvalue = null;

        HistogramDataPointAttributeValue gAttValue = histogramDataPointAttribute.getValue();

        if (gAttValue != null) {
          attvalue = gAttValue.getStringValue();
        } else {
          attvalue = null;
        }

        if (keyValue != null && attvalue != null) {
          keys.append("( ");
          keys.append(keyValue);
          keys.append(":");
          keys.append(attvalue);
          keys.append(" )");
        }
      }

      KeplerMetricDTO keplerMetricDTO = new KeplerMetricDTO();
      keplerMetricDTO.setDate(createdTime);
      double usage = 0; 
      keplerMetricDTO.setPowerConsumption(usage);
      // keplerMetricDTO.setObservedTimeMillis(observedTimeMillis);
      keplerMetricDTO.setServiceName(
        keys != null ? keys.toString() : metricName
      );
      // keplerMetricDTO.setType(type);

      // Add each DTO to the list
      keplerMetricDTOList.add(keplerMetricDTO);
    }

    return keplerMetricDTOList;
  }

  private static Date convertUnixNanoToLocalDateTime(String startTimeUnixNano) {
    long observedTimeMillis = Long.parseLong(startTimeUnixNano) / 1_000_000;

    Instant instant = Instant.ofEpochMilli(observedTimeMillis);

    ZoneId istZone = ZoneId.of("Asia/Kolkata");
    LocalDateTime istDateTime = LocalDateTime.ofInstant(instant, istZone);

    return Date.from(istDateTime.atZone(istZone).toInstant());
  }

  public void addDTO() {
    // KeplerMetricDTO keplerMetricDTO = new KeplerMetricDTO();

    // keplerMetricDTO.setDate(createdTime);
    // keplerMetricDTO.setPowerConsumption(usage);
    // keplerMetricDTO.setObservedTimeMillis(observedTimeMillis);
    // keplerMetricDTO.setServiceName(key == null ? metricName : key);
    // keplerMetricDTO.setType(type);
    // keplerMetricDTOLst.add(keplerMetricDTO);

  }
}
