package com.zaga.entity.podMetric;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "Metric",database = "PodMetric")
public class PodMetric {
    private String serviceName;
    private Long cpuMax;
    private Long cpuMin;
    private Long memoryMin;
    private Long memoryMax;
}
