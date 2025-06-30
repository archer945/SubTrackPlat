package com.common.domain.dto.dashboard.inspect;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InspectionStatusStats extends InspectionTypeStats{
    private String status;     // 任务状态
    private Integer count;     // 数量
    private Double ratio;      // 占比
}
