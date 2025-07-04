package com.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.domain.dto.dashboard.inspect.*;
import com.dashboard.entity.InspectTask;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;


@Mapper
public interface InspectionTaskMapper extends BaseMapper<InspectTask> {

    // 基础统计
    BasicStatsDTO countBasicStats();

    // 按任务类型统计
    List<InspectionTypeStats> countByType();

    // 按状态统计
    List<InspectionStatusStats> countByStatus();

    // 按执行人统计
    List<InspectionExecutorStats> countByExecutor();

    // 最近7天趋势
    List<InspectionTrendDTO> countRecentTrend();

    Integer countTotalInspections();

    List<MonthlyInspectionDTO> countMonthlyInspections();

    // 计算所有已完成任务的距离总和
    Double sumCompletedDistance();

    // 获取今日巡检任务详情列表
    List<InspectionTaskDetailDTO> getTodayInspectionTasks();

}