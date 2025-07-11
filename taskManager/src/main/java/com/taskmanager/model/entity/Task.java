package com.taskmanager.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Task {
    private Long id;
    private String name;
    private String type;
    private String priority;
    private String description;
    private Long executorId;
    private Long assistantId;
    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private String lineName;
    private String startPoint;
    private String endPoint;
    private String rangeDescription;
    private String status;
    private Integer completionRate;
    private String result;
    private Integer problemsFound;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long creatorId;
//    private String creatorName;
//    private String executorName;
//    private String assistantName;

}
