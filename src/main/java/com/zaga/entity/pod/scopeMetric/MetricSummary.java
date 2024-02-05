package com.zaga.entity.pod.scopeMetric;

import java.util.List;

import com.zaga.entity.pod.scopeMetric.sum.SumDataPoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricSummary {
     private List<SumDataPoint> dataPoints;
}
