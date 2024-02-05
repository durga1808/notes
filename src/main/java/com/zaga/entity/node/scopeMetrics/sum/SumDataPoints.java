package com.zaga.entity.node.scopeMetrics.sum;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SumDataPoints {
    private String asDouble;
    private String timeUnixNano;
    private double startTimeUnixNano;
}
