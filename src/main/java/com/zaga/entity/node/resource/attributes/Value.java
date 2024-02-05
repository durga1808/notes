package com.zaga.entity.node.resource.attributes;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Value {
    
    private String stringValue;
    private ArrayValue arrayValue;

}
