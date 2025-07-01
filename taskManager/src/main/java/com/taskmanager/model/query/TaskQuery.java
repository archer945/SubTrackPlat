package com.taskmanager.model.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskQuery {
    private Integer page = 1;
    private Integer size = 10;

    // 可选查询条件
    private String name;
    private String type;
    private String status;

    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;
}
