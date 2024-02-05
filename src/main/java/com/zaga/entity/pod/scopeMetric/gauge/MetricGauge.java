package com.zaga.entity.pod.scopeMetric.gauge;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricGauge {
       private List<GaugeDataPoint> dataPoints;
}
