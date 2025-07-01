package com.taskmanager.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TaskExportVO {

    @ExcelProperty("任务名称")
    private String name;

    @ExcelProperty("任务类型")
    private String type;

    @ExcelProperty("优先级")
    private String priority;

    @ExcelProperty("执行人ID")
    private Long executorId;

    @ExcelProperty("计划开始")
    private String plannedStart;

    @ExcelProperty("计划结束")
    private String plannedEnd;

    @ExcelProperty("状态")
    private String status;
}
