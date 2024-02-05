package com.zaga.entity.node;

import java.util.List;

import com.zaga.entity.node.resource.Attributes;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class Resource {
    private List<Attributes> attributes;
}
