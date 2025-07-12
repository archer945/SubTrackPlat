package com.dashboard.controller;

import com.common.domain.dto.dashboard.inspect.InspectionStatsDTO;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.ResultStatus;
import com.dashboard.service.InspectionStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InspectionStatsControllerTest {

    @Mock
    private InspectionStatsService inspectionStatsService;

    @InjectMocks
    private InspectionStatsController inspectionStatsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetInspectionSummary() {
        // 准备测试数据
        InspectionStatsDTO stats = new InspectionStatsDTO();
        stats.setTotalInspections(100);
        stats.setTodayCount(10);
        
        // 模拟依赖方法的行为
        when(inspectionStatsService.getInspectionStats()).thenReturn(stats);
        
        // 执行测试
        JsonVO<InspectionStatsDTO> result = inspectionStatsController.getInspectionSummary();
        
        // 验证结果
        assertEquals(ResultStatus.SUCCESS.getCode(), result.getCode());
        assertEquals(stats, result.getData());
        verify(inspectionStatsService).getInspectionStats();
    }

    @Test
    void testGetInspectionTrend_NoParams() {
        // 准备测试数据
        InspectionStatsDTO stats = new InspectionStatsDTO();
        stats.setTotalInspections(100);
        
        // 模拟依赖方法的行为
        when(inspectionStatsService.getInspectionStats()).thenReturn(stats);
        
        // 执行测试
        JsonVO<InspectionStatsDTO> result = inspectionStatsController.getInspectionTrend(null, null);
        
        // 验证结果
        assertEquals(ResultStatus.SUCCESS.getCode(), result.getCode());
        assertEquals(stats, result.getData());
        verify(inspectionStatsService).getInspectionStats();
    }

    @Test
    void testGetInspectionTrend_WithParams() {
        // 准备测试数据
        InspectionStatsDTO stats = new InspectionStatsDTO();
        stats.setTotalInspections(50);
        
        // 模拟依赖方法的行为
        when(inspectionStatsService.getInspectionStats()).thenReturn(stats);
        
        // 执行测试
        JsonVO<InspectionStatsDTO> result = inspectionStatsController.getInspectionTrend("2023-01-01", "2023-01-31");
        
        // 验证结果
        assertEquals(ResultStatus.SUCCESS.getCode(), result.getCode());
        assertEquals(stats, result.getData());
        verify(inspectionStatsService).getInspectionStats();
    }
} 