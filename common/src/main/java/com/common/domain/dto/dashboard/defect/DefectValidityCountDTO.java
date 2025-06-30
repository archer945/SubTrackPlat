package com.common.domain.dto.dashboard.defect;

import lombok.Data;

@Data
public class DefectValidityCountDTO {
    private Integer total;        // 总缺陷数
    private Integer valid;       // 有效缺陷数
    private Integer falseReport; // 误报缺陷数
}
