package com.defectmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.domain.vo.JsonVO;
import com.defectmanager.enmu.DefectStatusEnum;
import com.defectmanager.entity.Defect;
import com.defectmanager.entity.DefectImage;
import com.defectmanager.query.DefectQuery;
import com.defectmanager.service.DefectService;
import com.defectmanager.service.ImageService;
import com.defectmanager.service.exportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DefectControllerTest {

    @Mock
    private DefectService defectService;

    @Mock
    private ImageService imageService;

    @Mock
    private exportService exportService;

    @InjectMocks
    private DefectController defectController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testQuery() {
        // 准备测试数据
        DefectQuery query = new DefectQuery();
        query.setPageIndex(1);
        query.setPageSize(10);

        Page<Defect> page = new Page<>(1, 10);
        List<Defect> defects = new ArrayList<>();
        Defect defect = new Defect();
        defect.setId(1L);
        defects.add(defect);
        page.setRecords(defects);
        page.setTotal(1);

        // 模拟依赖方法的行为
        when(defectService.queryByCondition(any(DefectQuery.class))).thenReturn(page);

        // 执行测试
        JsonVO<Page<Defect>> result = defectController.query(query);

        // 验证结果
        assertNotNull(result);
        assertEquals(10000, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getTotal());
        assertEquals(1, result.getData().getRecords().size());
        assertEquals(1L, result.getData().getRecords().get(0).getId());

        verify(defectService).queryByCondition(any(DefectQuery.class));
    }

    @Test
    void testAdd() {
        // 准备测试数据
        Defect defect = new Defect();
        defect.setId(1L);

        // 模拟依赖方法的行为
        when(defectService.add(any(Defect.class))).thenReturn(defect);

        // 执行测试
        JsonVO<Defect> result = defectController.add(defect);

        // 验证结果
        assertNotNull(result);
        assertEquals(10000, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());

        verify(defectService).add(any(Defect.class));
    }

    @Test
    void testDeleteSuccess() {
        // 准备测试数据
        Long defectId = 1L;

        // 模拟依赖方法的行为
        when(defectService.deleteDefect(defectId)).thenReturn(true);

        // 执行测试
        JsonVO<Boolean> result = defectController.delete(defectId);

        // 验证结果
        assertNotNull(result);
        assertEquals(10000, result.getCode());
        assertTrue(result.getData());

        verify(defectService, times(2)).deleteDefect(defectId);
    }

    @Test
    void testDeleteFail() {
        // 准备测试数据
        Long defectId = 1L;

        // 模拟依赖方法的行为
        when(defectService.deleteDefect(defectId)).thenReturn(false);

        // 执行测试
        JsonVO<Boolean> result = defectController.delete(defectId);

        // 验证结果
        assertNotNull(result);
        assertEquals(9999, result.getCode());
        assertNull(result.getData());

        verify(defectService).deleteDefect(defectId);
    }

    @Test
    void testUpdateStatusSuccess() {
        // 准备测试数据
        Long defectId = 1L;
        String status = "已确认"; // 使用正确的数据库值
        Long operatorId = 10L;

        // 确保我们使用正确的枚举值
        DefectStatusEnum statusEnum = DefectStatusEnum.CONFIRMED;

        // 模拟依赖方法的行为
        when(defectService.updateStatus(eq(defectId), eq(statusEnum), eq(operatorId))).thenReturn(true);

        // 执行测试
        ResponseEntity<String> result = defectController.updateStatus(defectId, status, operatorId);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("状态更新成功", result.getBody());

        verify(defectService).updateStatus(eq(defectId), eq(statusEnum), eq(operatorId));
    }

    @Test
    void testUpdateStatusFail() {
        // 准备测试数据
        Long defectId = 1L;
        String status = "已确认"; // 使用正确的数据库值
        Long operatorId = 10L;

        // 确保我们使用正确的枚举值
        DefectStatusEnum statusEnum = DefectStatusEnum.CONFIRMED;

        // 模拟依赖方法的行为
        when(defectService.updateStatus(eq(defectId), eq(statusEnum), eq(operatorId))).thenReturn(false);

        // 执行测试
        ResponseEntity<String> result = defectController.updateStatus(defectId, status, operatorId);

        // 验证结果
        assertNotNull(result);
        assertEquals(400, result.getStatusCodeValue());
        assertEquals("更新失败", result.getBody());

        verify(defectService).updateStatus(eq(defectId), eq(statusEnum), eq(operatorId));
    }

    @Test
    void testUpdateStatusInvalidStatus() {
        // 准备测试数据
        Long defectId = 1L;
        String status = "INVALID_STATUS";
        Long operatorId = 10L;

        // 执行测试
        ResponseEntity<String> result = defectController.updateStatus(defectId, status, operatorId);

        // 验证结果
        assertNotNull(result);
        assertEquals(400, result.getStatusCodeValue());
        assertTrue(result.getBody().contains("无效的状态值"));

        verify(defectService, never()).updateStatus(anyLong(), any(DefectStatusEnum.class), anyLong());
    }

    @Test
    void testUpdateStatusException() {
        // 准备测试数据
        Long defectId = 1L;
        String status = "已确认"; // 使用正确的数据库值
        Long operatorId = 10L;

        // 确保我们使用正确的枚举值
        DefectStatusEnum statusEnum = DefectStatusEnum.CONFIRMED;

        // 模拟依赖方法的行为
        when(defectService.updateStatus(eq(defectId), eq(statusEnum), eq(operatorId)))
            .thenThrow(new RuntimeException("测试异常"));

        // 执行测试
        ResponseEntity<String> result = defectController.updateStatus(defectId, status, operatorId);

        // 验证结果
        assertNotNull(result);
        assertEquals(400, result.getStatusCodeValue());
        assertEquals("测试异常", result.getBody());

        verify(defectService).updateStatus(eq(defectId), eq(statusEnum), eq(operatorId));
    }

    @Test
    void testGetDefectDetailSuccess() {
        // 准备测试数据
        Long defectId = 1L;
        Defect defect = new Defect();
        defect.setId(defectId);

        List<DefectImage> images = new ArrayList<>();
        DefectImage image = new DefectImage();
        image.setId(1L);
        image.setDefectId(defectId);
        images.add(image);

        // 模拟依赖方法的行为
        when(defectService.getById(defectId)).thenReturn(defect);
        when(imageService.getImagesByDefectId(defectId)).thenReturn(images);

        // 执行测试
        JsonVO<Defect> result = defectController.getDefectDetail(defectId);

        // 验证结果
        assertNotNull(result);
        assertEquals(10000, result.getCode());
        assertNotNull(result.getData());
        assertEquals(defectId, result.getData().getId());
        assertNotNull(result.getData().getImages());
        assertEquals(1, result.getData().getImages().size());

        verify(defectService).getById(defectId);
        verify(imageService).getImagesByDefectId(defectId);
    }

    @Test
    void testGetDefectDetailNotFound() {
        // 准备测试数据
        Long defectId = 999L;

        // 模拟依赖方法的行为
        when(defectService.getById(defectId)).thenReturn(null);

        // 执行测试
        JsonVO<Defect> result = defectController.getDefectDetail(defectId);

        // 验证结果
        assertNotNull(result);
        assertEquals(9999, result.getCode());
        assertNull(result.getData());

        verify(defectService).getById(defectId);
        verify(imageService, never()).getImagesByDefectId(anyLong());
    }

    @Test
    void testExportDefects() throws IOException {
        // 准备测试数据
        DefectQuery query = new DefectQuery();
        MockHttpServletResponse response = new MockHttpServletResponse();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write("测试CSV内容".getBytes());

        // 模拟依赖方法的行为
        when(exportService.exportDefectsToCsv(any(DefectQuery.class), any())).thenReturn(outputStream);

        // 执行测试
        defectController.exportDefects(query, response);

        // 验证结果
        assertEquals("text/csv;charset=UTF-8", response.getContentType());
        assertTrue(response.getHeader("Content-Disposition").contains("attachment; filename="));
        
        verify(exportService).exportDefectsToCsv(any(DefectQuery.class), any());
    }

    @Test
    void testExportDefectsWithException() throws IOException {
        // 准备测试数据
        DefectQuery query = new DefectQuery();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 模拟依赖方法的行为
        when(exportService.exportDefectsToCsv(any(DefectQuery.class), any())).thenThrow(new IOException("导出失败"));

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            defectController.exportDefects(query, response);
        });

        assertTrue(exception.getMessage().contains("导出失败"));
        verify(exportService).exportDefectsToCsv(any(DefectQuery.class), any());
    }
} 