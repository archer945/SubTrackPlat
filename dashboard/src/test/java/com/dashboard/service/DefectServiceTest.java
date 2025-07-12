package com.dashboard.service;

import com.common.domain.dto.dashboard.defect.DefectStatsDTO;
import com.common.domain.dto.dashboard.defect.DefectTypeDTO;
import com.common.domain.dto.dashboard.defect.DefectValidityCountDTO;
import com.dashboard.mapper.DefectMapper;
import com.dashboard.service.impl.DefectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefectServiceTest {

    @Mock
    private DefectMapper defectMapper;
    
    @InjectMocks
    private DefectServiceImpl defectService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testGetDefectType() {
        // 准备测试数据
        List<DefectTypeDTO> defectTypes = new ArrayList<>();
        DefectTypeDTO type1 = new DefectTypeDTO();
        type1.setType("结构裂缝");
        type1.setCount(30);
        DefectTypeDTO type2 = new DefectTypeDTO();
        type2.setType("渗水");
        type2.setCount(70);
        defectTypes.add(type1);
        defectTypes.add(type2);
        
        // 模拟依赖方法的行为
        when(defectMapper.getDefectType()).thenReturn(defectTypes);
        
        // 执行测试
        List<DefectTypeDTO> result = defectService.getDefectType();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("结构裂缝", result.get(0).getType());
        assertEquals("渗水", result.get(1).getType());
        assertEquals(30.0, result.get(0).getRatio());
        assertEquals(70.0, result.get(1).getRatio());
        
        verify(defectMapper).getDefectType();
    }
    
    @Test
    void testGetDefectStats() {
        // 准备测试数据
        DefectValidityCountDTO validityCount = new DefectValidityCountDTO();
        validityCount.setTotal(100);
        validityCount.setValid(80);
        validityCount.setFalseReport(20);
        
        // 模拟依赖方法的行为
        when(defectMapper.countDefectValidity()).thenReturn(validityCount);
        when(defectMapper.countByType()).thenReturn(new ArrayList<>());
        when(defectMapper.countBySeverity()).thenReturn(new ArrayList<>());
        when(defectMapper.countByStatus()).thenReturn(new ArrayList<>());
        when(defectMapper.countByValidity()).thenReturn(new ArrayList<>());
        when(defectMapper.countRecentTrend()).thenReturn(new ArrayList<>());
        
        // 执行测试
        DefectStatsDTO result = defectService.getDefectStats();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(100, result.getTotalDefects());
        assertEquals(80, result.getConfirmedValidDefects());
        assertEquals(20, result.getFalseReportDefects());
        assertEquals(80.0, result.getConfirmedValidRate());
        assertEquals(20.0, result.getFalseReportRate());
        
        verify(defectMapper).countDefectValidity();
        verify(defectMapper).countByType();
        verify(defectMapper).countBySeverity();
        verify(defectMapper).countByStatus();
        verify(defectMapper).countByValidity();
        verify(defectMapper).countRecentTrend();
    }
    
    @Test
    void testFormatPercent() {
        // 通过反射调用私有方法进行测试
        try {
            java.lang.reflect.Method formatPercentMethod = DefectServiceImpl.class.getDeclaredMethod("formatPercent", Integer.class, Integer.class);
            formatPercentMethod.setAccessible(true);
            
            DefectServiceImpl service = new DefectServiceImpl();
            
            // 测试正常情况
            Double result1 = (Double) formatPercentMethod.invoke(service, 25, 100);
            assertEquals(25.0, result1);
            
            // 测试小数情况
            Double result2 = (Double) formatPercentMethod.invoke(service, 33, 100);
            assertEquals(33.0, result2);
            
            // 测试除以零情况（实现应该处理这种情况）
            try {
                Double result3 = (Double) formatPercentMethod.invoke(service, 25, 0);
                // 如果没有抛出异常，验证结果是否为0或其他默认值
                assertNotNull(result3);
            } catch (Exception e) {
                // 如果抛出异常，也是可接受的处理方式
                assertTrue(e.getCause() instanceof ArithmeticException);
            }
            
        } catch (Exception e) {
            fail("测试私有方法失败: " + e.getMessage());
        }
    }
} 