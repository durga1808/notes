package com.zaga.entity.node;

import java.util.List;

import com.zaga.entity.node.scopeMetrics.Metrics;
import com.zaga.entity.node.scopeMetrics.Scope;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScopeMetric {
    private Scope scope;
    private List<Metrics>metrics ;

}
