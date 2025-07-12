package com.defectmanager.entity;

import com.defectmanager.enmu.DefectStatusEnum;
import com.defectmanager.enmu.DefectTypeEnum;
import com.defectmanager.enmu.FoundMethodEnum;
import com.defectmanager.enmu.SeverityLevelEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefectTest {

    @Test
    void testNoArgsConstructor() {
        Defect defect = new Defect();
        assertNotNull(defect);
    }

    @Test
    void testAllArgsConstructor() {
        Long id = 1L;
        String code = "DEF-001";
        DefectTypeEnum type = DefectTypeEnum.STRUCTURAL_CRACK;
        String description = "测试描述";
        SeverityLevelEnum severity = SeverityLevelEnum.HIGH;
        Boolean isValid = true;
        BigDecimal defectLength = new BigDecimal("10.5");
        BigDecimal defectArea = new BigDecimal("25.0");
        Integer defectCount = 1;
        String suggestion = "建议修复";
        LocalDateTime foundTime = LocalDateTime.now();
        Long foundBy = 100L;
        Long taskId = 200L;
        String taskName = "测试任务";
        FoundMethodEnum foundMethod = FoundMethodEnum.MANUAL_INSPECTION;
        BigDecimal location = new BigDecimal("50.0");
        DefectStatusEnum status = DefectStatusEnum.PENDING;
        Long confirmBy = 101L;
        LocalDateTime confirmTime = LocalDateTime.now().plusHours(1);
        Long handleBy = 102L;
        LocalDateTime handleStart = LocalDateTime.now().plusHours(2);
        LocalDateTime handleEnd = LocalDateTime.now().plusHours(3);
        String result = "已修复";
        LocalDateTime createTime = LocalDateTime.now().minusDays(1);
        LocalDateTime updateTime = LocalDateTime.now();
        List<DefectImage> images = new ArrayList<>();

        Defect defect = new Defect(id, code, type, description, severity, isValid, defectLength, defectArea, defectCount,
                suggestion, foundTime, foundBy, taskId, taskName, foundMethod, location, status, confirmBy, confirmTime,
                handleBy, handleStart, handleEnd, result, createTime, updateTime, images);

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
    void testGetterAndSetter() {
        Defect defect = new Defect();

        Long id = 1L;
        defect.setId(id);
        assertEquals(id, defect.getId());

        String code = "DEF-001";
        defect.setCode(code);
        assertEquals(code, defect.getCode());

        DefectTypeEnum type = DefectTypeEnum.STRUCTURAL_CRACK;
        defect.setType(type);
        assertEquals(type, defect.getType());

        String description = "测试描述";
        defect.setDescription(description);
        assertEquals(description, defect.getDescription());

        SeverityLevelEnum severity = SeverityLevelEnum.HIGH;
        defect.setSeverity(severity);
        assertEquals(severity, defect.getSeverity());

        Boolean isValid = true;
        defect.setIsValid(isValid);
        assertEquals(isValid, defect.getIsValid());

        BigDecimal defectLength = new BigDecimal("10.5");
        defect.setDefectLength(defectLength);
        assertEquals(defectLength, defect.getDefectLength());

        BigDecimal defectArea = new BigDecimal("25.0");
        defect.setDefectArea(defectArea);
        assertEquals(defectArea, defect.getDefectArea());

        Integer defectCount = 2;
        defect.setDefectCount(defectCount);
        assertEquals(defectCount, defect.getDefectCount());

        String suggestion = "建议修复";
        defect.setSuggestion(suggestion);
        assertEquals(suggestion, defect.getSuggestion());

        LocalDateTime foundTime = LocalDateTime.now();
        defect.setFoundTime(foundTime);
        assertEquals(foundTime, defect.getFoundTime());

        Long foundBy = 100L;
        defect.setFoundBy(foundBy);
        assertEquals(foundBy, defect.getFoundBy());

        Long taskId = 200L;
        defect.setTaskId(taskId);
        assertEquals(taskId, defect.getTaskId());

        String taskName = "测试任务";
        defect.setTaskName(taskName);
        assertEquals(taskName, defect.getTaskName());

        FoundMethodEnum foundMethod = FoundMethodEnum.MANUAL_INSPECTION;
        defect.setFoundMethod(foundMethod);
        assertEquals(foundMethod, defect.getFoundMethod());

        BigDecimal location = new BigDecimal("50.0");
        defect.setLocation(location);
        assertEquals(location, defect.getLocation());

        DefectStatusEnum status = DefectStatusEnum.PENDING;
        defect.setStatus(status);
        assertEquals(status, defect.getStatus());

        Long confirmBy = 101L;
        defect.setConfirmBy(confirmBy);
        assertEquals(confirmBy, defect.getConfirmBy());

        LocalDateTime confirmTime = LocalDateTime.now().plusHours(1);
        defect.setConfirmTime(confirmTime);
        assertEquals(confirmTime, defect.getConfirmTime());

        Long handleBy = 102L;
        defect.setHandleBy(handleBy);
        assertEquals(handleBy, defect.getHandleBy());

        LocalDateTime handleStart = LocalDateTime.now().plusHours(2);
        defect.setHandleStart(handleStart);
        assertEquals(handleStart, defect.getHandleStart());

        LocalDateTime handleEnd = LocalDateTime.now().plusHours(3);
        defect.setHandleEnd(handleEnd);
        assertEquals(handleEnd, defect.getHandleEnd());

        String result = "已修复";
        defect.setResult(result);
        assertEquals(result, defect.getResult());

        LocalDateTime createTime = LocalDateTime.now().minusDays(1);
        defect.setCreateTime(createTime);
        assertEquals(createTime, defect.getCreateTime());

        LocalDateTime updateTime = LocalDateTime.now();
        defect.setUpdateTime(updateTime);
        assertEquals(updateTime, defect.getUpdateTime());

        List<DefectImage> images = new ArrayList<>();
        DefectImage image = new DefectImage();
        image.setId(1L);
        images.add(image);
        defect.setImages(images);
        assertEquals(images, defect.getImages());
    }

    @Test
    void testEqualsAndHashCode() {
        Defect defect1 = new Defect();
        defect1.setId(1L);
        defect1.setCode("DEF-001");

        Defect defect2 = new Defect();
        defect2.setId(1L);
        defect2.setCode("DEF-001");

        Defect defect3 = new Defect();
        defect3.setId(2L);
        defect3.setCode("DEF-002");

        // 测试equals
        assertEquals(defect1, defect1); // 自反性
        assertEquals(defect1, defect2); // 对称性
        assertNotEquals(defect1, defect3);
        assertNotEquals(defect1, null);
        assertNotEquals(defect1, new Object());

        // 测试hashCode
        assertEquals(defect1.hashCode(), defect2.hashCode());
    }

    @Test
    void testToString() {
        Defect defect = new Defect();
        defect.setId(1L);
        defect.setCode("DEF-001");
        defect.setDescription("测试描述");

        String toString = defect.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("code=DEF-001"));
        assertTrue(toString.contains("description=测试描述"));
    }
} 