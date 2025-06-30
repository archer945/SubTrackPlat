package com.common.domain.dto.dashboard.inspect;


import lombok.Data;

@Data
public class InspectionTrendDTO {
    private String date;       // 日期(yyyy-MM-dd)
    private Integer createdCount;  // 当日创建任务数
    private Integer completedCount; // 当日完成任务数
}
