package com.common.domain.dto.dashboard.inspect;

import lombok.Data;

@Data
public class MonthlyInspectionDTO {
    private String yearMonth;  // 格式："2023-06"
    private Integer count;     // 该月巡检次数
}
