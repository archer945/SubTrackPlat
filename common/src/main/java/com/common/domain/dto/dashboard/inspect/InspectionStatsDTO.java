package com.common.domain.dto.dashboard.inspect;

import lombok.Data;

import java.util.List;

@Data
public class InspectionStatsDTO {

    // 核心指标
    private Integer todayCount;          // 今日巡视任务数
    private Integer yesterdayCount;     // 昨日巡视任务数
    private Double growthRate;          // 环比增长率
    private Integer completedCount;     // 已完成任务数
    private Double completionRate;     // 完成率

    // 分布统计
    private List<InspectionTypeStats> typeDistribution;    // 按任务类型分布
    private List<InspectionStatusStats> statusDistribution; // 按状态分布
    private List<InspectionExecutorStats> executorDistribution; // 按执行人分布
    private List<InspectionTrendDTO> recentTrend;          // 最近7天趋势


    // 新增以下两个字段
    private Integer totalInspections;          // 巡检总次数（历史累计）
    private List<MonthlyInspectionDTO> monthlyInspections; // 每月巡检次数
}
