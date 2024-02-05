package com.zaga.entity.pod.scopeMetric.sum;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SumDataPoint {
    private List<SumAttribute> attributes;
    private String startTimeUnixNano;
    private String timeUnixNano;
    private Long asInt; 
    private Double  asDouble; 

}
