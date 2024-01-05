package com.zaga.entity.auth;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


import jakarta.json.bind.annotation.JsonbDateFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Rule {
    // private String ruleType;
    // private Double cpuLimit;
    // private Integer memoryLimit;
    // private long duration;
    // private List<String> severityText;
    // private Integer reoccuringCount;
    // @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    // private LocalDateTime startDateTime;
    // @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    // private LocalDateTime expiryDateTime;

    private String memoryConstraint;
    private LocalDateTime expiryDateTime;
    private long duration;
    private double cpuLimit;
    private Integer memoryLimit;
    private String ruleType;
    private LocalDateTime startDateTime;
    private String cpuConstraint;
    private String durationConstraint;
    private List<String> severityText;
    private String severityConstraint;
}
