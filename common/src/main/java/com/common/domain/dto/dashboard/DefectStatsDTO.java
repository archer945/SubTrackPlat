package com.common.domain.dto.dashboard;

import lombok.Data;

import java.util.List;

@Data
public class DefectStatsDTO {
    private Integer totalDefects;          // 总缺陷数
    private Integer validDefects;         // 有效缺陷数
    private Double validRate;             // 有效率
    private List<DefectTypeStats> typeDistribution;  // 类型分布
    private List<DefectSeverityStats> severityDistribution; // 严重程度分布
    private List<DefectStatusStats> statusDistribution; // 状态分布
    private List<DefectTrendDTO> recentTrend; // 最近7天趋势
}
