package com.zaga.entity.node.scopeMetrics.gauge;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gauge {
    private List<GaugeDataPoints> dataPoints;
    @JsonProperty("isMonotonic")
    @JsonIgnore
    private boolean isMonotonic;
    @JsonProperty("aggregationTemporality")
    @JsonIgnore
    private int aggregationTemporality;
}
