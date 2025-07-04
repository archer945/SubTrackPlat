package com.dashboard.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("inspect_task")
public class InspectTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String type;
    private String priority; // 对应枚举类型 inspect_task_priority_enum
    private String description;

    @TableField("executor_id")
    private Long executorId;

    @TableField("assistant_id")
    private Long assistantId;

    @TableField("planned_start")
    private LocalDateTime plannedStart;

    @TableField("planned_end")
    private LocalDateTime plannedEnd;

    @TableField("actual_start")
    private LocalDateTime actualStart;

    @TableField("actual_end")
    private LocalDateTime actualEnd;

    @TableField("line_name")
    private String lineName;

    @TableField("start_point")
    private String startPoint;

    @TableField("end_point")
    private String endPoint;

    @TableField("range_description")
    private String rangeDescription;

    private Double distance; // 巡视距离

    private String status; // 对应枚举类型 inspect_task_status_enum

    @TableField("completion_rate")
    private Integer completionRate;

    private String result;

    @TableField("problems_found")
    private Integer problemsFound;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}