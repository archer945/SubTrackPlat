package com.taskmanager.model.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskQuery {
    private Integer page = 1; // 当前页码
    private Integer size = 10; // 每页数据条数

    // 可选查询条件
    private String name; // 任务名称
    private String type; // 任务类型
    private String status; // 任务状态
    private String executor; // 执行人
    private String priority; // 任务优先级

    private LocalDateTime plannedStart; // 计划开始时间
    private LocalDateTime plannedEnd; // 计划结束时间
}
