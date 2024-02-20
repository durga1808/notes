package com.zaga.entity.cluster_utilization.scopeMetric.sum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SumDataPoint {
    private String startTimeUnixNano;
    private String timeUnixNano;
    private String asInt;
    private String asDouble;
}
