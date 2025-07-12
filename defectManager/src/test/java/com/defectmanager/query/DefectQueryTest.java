package com.defectmanager.query;

import com.defectmanager.enmu.DefectStatusEnum;
import com.defectmanager.enmu.DefectTypeEnum;
import com.defectmanager.enmu.SeverityLevelEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DefectQueryTest {

    @Test
    void testGetterAndSetter() {
        DefectQuery query = new DefectQuery();
        
        String type = "结构裂缝";
        query.setType(type);
        assertEquals(type, query.getType());
        
        String status = "待确认";
        query.setStatus(status);
        assertEquals(status, query.getStatus());
        
        String severity = "高";
        query.setSeverity(severity);
        assertEquals(severity, query.getSeverity());
        
        Boolean isValid = true;
        query.setIsValid(isValid);
        assertEquals(isValid, query.getIsValid());
        
        String taskId = "100";
        query.setTaskId(taskId);
        assertEquals(taskId, query.getTaskId());
        
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        query.setStartTime(startTime);
        assertEquals(startTime, query.getStartTime());
        
        LocalDateTime endTime = LocalDateTime.now();
        query.setEndTime(endTime);
        assertEquals(endTime, query.getEndTime());
    }
    
    @Test
    void testGetTypeEnum() {
        DefectQuery query = new DefectQuery();
        
        // 测试正确的转换
        query.setType("结构裂缝");
        assertEquals(DefectTypeEnum.STRUCTURAL_CRACK, query.getTypeEnum());
        
        // 测试null值
        query.setType(null);
        assertNull(query.getTypeEnum());
    }
    
    @Test
    void testGetStatusEnum() {
        DefectQuery query = new DefectQuery();
        
        // 测试正确的转换
        query.setStatus("待确认");
        assertEquals(DefectStatusEnum.PENDING, query.getStatusEnum());
        
        // 测试null值
        query.setStatus(null);
        assertNull(query.getStatusEnum());
    }
    
    @Test
    void testGetSeverityEnum() {
        DefectQuery query = new DefectQuery();
        
        // 测试正确的转换
        query.setSeverity("高");
        assertEquals(SeverityLevelEnum.HIGH, query.getSeverityEnum());
        
        // 测试null值
        query.setSeverity(null);
        assertNull(query.getSeverityEnum());
    }
    
    @Test
    void testEqualsAndHashCode() {
        DefectQuery query1 = new DefectQuery();
        query1.setType("结构裂缝");
        query1.setStatus("待确认");
        query1.setSeverity("高");
        
        DefectQuery query2 = new DefectQuery();
        query2.setType("结构裂缝");
        query2.setStatus("待确认");
        query2.setSeverity("高");
        
        DefectQuery query3 = new DefectQuery();
        query3.setType("渗水");
        query3.setStatus("已确认");
        query3.setSeverity("中");
        
        // 测试equals
        assertEquals(query1, query1); // 自反性
        assertEquals(query1, query2); // 对称性
        assertNotEquals(query1, query3);
        assertNotEquals(query1, null);
        assertNotEquals(query1, new Object());
        
        // 测试hashCode
        assertEquals(query1.hashCode(), query2.hashCode());
    }
    
    @Test
    void testToString() {
        DefectQuery query = new DefectQuery();
        query.setType("结构裂缝");
        query.setStatus("待确认");
        query.setSeverity("高");
        
        String toString = query.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("type=结构裂缝"));
        assertTrue(toString.contains("status=待确认"));
        assertTrue(toString.contains("severity=高"));
    }
    
    @Test
    void testInvalidEnumValues() {
        DefectQuery query = new DefectQuery();
        
        // 测试无效的类型值
        query.setType("不存在的类型");
        Exception typeException = assertThrows(IllegalArgumentException.class, () -> {
            query.getTypeEnum();
        });
        assertTrue(typeException.getMessage().contains("无效的缺陷类型"));
        
        // 测试无效的状态值
        query.setStatus("不存在的状态");
        Exception statusException = assertThrows(IllegalArgumentException.class, () -> {
            query.getStatusEnum();
        });
        assertTrue(statusException.getMessage().contains("无效的缺陷状态"));
        
        // 测试无效的严重等级值
        query.setSeverity("不存在的等级");
        Exception severityException = assertThrows(IllegalArgumentException.class, () -> {
            query.getSeverityEnum();
        });
        assertTrue(severityException.getMessage().contains("无效的严重等级"));
    }
} 