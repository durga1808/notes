package com.zaga.entity.cluster_utilization;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceMetric {
     private Resource resource;
    private List<ScopeMetric> scopeMetrics;
}
