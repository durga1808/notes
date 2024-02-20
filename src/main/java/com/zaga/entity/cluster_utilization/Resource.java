package com.zaga.entity.cluster_utilization;

import java.util.List;

import com.zaga.entity.cluster_utilization.resource.Attribute;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resource {
   private List<Attribute> attributes; 
}
