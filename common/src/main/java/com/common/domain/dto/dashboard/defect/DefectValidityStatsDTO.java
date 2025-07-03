package com.common.domain.dto.dashboard.defect;

import lombok.Data;

@Data
public class DefectValidityStatsDTO {
    private Boolean isValid;  // true=有效, false=误报
    private Integer count;
    private Double ratio;
}
