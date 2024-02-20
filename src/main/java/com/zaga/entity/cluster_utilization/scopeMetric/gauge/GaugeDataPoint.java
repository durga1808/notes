package com.zaga.entity.cluster_utilization.scopeMetric.gauge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GaugeDataPoint {
    private String startTimeUnixNano;
    private String timeUnixNano;
    private String asInt;
    private String asDouble;
}
