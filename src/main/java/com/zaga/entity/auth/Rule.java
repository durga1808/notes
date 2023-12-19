package com.zaga.entity.auth;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Rule {
    private String ruleType;
    private Double cpuLimit;
    private Integer memoryLimit;
    private long duration;
    private List<String> severityText;
    private Integer reoccuringCount;
    private LocalDateTime startDateTime;
    private LocalDateTime expiryDateTime;
}
