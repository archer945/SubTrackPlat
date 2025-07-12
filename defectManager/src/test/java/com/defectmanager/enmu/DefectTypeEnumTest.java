package com.defectmanager.enmu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefectTypeEnumTest {

    @Test
    void testEnumValues() {
        // 测试枚举值的数量
        assertEquals(8, DefectTypeEnum.values().length);
        
        // 测试具体的枚举值
        assertEquals("结构裂缝", DefectTypeEnum.STRUCTURAL_CRACK.getDbValue());
        assertEquals("结构裂缝", DefectTypeEnum.STRUCTURAL_CRACK.getDisplayName());
        
        assertEquals("渗水", DefectTypeEnum.LEAKAGE.getDbValue());
        assertEquals("渗水", DefectTypeEnum.LEAKAGE.getDisplayName());
        
        assertEquals("设备故障", DefectTypeEnum.EQUIPMENT_FAILURE.getDbValue());
        assertEquals("设备故障", DefectTypeEnum.EQUIPMENT_FAILURE.getDisplayName());
        
        assertEquals("照明问题", DefectTypeEnum.LIGHTING_ISSUE.getDbValue());
        assertEquals("照明问题", DefectTypeEnum.LIGHTING_ISSUE.getDisplayName());
        
        assertEquals("脱落", DefectTypeEnum.DETACHMENT.getDbValue());
        assertEquals("脱落", DefectTypeEnum.DETACHMENT.getDisplayName());
        
        assertEquals("腐蚀", DefectTypeEnum.CORROSION.getDbValue());
        assertEquals("腐蚀", DefectTypeEnum.CORROSION.getDisplayName());
        
        assertEquals("渗漏", DefectTypeEnum.SEEPAGE.getDbValue());
        assertEquals("渗漏", DefectTypeEnum.SEEPAGE.getDisplayName());
        
        assertEquals("设备异常", DefectTypeEnum.EQUIPMENT_ABNORMALITY.getDbValue());
        assertEquals("设备异常", DefectTypeEnum.EQUIPMENT_ABNORMALITY.getDisplayName());
    }
    
    @Test
    void testFromDbValue() {
        // 测试正确的转换
        assertEquals(DefectTypeEnum.STRUCTURAL_CRACK, DefectTypeEnum.fromDbValue("结构裂缝"));
        assertEquals(DefectTypeEnum.LEAKAGE, DefectTypeEnum.fromDbValue("渗水"));
        assertEquals(DefectTypeEnum.EQUIPMENT_FAILURE, DefectTypeEnum.fromDbValue("设备故障"));
        assertEquals(DefectTypeEnum.LIGHTING_ISSUE, DefectTypeEnum.fromDbValue("照明问题"));
        assertEquals(DefectTypeEnum.DETACHMENT, DefectTypeEnum.fromDbValue("脱落"));
        assertEquals(DefectTypeEnum.CORROSION, DefectTypeEnum.fromDbValue("腐蚀"));
        assertEquals(DefectTypeEnum.SEEPAGE, DefectTypeEnum.fromDbValue("渗漏"));
        assertEquals(DefectTypeEnum.EQUIPMENT_ABNORMALITY, DefectTypeEnum.fromDbValue("设备异常"));
        
        // 测试无效值的异常
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DefectTypeEnum.fromDbValue("不存在的类型");
        });
        
        assertTrue(exception.getMessage().contains("无效的缺陷类型"));
    }
} 