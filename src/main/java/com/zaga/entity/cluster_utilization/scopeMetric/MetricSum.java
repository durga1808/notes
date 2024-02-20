package com.zaga.entity.cluster_utilization.scopeMetric;

import java.util.List;

import com.zaga.entity.cluster_utilization.scopeMetric.sum.SumDataPoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricSum {
    private List<SumDataPoint> dataPoints;
    private int aggregationTemporality;
    private Boolean isMonotonic;

}
