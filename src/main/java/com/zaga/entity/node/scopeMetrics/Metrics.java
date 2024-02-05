package com.zaga.entity.node.scopeMetrics;

import com.zaga.entity.node.scopeMetrics.gauge.Gauge;
import com.zaga.entity.node.scopeMetrics.sum.Sum;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metrics {
    private String description;
    private String name;
    private String unit;
    private Sum sum;
    private Gauge gauge;

    

}
