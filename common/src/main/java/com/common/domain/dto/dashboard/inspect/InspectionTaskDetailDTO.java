package com.common.domain.dto.dashboard.inspect;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InspectionTaskDetailDTO {
    private Long id;              // 任务ID
    private String lineName;      // 线路名称
    private String startPoint;    // 起点
    private String endPoint;      // 终点
    private String status;        // 任务状态
    private LocalDateTime actualStart; // 计划开始时间
    private LocalDateTime plannedEnd;   // 计划结束时间
    private String name;          // 执行人姓名
}