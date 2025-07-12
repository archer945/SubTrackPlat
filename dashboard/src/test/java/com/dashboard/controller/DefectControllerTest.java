package com.dashboard.controller;

import com.common.domain.dto.dashboard.defect.DefectStatsDTO;
import com.common.domain.dto.dashboard.defect.DefectTypeDTO;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.ResultStatus;
import com.dashboard.service.DefectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefectControllerTest {

    @Mock
    private DefectService defectService;

    @InjectMocks
    private DefectController defectController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApiRoot() {
        String result = defectController.apiRoot();
        assertEquals("Dashboard API 已就绪，可用接口：/defectOverview, /inspectSummary", result);
    }

    @Test
    void testGetDefectType_Success() {
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
        when(defectService.getDefectType()).thenReturn(defectTypes);
        
        // 执行测试
        JsonVO<List<DefectTypeDTO>> result = defectController.getDefectType();
        
        // 验证结果
        assertEquals(ResultStatus.SUCCESS.getCode(), result.getCode());
        assertEquals(defectTypes, result.getData());
        verify(defectService).getDefectType();
    }

    @Test
    void testGetDefectType_Failure() {
        // 模拟依赖方法的行为
        when(defectService.getDefectType()).thenReturn(null);
        
        // 执行测试
        JsonVO<List<DefectTypeDTO>> result = defectController.getDefectType();
        
        // 验证结果
        assertEquals(ResultStatus.FAIL.getCode(), result.getCode());
        assertNull(result.getData());
        verify(defectService).getDefectType();
    }

    @Test
    void testGetDefectStats() {
        // 准备测试数据
        DefectStatsDTO stats = new DefectStatsDTO();
        stats.setTotalDefects(100);
        
        // 模拟依赖方法的行为
        when(defectService.getDefectStats()).thenReturn(stats);
        
        // 执行测试
        JsonVO<DefectStatsDTO> result = defectController.getDefectStats();
        
        // 验证结果
        assertEquals(ResultStatus.SUCCESS.getCode(), result.getCode());
        assertEquals(stats, result.getData());
        verify(defectService).getDefectStats();
    }
} 