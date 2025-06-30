package com.common.domain.dto.dashboard.defect;

import lombok.Data;

@Data
public class DefectTypeDTO {
    private String type;      // 缺陷类型（如"结构裂缝"）
    private Integer count;    // 数量
    private Double ratio;     // 占比
}
