package com.zaga.entity.pod.scopeMetric;

import com.zaga.entity.pod.scopeMetric.gauge.MetricGauge;
import com.zaga.entity.pod.scopeMetric.sum.MetricSum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metric {
    private String name;
    private String description;
    private String unit;
    private MetricSum sum;
    private MetricGauge gauge;
}
