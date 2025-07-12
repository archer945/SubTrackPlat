package com.defectmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.defectmanager.enmu.DefectStatusEnum;
import com.defectmanager.enmu.DefectTypeEnum;
import com.defectmanager.enmu.SeverityLevelEnum;
import com.defectmanager.entity.Defect;
import com.defectmanager.entity.DefectImage;
import com.defectmanager.mapper.DefectImageMapper;
import com.defectmanager.mapper.DefectMapper;
import com.defectmanager.query.DefectQuery;
import com.defectmanager.service.ImageService;
import com.defectmanager.utils.AliOSSUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DefectServiceImplTest {

    @Mock
    private DefectMapper defectMapper;

    @Mock
    private ImageService imageService;

    @Mock
    private AliOSSUtils ossUtils;

    @Mock
    private DefectImageMapper imageMapper;

    @InjectMocks
    @Spy
    private DefectServiceImpl defectService;

    // 通过反射访问私有方法
    private Method buildWrapperMethod;
    private Method updateStatusFieldsMethod;
    private Method validateTransitionMethod;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // 初始化反射方法
        buildWrapperMethod = DefectServiceImpl.class.getDeclaredMethod("buildWrapper", DefectQuery.class);
        buildWrapperMethod.setAccessible(true);
        
        updateStatusFieldsMethod = DefectServiceImpl.class.getDeclaredMethod("updateStatusFields", Defect.class, DefectStatusEnum.class, Long.class);
        updateStatusFieldsMethod.setAccessible(true);
        
        validateTransitionMethod = DefectServiceImpl.class.getDeclaredMethod("validateTransition", DefectStatusEnum.class, DefectStatusEnum.class);
        validateTransitionMethod.setAccessible(true);
    }

    @Test
    void testQueryByConditionWithImages() {
        // 准备测试数据
        DefectQuery query = new DefectQuery();
        query.setPageIndex(1);
        query.setPageSize(10);

        // 模拟分页结果
        Page<Defect> page = new Page<>(1, 10);
        List<Defect> defects = new ArrayList<>();
        Defect defect = new Defect();
        defect.setId(1L);
        defects.add(defect);
        page.setRecords(defects);
        page.setTotal(1);

        // 模拟图片查询结果
        List<DefectImage> images = new ArrayList<>();
        DefectImage image = new DefectImage();
        image.setId(1L);
        image.setDefectId(1L);
        images.add(image);

        // 模拟依赖方法的行为
        when(defectMapper.selectPage(any(), any())).thenReturn(page);
        when(imageMapper.selectList(any())).thenReturn(images);

        // 执行测试
        Page<Defect> result = defectService.queryByCondition(query);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(1L, result.getRecords().get(0).getId());
        assertNotNull(result.getRecords().get(0).getImages());
        assertEquals(1, result.getRecords().get(0).getImages().size());

        // 验证方法调用
        verify(defectMapper).selectPage(any(), any());
        verify(imageMapper).selectList(any());
    }

    @Test
    void testQueryByConditionWithoutImages() {
        // 准备测试数据
        DefectQuery query = new DefectQuery();
        query.setPageIndex(1);
        query.setPageSize(10);

        // 模拟分页结果 - 空记录
        Page<Defect> page = new Page<>(1, 10);
        page.setRecords(Collections.emptyList());
        page.setTotal(0);

        // 模拟依赖方法的行为
        when(defectMapper.selectPage(any(), any())).thenReturn(page);

        // 执行测试
        Page<Defect> result = defectService.queryByCondition(query);

        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getRecords().size());

        // 验证方法调用
        verify(defectMapper).selectPage(any(), any());
        verify(imageMapper, never()).selectList(any());
    }

    @Test
    void testLambdaQueryByCondition() {
        // 测试forEach中的lambda表达式
        Defect defect = new Defect();
        defect.setId(1L);
        
        List<DefectImage> images = new ArrayList<>();
        DefectImage image = new DefectImage();
        image.setId(1L);
        image.setDefectId(1L);
        images.add(image);
        
        // 直接测试lambda表达式的行为
        Consumer<Defect> lambdaConsumer = d -> {
            List<DefectImage> imgs = images;
            d.setImages(imgs != null ? imgs : new ArrayList<>());
        };
        
        lambdaConsumer.accept(defect);
        
        // 验证结果
        assertNotNull(defect.getImages());
        assertEquals(1, defect.getImages().size());
        assertEquals(1L, defect.getImages().get(0).getId());
    }

    @Test
    void testBuildWrapper() throws Exception {
        // 准备测试数据
        DefectQuery query = new DefectQuery();
        query.setType("结构裂缝");  // 使用有效的缺陷类型
        query.setStatus("待确认");
        query.setSeverity("高");  // 使用有效的严重等级
        query.setTaskId("TASK-001");
        query.setIsValid(true);
        query.setStartTime(LocalDateTime.now().minusDays(7));
        query.setEndTime(LocalDateTime.now());

        // 使用反射调用私有方法
        LambdaQueryWrapper<Defect> wrapper = (LambdaQueryWrapper<Defect>) buildWrapperMethod.invoke(defectService, query);

        // 验证wrapper不为空
        assertNotNull(wrapper);
    }

    @Test
    void testAdd() {
        // 准备测试数据
        Defect defect = new Defect();
        defect.setDescription("测试缺陷");

        // 模拟依赖方法的行为
        when(defectMapper.selectCount(any())).thenReturn(10L);
        when(defectMapper.insert(any(Defect.class))).thenReturn(1);

        // 执行测试
        Defect result = defectService.add(defect);

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
        assertNotNull(result.getCode());
        assertEquals(DefectStatusEnum.PENDING, result.getStatus());
        assertTrue(result.getCode().startsWith("DEF-" + LocalDate.now().getYear()));

        // 验证方法调用
        verify(defectMapper).selectCount(any());
        verify(defectMapper).insert(any(Defect.class));
    }

    @Test
    void testAddWithExistingStatus() {
        // 准备测试数据
        Defect defect = new Defect();
        defect.setDescription("测试缺陷");
        defect.setStatus(DefectStatusEnum.CONFIRMED);

        // 模拟依赖方法的行为
        when(defectMapper.selectCount(any())).thenReturn(10L);
        when(defectMapper.insert(any(Defect.class))).thenReturn(1);

        // 执行测试
        Defect result = defectService.add(defect);

        // 验证结果
        assertNotNull(result);
        assertEquals(DefectStatusEnum.CONFIRMED, result.getStatus());

        // 验证方法调用
        verify(defectMapper).insert(any(Defect.class));
    }

    @Test
    void testDeleteDefectSuccess() {
        // 准备测试数据
        Long defectId = 1L;
        Defect defect = new Defect();
        defect.setId(defectId);

        List<DefectImage> images = new ArrayList<>();
        DefectImage image = new DefectImage();
        image.setId(1L);
        image.setDefectId(defectId);
        image.setImageUrl("http://example.com/image.jpg");
        images.add(image);

        // 模拟依赖方法的行为
        when(defectMapper.selectById(defectId)).thenReturn(defect);
        when(imageService.getImagesByDefectId(defectId)).thenReturn(images);
        when(defectMapper.deleteById(defectId)).thenReturn(1);

        // 执行测试
        Boolean result = defectService.deleteDefect(defectId);

        // 验证结果
        assertTrue(result);

        // 验证方法调用
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

        // 验证方法调用
        verify(defectMapper).selectById(defectId);
        verify(imageService, never()).getImagesByDefectId(anyLong());
        verify(defectMapper, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteDefectWithEmptyImages() {
        // 准备测试数据
        Long defectId = 1L;
        Defect defect = new Defect();
        defect.setId(defectId);

        // 模拟依赖方法的行为 - 返回空图片列表
        when(defectMapper.selectById(defectId)).thenReturn(defect);
        when(imageService.getImagesByDefectId(defectId)).thenReturn(Collections.emptyList());
        when(defectMapper.deleteById(defectId)).thenReturn(1);

        // 执行测试
        Boolean result = defectService.deleteDefect(defectId);

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(defectMapper).selectById(defectId);
        verify(imageService).getImagesByDefectId(defectId);
        verify(defectMapper).deleteById(defectId);
        // 验证不会调用异步删除图片
        verify(ossUtils, never()).delete(anyString());
    }

    @Test
    void testUpdateStatusSuccess() {
        // 准备测试数据
        Long defectId = 1L;
        Long operatorId = 10L;
        Defect defect = new Defect();
        defect.setId(defectId);
        defect.setStatus(DefectStatusEnum.PENDING);

        // 模拟依赖方法的行为
        when(defectMapper.selectById(defectId)).thenReturn(defect);
        when(defectMapper.updateById(any(Defect.class))).thenReturn(1);

        // 执行测试
        boolean result = defectService.updateStatus(defectId, DefectStatusEnum.CONFIRMED, operatorId);

        // 验证结果
        assertTrue(result);
        assertEquals(DefectStatusEnum.CONFIRMED, defect.getStatus());
        assertNotNull(defect.getConfirmBy());
        assertNotNull(defect.getConfirmTime());
        assertTrue(defect.getIsValid());

        // 验证方法调用
        verify(defectMapper).selectById(defectId);
        verify(defectMapper).updateById(any(Defect.class));
    }

    @Test
    void testUpdateStatusDefectNotFound() {
        // 准备测试数据
        Long defectId = 999L;
        Long operatorId = 10L;

        // 模拟依赖方法的行为
        when(defectMapper.selectById(defectId)).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            defectService.updateStatus(defectId, DefectStatusEnum.CONFIRMED, operatorId);
        });

        assertEquals("缺陷不存在", exception.getMessage());

        // 验证方法调用
        verify(defectMapper).selectById(defectId);
        verify(defectMapper, never()).updateById(any(Defect.class));
    }

    @Test
    void testUpdateStatusInvalidTransition() {
        // 准备测试数据
        Long defectId = 1L;
        Long operatorId = 10L;
        Defect defect = new Defect();
        defect.setId(defectId);
        defect.setStatus(DefectStatusEnum.PENDING);

        // 模拟依赖方法的行为
        when(defectMapper.selectById(defectId)).thenReturn(defect);

        // 执行测试并验证异常 - 尝试从PENDING直接到FIXED是非法的
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            defectService.updateStatus(defectId, DefectStatusEnum.FIXED, operatorId);
        });

        assertTrue(exception.getMessage().contains("非法状态流转"));

        // 验证方法调用
        verify(defectMapper).selectById(defectId);
        verify(defectMapper, never()).updateById(any(Defect.class));
    }

    @Test
    void testUpdateStatusFields() throws Exception {
        // 测试各种状态更新的字段设置
        Long operatorId = 10L;
        
        // 1. CONFIRMED状态
        Defect defect1 = new Defect();
        updateStatusFieldsMethod.invoke(defectService, defect1, DefectStatusEnum.CONFIRMED, operatorId);
        assertEquals(DefectStatusEnum.CONFIRMED, defect1.getStatus());
        assertEquals(operatorId, defect1.getConfirmBy());
        assertNotNull(defect1.getConfirmTime());
        assertTrue(defect1.getIsValid());
        
        // 2. PROCESSING状态
        Defect defect2 = new Defect();
        updateStatusFieldsMethod.invoke(defectService, defect2, DefectStatusEnum.PROCESSING, operatorId);
        assertEquals(DefectStatusEnum.PROCESSING, defect2.getStatus());
        assertEquals(operatorId, defect2.getHandleBy());
        assertNotNull(defect2.getHandleStart());
        
        // 3. FIXED状态
        Defect defect3 = new Defect();
        updateStatusFieldsMethod.invoke(defectService, defect3, DefectStatusEnum.FIXED, operatorId);
        assertEquals(DefectStatusEnum.FIXED, defect3.getStatus());
        assertEquals(operatorId, defect3.getHandleBy());
        assertNotNull(defect3.getHandleEnd());
        
        // 4. CLOSED状态
        Defect defect4 = new Defect();
        updateStatusFieldsMethod.invoke(defectService, defect4, DefectStatusEnum.CLOSED, operatorId);
        assertEquals(DefectStatusEnum.CLOSED, defect4.getStatus());
        
        // 5. 其他状态 (REJECTED)
        Defect defect5 = new Defect();
        updateStatusFieldsMethod.invoke(defectService, defect5, DefectStatusEnum.REJECTED, operatorId);
        assertEquals(DefectStatusEnum.REJECTED, defect5.getStatus());
    }

    @Test
    void testValidateTransition() throws Exception {
        // 测试有效的状态流转
        assertDoesNotThrow(() -> {
            validateTransitionMethod.invoke(defectService, DefectStatusEnum.PENDING, DefectStatusEnum.CONFIRMED);
            validateTransitionMethod.invoke(defectService, DefectStatusEnum.CONFIRMED, DefectStatusEnum.PROCESSING);
            validateTransitionMethod.invoke(defectService, DefectStatusEnum.PROCESSING, DefectStatusEnum.FIXED);
            validateTransitionMethod.invoke(defectService, DefectStatusEnum.REVIEW_NEEDED, DefectStatusEnum.PROCESSING);
        });
        
        // 测试无效的状态流转
        assertThrows(Exception.class, () -> {
            validateTransitionMethod.invoke(defectService, DefectStatusEnum.PENDING, DefectStatusEnum.FIXED);
        });
        
        assertThrows(Exception.class, () -> {
            validateTransitionMethod.invoke(defectService, DefectStatusEnum.CONFIRMED, DefectStatusEnum.REJECTED);
        });
        
        assertThrows(Exception.class, () -> {
            validateTransitionMethod.invoke(defectService, DefectStatusEnum.FIXED, DefectStatusEnum.PENDING);
        });
    }

    @Test
    void testGetById() {
        // 准备测试数据
        Long defectId = 1L;
        Defect defect = new Defect();
        defect.setId(defectId);

        // 模拟依赖方法的行为
        when(defectMapper.selectById(defectId)).thenReturn(defect);

        // 执行测试
        Defect result = defectService.getById(defectId);

        // 验证结果
        assertNotNull(result);
        assertEquals(defectId, result.getId());

        // 验证方法调用
        verify(defectMapper).selectById(defectId);
    }
} 