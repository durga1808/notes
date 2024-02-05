package com.zaga.entity.pod.scopeMetric.gauge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GaugeDataPoint {
    private String startTimeUnixNano;
    private String timeUnixNano;
    private Long asInt; 
    private Double asDouble;
}
