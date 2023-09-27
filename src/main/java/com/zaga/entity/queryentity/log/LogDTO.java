package com.zaga.entity.queryentity.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogDTO {
    private String serviceName;
    private String traceId;
    
}
