package com.common.domain.dto.dashboard.defect;

import lombok.Data;

import java.util.List;

@Data
public class DefectStatsDTO {
    private Integer totalDefects;          // 总缺陷数
    private Integer confirmedValidDefects;         // 有效缺陷数
    private Double confirmedValidRate;             // 有效率
    private Integer falseReportDefects;   // 【新增】误报缺陷数（is_valid = false）
    private Double falseReportRate;       // 【新增】误报率
    private List<DefectTypeStats> typeDistribution;  // 类型分布
    private List<DefectSeverityStats> severityDistribution; // 严重程度分布
    private List<DefectStatusStats> statusDistribution; // 状态分布
    private List<DefectTrendDTO> recentTrend; // 最近7天趋势
    private List<DefectValidityStatsDTO> validityDistribution; // 有效性分布
}
