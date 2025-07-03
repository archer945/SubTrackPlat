package com.dashboard.mapper;

import com.common.domain.dto.dashboard.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface DefectMapper {

    @Mapper
    public  List<DefectTypeDTO> getDefectType();

    @Mapper
    // 基础统计
    Map<String, Integer> countTotalAndValidDefects();

    @Mapper
    // 按类型统计
    List<DefectTypeStats> countByType();

    @Mapper
    // 按严重程度统计
    List<DefectSeverityStats> countBySeverity();

    @Mapper
    // 按状态统计
    List<DefectStatusStats> countByStatus();

    @Mapper
    // 最近7天趋势
    List<DefectTrendDTO> countRecentTrend();

}
