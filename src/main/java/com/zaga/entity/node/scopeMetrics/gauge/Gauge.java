package com.zaga.entity.node.scopeMetrics.gauge;

import java.util.List;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gauge {
    private List<GaugeDataPoints> dataPoints;
    private boolean isMonotonic;
    private int aggregationTemporality;
}
