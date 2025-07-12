package com.dashboard.service;

import com.common.domain.dto.dashboard.inspect.*;
import com.dashboard.mapper.InspectionTaskMapper;
import com.dashboard.service.impl.InspectionStatsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InspectionStatsServiceTest {

    @Mock
    private InspectionTaskMapper inspectionTaskMapper;
    
    @InjectMocks
    private InspectionStatsServiceImpl inspectionStatsService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testGetInspectionStats() {
        // 准备测试数据
        BasicStatsDTO basicStats = new BasicStatsDTO();
        basicStats.setToday(10);
        basicStats.setYesterday(8);
        basicStats.setCompleted(7);
        basicStats.setTodayDistance(15.5);
        basicStats.setYesterdayDistance(12.3);
        
        List<InspectionTypeStats> typeStats = new ArrayList<>();
        InspectionTypeStats typeA = new InspectionTypeStats();
        typeA.setType("A类型");
        typeA.setCount(5);
        InspectionTypeStats typeB = new InspectionTypeStats();
        typeB.setType("B类型");
        typeB.setCount(5);
        typeStats.add(typeA);
        typeStats.add(typeB);
        
        List<InspectionStatusStats> statusStats = new ArrayList<>();
        InspectionStatusStats statusA = new InspectionStatusStats();
        statusA.setStatus("已完成");
        statusA.setCount(7);
        InspectionStatusStats statusB = new InspectionStatusStats();
        statusB.setStatus("进行中");
        statusB.setCount(3);
        statusStats.add(statusA);
        statusStats.add(statusB);
        
        List<InspectionExecutorStats> executorStats = new ArrayList<>();
        List<InspectionTrendDTO> trendStats = new ArrayList<>();
        List<MonthlyInspectionDTO> monthlyStats = new ArrayList<>();
        
        // 模拟依赖方法的行为
        when(inspectionTaskMapper.countBasicStats()).thenReturn(basicStats);
        when(inspectionTaskMapper.sumCompletedDistance()).thenReturn(100.5);
        when(inspectionTaskMapper.getTodayInspectionTasks()).thenReturn(new ArrayList<>());
        when(inspectionTaskMapper.countByType()).thenReturn(typeStats);
        when(inspectionTaskMapper.countByStatus()).thenReturn(statusStats);
        when(inspectionTaskMapper.countByExecutor()).thenReturn(executorStats);
        when(inspectionTaskMapper.countRecentTrend()).thenReturn(trendStats);
        when(inspectionTaskMapper.countTotalInspections()).thenReturn(100);
        when(inspectionTaskMapper.countMonthlyInspections()).thenReturn(monthlyStats);
        
        // 执行测试
        InspectionStatsDTO result = inspectionStatsService.getInspectionStats();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(10, result.getTodayCount());
        assertEquals(8, result.getYesterdayCount());
        assertEquals(7, result.getCompletedCount());
        assertEquals(15.5, result.getTodayDistance());
        assertEquals(12.3, result.getYesterdayDistance());
        assertEquals(100.5, result.getTotalDistance());
        assertEquals(25.0, result.getGrowthRate()); // (10-8)/8*100
        assertEquals(70.0, result.getCompletionRate()); // 7/10*100
        assertEquals(2, result.getTypeDistribution().size());
        assertEquals(2, result.getStatusDistribution().size());
        assertEquals(50.0, result.getTypeDistribution().get(0).getRatio());
        assertEquals(70.0, result.getStatusDistribution().get(0).getRatio());
        assertEquals(100, result.getTotalInspections());
        
        verify(inspectionTaskMapper).countBasicStats();
        verify(inspectionTaskMapper).sumCompletedDistance();
        verify(inspectionTaskMapper).getTodayInspectionTasks();
        verify(inspectionTaskMapper).countByType();
        verify(inspectionTaskMapper).countByStatus();
        verify(inspectionTaskMapper).countByExecutor();
        verify(inspectionTaskMapper).countRecentTrend();
        verify(inspectionTaskMapper).countTotalInspections();
        verify(inspectionTaskMapper).countMonthlyInspections();
    }
    
    @Test
    void testCalculateRates() {
        // 准备测试数据
        InspectionStatsDTO stats = new InspectionStatsDTO();
        stats.setTodayCount(10);
        stats.setYesterdayCount(8);
        stats.setCompletedCount(7);
        
        // 执行测试
        inspectionStatsService.calculateRates(stats);
        
        // 验证结果
        assertEquals(25.0, stats.getGrowthRate()); // (10-8)/8*100
        assertEquals(70.0, stats.getCompletionRate()); // 7/10*100
    }
    
    @Test
    void testCalculateRatesWithZeroYesterday() {
        // 准备测试数据
        InspectionStatsDTO stats = new InspectionStatsDTO();
        stats.setTodayCount(10);
        stats.setYesterdayCount(0);
        stats.setCompletedCount(7);
        
        // 执行测试
        inspectionStatsService.calculateRates(stats);
        
        // 验证结果
        assertEquals(0.0, stats.getGrowthRate()); // 除以0的特殊处理
        assertEquals(70.0, stats.getCompletionRate()); // 7/10*100
    }
    
    @Test
    void testCalculateRatesWithZeroToday() {
        // 准备测试数据
        InspectionStatsDTO stats = new InspectionStatsDTO();
        stats.setTodayCount(0);
        stats.setYesterdayCount(8);
        stats.setCompletedCount(0);
        
        // 执行测试
        inspectionStatsService.calculateRates(stats);
        
        // 验证结果
        assertEquals(-100.0, stats.getGrowthRate()); // (0-8)/8*100
        assertEquals(0.0, stats.getCompletionRate()); // 除以0的特殊处理
    }
    
    @Test
    void testCalculateRatios() {
        // 准备测试数据
        List<InspectionTypeStats> stats = new ArrayList<>();
        InspectionTypeStats typeA = new InspectionTypeStats();
        typeA.setType("A类型");
        typeA.setCount(30);
        InspectionTypeStats typeB = new InspectionTypeStats();
        typeB.setType("B类型");
        typeB.setCount(70);
        stats.add(typeA);
        stats.add(typeB);
        
        // 执行测试
        List<InspectionTypeStats> result = inspectionStatsService.calculateRatios(stats, 100);
        
        // 验证结果
        assertEquals(2, result.size());
        assertEquals(30.0, result.get(0).getRatio());
        assertEquals(70.0, result.get(1).getRatio());
    }
    
    @Test
    void testCalculateRatiosWithZeroTotal() {
        // 准备测试数据
        List<InspectionTypeStats> stats = new ArrayList<>();
        InspectionTypeStats typeA = new InspectionTypeStats();
        typeA.setType("A类型");
        typeA.setCount(30);
        stats.add(typeA);
        
        // 执行测试
        List<InspectionTypeStats> result = inspectionStatsService.calculateRatios(stats, 0);
        
        // 验证结果
        assertEquals(1, result.size());
        assertEquals(0.0, result.get(0).getRatio()); // 除以0的特殊处理
    }
} 