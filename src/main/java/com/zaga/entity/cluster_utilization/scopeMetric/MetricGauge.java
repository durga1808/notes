package com.zaga.entity.cluster_utilization.scopeMetric;

import java.util.List;

import com.zaga.entity.cluster_utilization.scopeMetric.gauge.GaugeDataPoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricGauge {
      private List<GaugeDataPoint> dataPoints;
}
