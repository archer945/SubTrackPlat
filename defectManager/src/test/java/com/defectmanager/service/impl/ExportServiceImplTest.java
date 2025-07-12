package com.defectmanager.service.impl;

import com.defectmanager.entity.Defect;
import com.defectmanager.enmu.DefectStatusEnum;
import com.defectmanager.enmu.DefectTypeEnum;
import com.defectmanager.enmu.SeverityLevelEnum;
import com.defectmanager.mapper.DefectMapper;
import com.defectmanager.query.DefectQuery;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExportServiceImplTest {

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
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        List<Defect> defects = new ArrayList<>();
        
        // 创建一个完整的缺陷对象
        Defect defect = new Defect();
        defect.setId(1L);
        defect.setTaskName("测试任务");
        defect.setType(DefectTypeEnum.STRUCTURAL_CRACK);
        defect.setLocation(BigDecimal.valueOf(100.0));
        defect.setIsValid(true);
        defect.setSeverity(SeverityLevelEnum.HIGH);
        defect.setDefectLength(BigDecimal.valueOf(10.5));
        defect.setDefectArea(BigDecimal.valueOf(20.25));
        defect.setDefectCount(5);
        defect.setSuggestion("建议修复");
        defect.setFoundTime(LocalDateTime.now());
        defect.setStatus(DefectStatusEnum.PENDING);
        defects.add(defect);
        
        // 创建一个部分属性为空的缺陷对象
        Defect defect2 = new Defect();
        defect2.setId(2L);
        defects.add(defect2);
        
        // 创建一个包含特殊字符的缺陷对象
        Defect defect3 = new Defect();
        defect3.setId(3L);
        defect3.setTaskName("包含,逗号和\"引号的任务");
        defect3.setSuggestion("包含\n换行符的建议");
        defects.add(defect3);

        // 模拟依赖方法的行为
        when(defectMapper.selectByQuery(any(DefectQuery.class))).thenReturn(defects);

        // 执行测试
        ByteArrayOutputStream result = exportService.exportDefectsToCsv(query, response);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.size() > 0);
        
        // 验证响应头
        assertEquals("text/csv;charset=UTF-8", response.getContentType());
        assertTrue(response.getHeader("Content-Disposition").contains("attachment; filename="));
        
        // 验证方法调用
        verify(defectMapper).selectByQuery(any(DefectQuery.class));
    }
    
    @Test
    void testExportDefectsToCsvWithEmptyList() throws IOException {
        // 准备测试数据
        DefectQuery query = new DefectQuery();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 模拟依赖方法的行为 - 返回空列表
        when(defectMapper.selectByQuery(any(DefectQuery.class))).thenReturn(Collections.emptyList());

        // 执行测试
        ByteArrayOutputStream result = exportService.exportDefectsToCsv(query, response);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.size() > 0); // 至少包含表头
        
        // 验证方法调用
        verify(defectMapper).selectByQuery(any(DefectQuery.class));
    }
    
    @Test
    void testExportDefectsToCsvWithIOException() throws IOException {
        // 准备测试数据
        DefectQuery query = new DefectQuery();
        MockHttpServletResponse response = mock(MockHttpServletResponse.class);
        
        // 模拟依赖方法的行为
        when(defectMapper.selectByQuery(any(DefectQuery.class))).thenReturn(new ArrayList<>());
        
        // 模拟IOException
        doThrow(new IOException("模拟IO异常")).when(response).flushBuffer();
        
        // 执行测试
        ByteArrayOutputStream result = exportService.exportDefectsToCsv(query, response);
        
        // 验证结果
        assertNotNull(result);
        
        // 验证方法调用
        verify(defectMapper).selectByQuery(any(DefectQuery.class));
        verify(response).flushBuffer();
    }
    
    @Test
    void testEscapeCsv() throws Exception {
        // 使用反射访问私有方法
        java.lang.reflect.Method escapeCsvMethod = ExportServiceImpl.class.getDeclaredMethod("escapeCsv", String.class);
        escapeCsvMethod.setAccessible(true);
        
        // 测试普通字符串
        String normal = "普通字符串";
        assertEquals(normal, escapeCsvMethod.invoke(exportService, normal));
        
        // 测试包含逗号的字符串
        String withComma = "包含,逗号";
        assertEquals("\"包含,逗号\"", escapeCsvMethod.invoke(exportService, withComma));
        
        // 测试包含引号的字符串
        String withQuote = "包含\"引号";
        assertEquals("\"包含\"\"引号\"", escapeCsvMethod.invoke(exportService, withQuote));
        
        // 测试包含换行符的字符串
        String withNewline = "包含\n换行";
        assertEquals("\"包含\n换行\"", escapeCsvMethod.invoke(exportService, withNewline));
        
        // 测试包含回车符的字符串
        String withReturn = "包含\r回车";
        assertEquals("\"包含\r回车\"", escapeCsvMethod.invoke(exportService, withReturn));
        
        // 测试null值
        assertEquals("", escapeCsvMethod.invoke(exportService, (Object)null));
    }
} 