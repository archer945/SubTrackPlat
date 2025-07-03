package com.common.domain.dto.dashboard.inspect;

import lombok.Data;

@Data
public class InspectionTypeStats {
    private String type;       // 任务类型
    private Integer count;     // 数量
    private Double ratio;      // 占比
}
