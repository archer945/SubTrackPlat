package com.dashboard.service.impl;

import com.common.domain.dto.dashboard.inspect.BasicStatsDTO;
import com.common.domain.dto.dashboard.inspect.InspectionStatsDTO;
import com.common.domain.dto.dashboard.inspect.InspectionTypeStats;
import com.dashboard.mapper.InspectionTaskMapper;
import com.dashboard.service.InspectionStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Component
public class InspectionStatsServiceImpl implements InspectionStatsService {
    @Autowired
    private  InspectionTaskMapper inspectionTaskMapper;

    @Override
    public InspectionStatsDTO getInspectionStats() {
        InspectionStatsDTO stats = new InspectionStatsDTO();

        // 1. 获取基础统计
        // 1. 获取基础统计（改为使用BasicStatsDTO接收）
        BasicStatsDTO basicStats = inspectionTaskMapper.countBasicStats();
        stats.setTodayCount(basicStats.getToday());
        stats.setYesterdayCount(basicStats.getYesterday());
        stats.setCompletedCount(basicStats.getCompleted());

        // 2. 计算比率
        calculateRates(stats);

        // 3. 获取分布数据
        stats.setTypeDistribution(calculateRatios(
                inspectionTaskMapper.countByType(),
                stats.getTodayCount()
        ));
        stats.setStatusDistribution(calculateRatios(
                inspectionTaskMapper.countByStatus(),
                stats.getTodayCount()
        ));
        stats.setExecutorDistribution(inspectionTaskMapper.countByExecutor());
        stats.setRecentTrend(inspectionTaskMapper.countRecentTrend());

        stats.setTotalInspections(inspectionTaskMapper.countTotalInspections());
        stats.setMonthlyInspections(inspectionTaskMapper.countMonthlyInspections());

        return stats;
    }

    public void calculateRates(InspectionStatsDTO stats) {
        // 计算环比增长率
        if (stats.getYesterdayCount() > 0) {
            double growth = (stats.getTodayCount() - stats.getYesterdayCount()) * 100.0 / stats.getYesterdayCount();
            stats.setGrowthRate(Double.parseDouble(String.format("%.2f", growth)));
        } else {
            stats.setGrowthRate(0.0);
        }

        // 计算完成率
        if (stats.getTodayCount() > 0) {
            double rate = stats.getCompletedCount() * 100.0 / stats.getTodayCount();
            stats.setCompletionRate(Double.parseDouble(String.format("%.2f", rate)));
        } else {
            stats.setCompletionRate(0.0);
        }
    }

    public <T extends InspectionTypeStats> List<T> calculateRatios(List<T> stats, int total) {
        if (total > 0) {
            stats.forEach(s -> s.setRatio(s.getCount() * 100.0 / total));
        }
        return stats;
    }
}
