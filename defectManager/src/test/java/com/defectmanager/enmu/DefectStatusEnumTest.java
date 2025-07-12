package com.defectmanager.enmu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefectStatusEnumTest {

    @Test
    void testEnumValues() {
        // 测试枚举值的数量
        assertEquals(7, DefectStatusEnum.values().length);
        
        // 测试具体的枚举值
        assertEquals("待确认", DefectStatusEnum.PENDING.getDbValue());
        assertEquals("待确认", DefectStatusEnum.PENDING.getDisplayName());
        
        assertEquals("已确认", DefectStatusEnum.CONFIRMED.getDbValue());
        assertEquals("已确认", DefectStatusEnum.CONFIRMED.getDisplayName());
        
        assertEquals("已驳回", DefectStatusEnum.REJECTED.getDbValue());
        assertEquals("已驳回", DefectStatusEnum.REJECTED.getDisplayName());
        
        assertEquals("处理中", DefectStatusEnum.PROCESSING.getDbValue());
        assertEquals("处理中", DefectStatusEnum.PROCESSING.getDisplayName());
        
        assertEquals("已整改", DefectStatusEnum.FIXED.getDbValue());
        assertEquals("已整改", DefectStatusEnum.FIXED.getDisplayName());
        
        assertEquals("需复查", DefectStatusEnum.REVIEW_NEEDED.getDbValue());
        assertEquals("需复查", DefectStatusEnum.REVIEW_NEEDED.getDisplayName());
        
        assertEquals("已关闭", DefectStatusEnum.CLOSED.getDbValue());
        assertEquals("已关闭", DefectStatusEnum.CLOSED.getDisplayName());
    }
    
    @Test
    void testFromDbValue() {
        // 测试正确的转换
        assertEquals(DefectStatusEnum.PENDING, DefectStatusEnum.fromDbValue("待确认"));
        assertEquals(DefectStatusEnum.CONFIRMED, DefectStatusEnum.fromDbValue("已确认"));
        assertEquals(DefectStatusEnum.REJECTED, DefectStatusEnum.fromDbValue("已驳回"));
        assertEquals(DefectStatusEnum.PROCESSING, DefectStatusEnum.fromDbValue("处理中"));
        assertEquals(DefectStatusEnum.FIXED, DefectStatusEnum.fromDbValue("已整改"));
        assertEquals(DefectStatusEnum.REVIEW_NEEDED, DefectStatusEnum.fromDbValue("需复查"));
        assertEquals(DefectStatusEnum.CLOSED, DefectStatusEnum.fromDbValue("已关闭"));
        
        // 测试无效值的异常
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DefectStatusEnum.fromDbValue("不存在的状态");
        });
        
        assertTrue(exception.getMessage().contains("无效的缺陷状态"));
    }
} 