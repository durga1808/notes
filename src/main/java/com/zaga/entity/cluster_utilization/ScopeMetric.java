package com.zaga.entity.cluster_utilization;

import java.util.List;

import com.zaga.entity.cluster_utilization.scopeMetric.Metric;
import com.zaga.entity.cluster_utilization.scopeMetric.Scope;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScopeMetric {
    private Scope scope;
    private List<Metric> metrics;
}
