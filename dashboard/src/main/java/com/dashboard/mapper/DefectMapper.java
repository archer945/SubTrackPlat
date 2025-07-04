package com.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.domain.dto.dashboard.defect.*;
import com.dashboard.entity.Defect;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface DefectMapper extends BaseMapper<Defect>{

    List<DefectTypeDTO> getDefectType();

    // 核心有效性统计（包含误报）
    // 修改返回类型为自定义DTO
    DefectValidityCountDTO countDefectValidity();

    // 有效性分布
    List<DefectValidityStatsDTO> countByValidity();

    // 类型分布
    List<DefectTypeStats> countByType();

    // 严重程度分布
    List<DefectSeverityStats> countBySeverity();

    // 状态分布
    List<DefectStatusStats> countByStatus();

    // 最近7天趋势
    List<DefectTrendDTO> countRecentTrend();
}
