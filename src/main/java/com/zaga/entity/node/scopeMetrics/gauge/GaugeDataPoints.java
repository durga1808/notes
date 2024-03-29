package com.zaga.entity.node.scopeMetrics.gauge;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GaugeDataPoints {
    @JsonProperty("asDouble")
    private Double asDouble;
    @JsonProperty("asInt")
    private String asInt;
    @JsonProperty("timeUnixNano")
    private String timeUnixNano;
    @JsonProperty("startTimeUnixNano")
    private String startTimeUnixNano;
}
