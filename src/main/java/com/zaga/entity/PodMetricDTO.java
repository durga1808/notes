package com.zaga.entity;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PodMetricDTO {
    private List<MetricDTO> metrics;
    private String podName;
}
