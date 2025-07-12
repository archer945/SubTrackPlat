package com.dashboard.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefectTest {

    @Test
    void testDefectGettersAndSetters() {
        // 创建测试对象
        Defect defect = new Defect();
        
        // 设置所有字段值
        Long id = 1L;
        String code = "DEF-001";
        String type = "结构裂缝";
        String description = "严重的结构裂缝";
        String severity = "高";
        Boolean isValid = true;
        BigDecimal defectLength = new BigDecimal("10.5");
        BigDecimal defectArea = new BigDecimal("25.3");
        Integer defectCount = 2;
        String suggestion = "需要紧急修复";
        LocalDateTime foundTime = LocalDateTime.now();
        Long foundBy = 101L;
        Long taskId = 201L;
        String taskName = "隧道巡检";
        String foundMethod = "人工巡检";
        BigDecimal location = new BigDecimal("500.75");
        String status = "已确认";
        Long confirmBy = 102L;
        LocalDateTime confirmTime = LocalDateTime.now();
        Long handleBy = 103L;
        LocalDateTime handleStart = LocalDateTime.now();
        LocalDateTime handleEnd = LocalDateTime.now().plusHours(2);
        String result = "已修复";
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime updateTime = LocalDateTime.now();
        List<DefectImage> images = Arrays.asList(new DefectImage(), new DefectImage());
        
        defect.setId(id);
        defect.setCode(code);
        defect.setType(type);
        defect.setDescription(description);
        defect.setSeverity(severity);
        defect.setIsValid(isValid);
        defect.setDefectLength(defectLength);
        defect.setDefectArea(defectArea);
        defect.setDefectCount(defectCount);
        defect.setSuggestion(suggestion);
        defect.setFoundTime(foundTime);
        defect.setFoundBy(foundBy);
        defect.setTaskId(taskId);
        defect.setTaskName(taskName);
        defect.setFoundMethod(foundMethod);
        defect.setLocation(location);
        defect.setStatus(status);
        defect.setConfirmBy(confirmBy);
        defect.setConfirmTime(confirmTime);
        defect.setHandleBy(handleBy);
        defect.setHandleStart(handleStart);
        defect.setHandleEnd(handleEnd);
        defect.setResult(result);
        defect.setCreateTime(createTime);
        defect.setUpdateTime(updateTime);
        defect.setImages(images);
        
        // 验证getter方法
        assertEquals(id, defect.getId());
        assertEquals(code, defect.getCode());
        assertEquals(type, defect.getType());
        assertEquals(description, defect.getDescription());
        assertEquals(severity, defect.getSeverity());
        assertEquals(isValid, defect.getIsValid());
        assertEquals(defectLength, defect.getDefectLength());
        assertEquals(defectArea, defect.getDefectArea());
        assertEquals(defectCount, defect.getDefectCount());
        assertEquals(suggestion, defect.getSuggestion());
        assertEquals(foundTime, defect.getFoundTime());
        assertEquals(foundBy, defect.getFoundBy());
        assertEquals(taskId, defect.getTaskId());
        assertEquals(taskName, defect.getTaskName());
        assertEquals(foundMethod, defect.getFoundMethod());
        assertEquals(location, defect.getLocation());
        assertEquals(status, defect.getStatus());
        assertEquals(confirmBy, defect.getConfirmBy());
        assertEquals(confirmTime, defect.getConfirmTime());
        assertEquals(handleBy, defect.getHandleBy());
        assertEquals(handleStart, defect.getHandleStart());
        assertEquals(handleEnd, defect.getHandleEnd());
        assertEquals(result, defect.getResult());
        assertEquals(createTime, defect.getCreateTime());
        assertEquals(updateTime, defect.getUpdateTime());
        assertEquals(images, defect.getImages());
    }
    
    @Test
    void testAllArgsConstructor() {
        // 创建所有参数并测试全参构造函数
        Long id = 1L;
        String code = "DEF-001";
        String type = "结构裂缝";
        String description = "测试描述";
        String severity = "高";
        Boolean isValid = true;
        BigDecimal defectLength = new BigDecimal("10.5");
        BigDecimal defectArea = new BigDecimal("25.3");
        Integer defectCount = 2;
        String suggestion = "测试建议";
        LocalDateTime foundTime = LocalDateTime.now();
        Long foundBy = 101L;
        Long taskId = 201L;
        String taskName = "测试任务";
        String foundMethod = "人工巡检";
        BigDecimal location = new BigDecimal("500.75");
        String status = "已确认";
        Long confirmBy = 102L;
        LocalDateTime confirmTime = LocalDateTime.now();
        Long handleBy = 103L;
        LocalDateTime handleStart = LocalDateTime.now();
        LocalDateTime handleEnd = LocalDateTime.now().plusHours(2);
        String result = "测试结果";
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime updateTime = LocalDateTime.now();
        List<DefectImage> images = Arrays.asList(new DefectImage());
        
        Defect defect = new Defect(id, code, type, description, severity, isValid, defectLength, 
                              defectArea, defectCount, suggestion, foundTime, foundBy, taskId, 
                              taskName, foundMethod, location, status, confirmBy, confirmTime, 
                              handleBy, handleStart, handleEnd, result, createTime, updateTime, images);
                              
        // 验证所有字段
        assertEquals(id, defect.getId());
        assertEquals(code, defect.getCode());
        assertEquals(type, defect.getType());
        assertEquals(description, defect.getDescription());
        assertEquals(severity, defect.getSeverity());
        assertEquals(isValid, defect.getIsValid());
        assertEquals(defectLength, defect.getDefectLength());
        assertEquals(defectArea, defect.getDefectArea());
        assertEquals(defectCount, defect.getDefectCount());
        assertEquals(suggestion, defect.getSuggestion());
        assertEquals(foundTime, defect.getFoundTime());
        assertEquals(foundBy, defect.getFoundBy());
        assertEquals(taskId, defect.getTaskId());
        assertEquals(taskName, defect.getTaskName());
        assertEquals(foundMethod, defect.getFoundMethod());
        assertEquals(location, defect.getLocation());
        assertEquals(status, defect.getStatus());
        assertEquals(confirmBy, defect.getConfirmBy());
        assertEquals(confirmTime, defect.getConfirmTime());
        assertEquals(handleBy, defect.getHandleBy());
        assertEquals(handleStart, defect.getHandleStart());
        assertEquals(handleEnd, defect.getHandleEnd());
        assertEquals(result, defect.getResult());
        assertEquals(createTime, defect.getCreateTime());
        assertEquals(updateTime, defect.getUpdateTime());
        assertEquals(images, defect.getImages());
    }

    @Test
    void testNoArgsConstructor() {
        // 测试无参构造函数
        Defect defect = new Defect();
        assertNotNull(defect);
        
        // 验证默认值
        assertEquals(1, defect.getDefectCount()); // 检查默认值是否为1
    }
    
    @Test
    void testEqualsAndHashCode() {
        // 测试equals和hashCode方法
        Defect defect1 = new Defect();
        defect1.setId(1L);
        defect1.setCode("DEF-001");
        defect1.setType("结构裂缝");
        
        Defect defect2 = new Defect();
        defect2.setId(1L);
        defect2.setCode("DEF-001");
        defect2.setType("结构裂缝");
        
        Defect defect3 = new Defect();
        defect3.setId(2L);
        defect3.setCode("DEF-002");
        defect3.setType("渗水");
        
        // 测试equals方法
        assertEquals(defect1, defect2);
        assertNotEquals(defect1, defect3);
        assertNotEquals(defect1, null);
        assertNotEquals(defect1, new Object());
        
        // 测试自反性
        assertEquals(defect1, defect1);
        
        // 测试对称性
        assertEquals(defect1.equals(defect2), defect2.equals(defect1));
        
        // 测试传递性
        Defect defect4 = new Defect();
        defect4.setId(1L);
        defect4.setCode("DEF-001");
        defect4.setType("结构裂缝");
        assertEquals(defect1, defect4);
        assertEquals(defect2, defect4);
        
        // 测试hashCode方法
        assertEquals(defect1.hashCode(), defect2.hashCode());
        assertNotEquals(defect1.hashCode(), defect3.hashCode());
        
        // 测试一致性
        assertEquals(defect1.hashCode(), defect1.hashCode());
    }
    
    @Test
    void testToString() {
        // 测试toString方法
        Defect defect = new Defect();
        defect.setId(1L);
        defect.setCode("DEF-001");
        defect.setType("结构裂缝");
        defect.setDescription("测试描述");
        defect.setSeverity("高");
        defect.setStatus("已确认");
        defect.setIsValid(true);
        
        String toStringResult = defect.toString();
        
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("code=DEF-001"));
        assertTrue(toStringResult.contains("type=结构裂缝"));
        assertTrue(toStringResult.contains("description=测试描述"));
        assertTrue(toStringResult.contains("severity=高"));
        assertTrue(toStringResult.contains("status=已确认"));
        assertTrue(toStringResult.contains("isValid=true"));
    }
    
    @Test
    void testNullValues() {
        // 测试null值
        Defect defect = new Defect();
        
        defect.setType(null);
        assertNull(defect.getType());
        
        defect.setDescription(null);
        assertNull(defect.getDescription());
        
        defect.setIsValid(null);
        assertNull(defect.getIsValid());
        
        defect.setImages(null);
        assertNull(defect.getImages());
        
        // 测试空列表
        defect.setImages(new ArrayList<>());
        assertEquals(0, defect.getImages().size());
    }
    
    @Test
    void testEdgeCases() {
        // 测试边缘情况
        Defect defect = new Defect();
        
        // 测试极大值
        defect.setId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, defect.getId());
        
        // 测试0
        defect.setDefectCount(0);
        assertEquals(0, defect.getDefectCount());
        
        // 测试负值（实际使用中可能不合理，但测试覆盖）
        defect.setDefectCount(-1);
        assertEquals(-1, defect.getDefectCount());
        
        // 测试BigDecimal精度
        BigDecimal smallValue = new BigDecimal("0.0001");
        defect.setDefectLength(smallValue);
        assertEquals(smallValue, defect.getDefectLength());
        
        // 测试空字符串
        defect.setCode("");
        assertEquals("", defect.getCode());
    }
} 