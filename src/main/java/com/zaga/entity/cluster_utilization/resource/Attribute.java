package com.zaga.entity.cluster_utilization.resource;

import com.zaga.entity.cluster_utilization.resource.attributes.Value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attribute {
     private String key;
    private Value value;
}
