package com.common.domain.dto.dashboard.defect;

import lombok.Data;

@Data
public class DefectSeverityStats {
    private String severity;  // "高"、"中"、"低"
    private Integer count;    // 数量
    private Double ratio;     // 占比
}
