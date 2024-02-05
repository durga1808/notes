package com.zaga.entity.node.scopeMetrics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zaga.entity.node.scopeMetrics.gauge.Gauge;
import com.zaga.entity.node.scopeMetrics.sum.Sum;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metrics {
    @JsonProperty("description")
    private String description;
    @JsonProperty("name")
    private String name;
    @JsonProperty("unit")
    private String unit;
    private Sum sum;
    private Gauge gauge;

    

}
