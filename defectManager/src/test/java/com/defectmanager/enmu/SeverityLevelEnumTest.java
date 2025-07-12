package com.defectmanager.enmu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeverityLevelEnumTest {

    @Test
    void testEnumValues() {
        // 测试枚举值的数量
        assertEquals(3, SeverityLevelEnum.values().length);
        
        // 测试具体的枚举值
        assertEquals("高", SeverityLevelEnum.HIGH.getDbValue());
        assertEquals("高", SeverityLevelEnum.HIGH.getDisplayName());
        
        assertEquals("中", SeverityLevelEnum.MEDIUM.getDbValue());
        assertEquals("中", SeverityLevelEnum.MEDIUM.getDisplayName());
        
        assertEquals("低", SeverityLevelEnum.LOW.getDbValue());
        assertEquals("低", SeverityLevelEnum.LOW.getDisplayName());
    }
    
    @Test
    void testFromDbValue() {
        // 测试正确的转换
        assertEquals(SeverityLevelEnum.HIGH, SeverityLevelEnum.fromDbValue("高"));
        assertEquals(SeverityLevelEnum.MEDIUM, SeverityLevelEnum.fromDbValue("中"));
        assertEquals(SeverityLevelEnum.LOW, SeverityLevelEnum.fromDbValue("低"));
        
        // 测试无效值的异常
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            SeverityLevelEnum.fromDbValue("不存在的等级");
        });
        
        assertTrue(exception.getMessage().contains("无效的严重等级"));
    }
} 