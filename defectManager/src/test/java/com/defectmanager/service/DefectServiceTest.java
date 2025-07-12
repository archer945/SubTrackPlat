package com.defectmanager.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.defectmanager.entity.Defect;
import com.defectmanager.entity.DefectImage;
import com.defectmanager.enmu.DefectStatusEnum;
import com.defectmanager.enmu.DefectTypeEnum;
import com.defectmanager.enmu.SeverityLevelEnum;
import com.defectmanager.mapper.DefectImageMapper;
import com.defectmanager.mapper.DefectMapper;
import com.defectmanager.query.DefectQuery;
import com.defectmanager.service.impl.DefectServiceImpl;
import com.defectmanager.utils.AliOSSUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DefectServiceTest {

    @Mock
    private DefectMapper defectMapper;
    
    @Mock
    private DefectImageMapper imageMapper;
    
    @Mock
    private ImageService imageService;
    
    @Mock
    private AliOSSUtils ossUtils;
    
    @InjectMocks
    private DefectServiceImpl defectService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testQueryByCondition() {
        // 准备测试数据
        DefectQuery query = new DefectQuery();
        query.setPageIndex(1);
        query.setPageSize(10);
        query.setType(DefectTypeEnum.STRUCTURAL_CRACK.getDbValue());
        query.setStatus(DefectStatusEnum.PENDING.getDbValue());
        
        List<Defect> defectList = new ArrayList<>();
        Defect defect = new Defect();
        defect.setId(1L);
        defect.setType(DefectTypeEnum.STRUCTURAL_CRACK);
        defect.setStatus(DefectStatusEnum.PENDING);
        defectList.add(defect);
        
        Page<Defect> defectPage = new Page<>(1, 10);
        defectPage.setRecords(defectList);
        defectPage.setTotal(1);
        
        List<DefectImage> imageList = new ArrayList<>();
        DefectImage image = new DefectImage();
        image.setId(1L);
        image.setDefectId(1L);
        image.setImageUrl("http://example.com/image.jpg");
        imageList.add(image);
        
        // 模拟依赖方法的行为
        when(defectMapper.selectPage(any(), any())).thenReturn(defectPage);
        when(imageMapper.selectList(any())).thenReturn(imageList);
        
        // 执行测试
        Page<Defect> result = defectService.queryByCondition(query);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(1L, result.getRecords().get(0).getId());
        assertEquals(1, result.getRecords().get(0).getImages().size());
        assertEquals("http://example.com/image.jpg", result.getRecords().get(0).getImages().get(0).getImageUrl());
        
        verify(defectMapper).selectPage(any(), any());
        verify(imageMapper).selectList(any());
    }
    
    @Test
    void testQueryByConditionWithEmptyResult() {
        // 准备测试数据
        DefectQuery query = new DefectQuery();
        query.setPageIndex(1);
        query.setPageSize(10);
        
        Page<Defect> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(new ArrayList<>());
        emptyPage.setTotal(0);
        
        // 模拟依赖方法的行为
        when(defectMapper.selectPage(any(), any())).thenReturn(emptyPage);
        
        // 执行测试
        Page<Defect> result = defectService.queryByCondition(query);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getRecords().size());
        
        verify(defectMapper).selectPage(any(), any());
        verify(imageMapper, never()).selectList(any());
    }
    
    @Test
    void testAddDefect() {
        // 准备测试数据
        Defect defect = new Defect();
        defect.setDescription("测试缺陷");
        defect.setType(DefectTypeEnum.STRUCTURAL_CRACK);
        defect.setSeverity(SeverityLevelEnum.HIGH);
        
        when(defectMapper.selectCount(any())).thenReturn(5L);
        when(defectMapper.insert(any())).thenReturn(1);
        
        // 执行测试
        Defect result = defectService.add(defect);
        
        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getCode());
        assertTrue(result.getCode().startsWith("DEF-"));
        assertEquals(DefectStatusEnum.PENDING, result.getStatus());
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
        
        verify(defectMapper).selectCount(any());
        verify(defectMapper).insert(any());
    }
    
    @Test
    void testDeleteDefectSuccess() {
        // 准备测试数据
        Long defectId = 1L;
        Defect defect = new Defect();
        defect.setId(defectId);
        
        List<DefectImage> imageList = new ArrayList<>();
        DefectImage image = new DefectImage();
        image.setId(1L);
        image.setDefectId(defectId);
        image.setImageUrl("http://example.com/image.jpg");
        imageList.add(image);
        
        // 模拟依赖方法的行为
        when(defectMapper.selectById(defectId)).thenReturn(defect);
        when(imageService.getImagesByDefectId(defectId)).thenReturn(imageList);
        when(defectMapper.deleteById(defectId)).thenReturn(1);
        
        // 执行测试
        Boolean result = defectService.deleteDefect(defectId);
        
        // 验证结果
        assertTrue(result);
        verify(defectMapper).selectById(defectId);
        verify(imageService).getImagesByDefectId(defectId);
        verify(defectMapper).deleteById(defectId);
    }
    
    @Test
    void testDeleteDefectNotFound() {
        // 准备测试数据
        Long defectId = 999L;
        
        // 模拟依赖方法的行为
        when(defectMapper.selectById(defectId)).thenReturn(null);
        
        // 执行测试
        Boolean result = defectService.deleteDefect(defectId);
        
        // 验证结果
        assertFalse(result);
        verify(defectMapper).selectById(defectId);
        verify(imageService, never()).getImagesByDefectId(anyLong());
        verify(defectMapper, never()).deleteById(anyLong());
    }
    
    @Test
    void testUpdateStatusSuccess() {
        // 准备测试数据
        Long defectId = 1L;
        Long operatorId = 10L;
        DefectStatusEnum newStatus = DefectStatusEnum.CONFIRMED;
        
        Defect defect = new Defect();
        defect.setId(defectId);
        defect.setStatus(DefectStatusEnum.PENDING);
        
        // 模拟依赖方法的行为
        when(defectMapper.selectById(defectId)).thenReturn(defect);
        when(defectMapper.updateById(any())).thenReturn(1);
        
        // 执行测试
        boolean result = defectService.updateStatus(defectId, newStatus, operatorId);
        
        // 验证结果
        assertTrue(result);
        verify(defectMapper).selectById(defectId);
        verify(defectMapper).updateById(any());
    }
    
    @Test
    void testUpdateStatusInvalidTransition() {
        // 准备测试数据
        Long defectId = 1L;
        Long operatorId = 10L;
        DefectStatusEnum newStatus = DefectStatusEnum.FIXED;
        
        Defect defect = new Defect();
        defect.setId(defectId);
        defect.setStatus(DefectStatusEnum.PENDING);
        
        // 模拟依赖方法的行为
        when(defectMapper.selectById(defectId)).thenReturn(defect);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            defectService.updateStatus(defectId, newStatus, operatorId);
        });
        
        assertTrue(exception.getMessage().contains("非法状态流转"));
        verify(defectMapper).selectById(defectId);
        verify(defectMapper, never()).updateById(any());
    }
    
    @Test
    void testGetById() {
        // 准备测试数据
        Long defectId = 1L;
        Defect defect = new Defect();
        defect.setId(defectId);
        defect.setDescription("测试缺陷");
        
        // 模拟依赖方法的行为
        when(defectMapper.selectById(defectId)).thenReturn(defect);
        
        // 执行测试
        Defect result = defectService.getById(defectId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(defectId, result.getId());
        assertEquals("测试缺陷", result.getDescription());
        verify(defectMapper).selectById(defectId);
    }
} 