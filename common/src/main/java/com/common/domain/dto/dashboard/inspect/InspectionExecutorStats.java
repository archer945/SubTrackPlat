package com.common.domain.dto.dashboard.inspect;

import lombok.Data;


@Data
public class InspectionExecutorStats {
    private Long executorId; // 执行者id
    private String executorName; // 执行者名称
    private Integer taskCount; // 负责的任务数
}
