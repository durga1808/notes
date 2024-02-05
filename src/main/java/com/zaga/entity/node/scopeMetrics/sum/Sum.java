package com.zaga.entity.node.scopeMetrics.sum;

import java.util.List;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sum {
    private List<SumDataPoints> dataPoints;
    private boolean isMonotonic;
    private int aggregationTemporality;
}
