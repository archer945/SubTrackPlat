package com.common.domain.dto.dashboard.defect;

import lombok.Data;

@Data
public class DefectTypeStats {
    private String type;      //类型
    private Integer count;    // 数量
    private Double ratio;     // 占比
}
