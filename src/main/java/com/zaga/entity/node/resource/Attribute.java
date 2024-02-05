package com.zaga.entity.node.resource;

import com.zaga.entity.node.resource.attributes.Value;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attribute {
    private String  key ;
    private  Value value ;

}
