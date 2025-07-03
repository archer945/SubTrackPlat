package com.common.domain.dto.dashboard.defect;

import lombok.Data;

@Data
public class DefectTypeStats {
    private String status;    // "待确认"、"已确认"等
    private Integer count;    // 数量
    private Double ratio;     // 占比
}
