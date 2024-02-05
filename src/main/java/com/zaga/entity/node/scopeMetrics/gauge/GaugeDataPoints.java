package com.zaga.entity.node.scopeMetrics.gauge;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GaugeDataPoints {
    private String asDouble;
    private String timeUnixNano;
    private double startTimeUnixNano;
}
