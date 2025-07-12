package com.defectmanager.enmu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FoundMethodEnumTest {

    @Test
    void testEnumValues() {
        // 测试枚举值的数量
        assertEquals(3, FoundMethodEnum.values().length);
        
        // 测试具体的枚举值
        assertEquals("人工巡检", FoundMethodEnum.MANUAL_INSPECTION.getDbValue());
        assertEquals("人工巡检", FoundMethodEnum.MANUAL_INSPECTION.getDisplayName());
        
        assertEquals("设备检测", FoundMethodEnum.EQUIPMENT_DETECTION.getDbValue());
        assertEquals("设备检测", FoundMethodEnum.EQUIPMENT_DETECTION.getDisplayName());
        
        assertEquals("例行检查", FoundMethodEnum.ROUTINE_CHECK.getDbValue());
        assertEquals("例行检查", FoundMethodEnum.ROUTINE_CHECK.getDisplayName());
    }
    
    @Test
    void testFromDbValue() {
        // 测试正确的转换
        assertEquals(FoundMethodEnum.MANUAL_INSPECTION, FoundMethodEnum.fromDbValue("人工巡检"));
        assertEquals(FoundMethodEnum.EQUIPMENT_DETECTION, FoundMethodEnum.fromDbValue("设备检测"));
        assertEquals(FoundMethodEnum.ROUTINE_CHECK, FoundMethodEnum.fromDbValue("例行检查"));
        
        // 测试无效值的异常
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            FoundMethodEnum.fromDbValue("不存在的方式");
        });
        
        assertTrue(exception.getMessage().contains("无效的发现方式"));
    }
} 