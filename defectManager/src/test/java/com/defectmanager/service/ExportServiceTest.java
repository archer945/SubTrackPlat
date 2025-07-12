package com.defectmanager.service;

import com.defectmanager.entity.Defect;
import com.defectmanager.enmu.DefectStatusEnum;
import com.defectmanager.enmu.DefectTypeEnum;
import com.defectmanager.enmu.SeverityLevelEnum;
import com.defectmanager.mapper.DefectMapper;
import com.defectmanager.query.DefectQuery;
import com.defectmanager.service.impl.ExportServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExportServiceTest {

    @Mock
    private DefectMapper defectMapper;
    
    @InjectMocks
    private ExportServiceImpl exportService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testExportDefectsToCsv() throws IOException {
        // 准备测试数据
        DefectQuery query = new DefectQuery();
        query.setType(DefectTypeEnum.STRUCTURAL_CRACK.getDbValue());
        query.setStatus(DefectStatusEnum.PENDING.getDbValue());
        
        List<Defect> defects = new ArrayList<>();
        Defect defect1 = createSampleDefect(1L, "测试任务1", DefectTypeEnum.STRUCTURAL_CRACK, new BigDecimal("100.0"), true, SeverityLevelEnum.HIGH);
        Defect defect2 = createSampleDefect(2L, "测试任务2", DefectTypeEnum.LEAKAGE, new BigDecimal("200.0"), false, SeverityLevelEnum.MEDIUM);
        defects.add(defect1);
        defects.add(defect2);
        
        HttpServletResponse response = new MockHttpServletResponse();
        
        // 模拟依赖方法的行为
        when(defectMapper.selectByQuery(any(DefectQuery.class))).thenReturn(defects);
        
        // 执行测试
        ByteArrayOutputStream result = exportService.exportDefectsToCsv(query, response);
        
        // 验证结果
        assertNotNull(result);
        String csvContent = result.toString("UTF-8");
        assertTrue(csvContent.contains("序号,任务名称,缺陷类型,缺陷距离位置(m),是否属实,严重程度"));
        assertTrue(csvContent.contains("1,测试任务1,结构裂缝"));
        assertTrue(csvContent.contains("2,测试任务2,渗水"));
        
        verify(defectMapper).selectByQuery(any(DefectQuery.class));
    }
    
    @Test
    void testExportDefectsToCsvWithEmptyResult() throws IOException {
        // 准备测试数据
        DefectQuery query = new DefectQuery();
        HttpServletResponse response = new MockHttpServletResponse();
        
        // 模拟依赖方法的行为
        when(defectMapper.selectByQuery(any(DefectQuery.class))).thenReturn(new ArrayList<>());
        
        // 执行测试
        ByteArrayOutputStream result = exportService.exportDefectsToCsv(query, response);
        
        // 验证结果
        assertNotNull(result);
        String csvContent = result.toString("UTF-8");
        assertTrue(csvContent.contains("序号,任务名称,缺陷类型,缺陷距离位置(m),是否属实,严重程度"));
        assertFalse(csvContent.contains("测试任务"));
        
        verify(defectMapper).selectByQuery(any(DefectQuery.class));
    }
    
    // 辅助方法，创建测试用的缺陷对象
    private Defect createSampleDefect(Long id, String taskName, DefectTypeEnum type, BigDecimal location, 
                                     Boolean isValid, SeverityLevelEnum severity) {
        Defect defect = new Defect();
        defect.setId(id);
        defect.setTaskName(taskName);
        defect.setType(type);
        defect.setLocation(location);
        defect.setIsValid(isValid);
        defect.setSeverity(severity);
        defect.setDefectLength(new BigDecimal("1.5"));
        defect.setDefectArea(new BigDecimal("2.25"));
        defect.setDefectCount(1);
        defect.setSuggestion("建议修复");
        defect.setFoundTime(LocalDateTime.now());
        defect.setStatus(DefectStatusEnum.PENDING);
        return defect;
    }
} 