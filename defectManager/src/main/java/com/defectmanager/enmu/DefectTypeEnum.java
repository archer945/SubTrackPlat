package com.defectmanager.enmu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum DefectTypeEnum {
    STRUCTURAL_CRACK("结构裂缝", "结构裂缝"),
    LEAKAGE("渗水", "渗水"),
    EQUIPMENT_FAILURE("设备故障", "设备故障"),
    LIGHTING_ISSUE("照明问题", "照明问题"),
    DETACHMENT("脱落", "脱落"),
    CORROSION("腐蚀", "腐蚀"),
    SEEPAGE("渗漏", "渗漏"),
    EQUIPMENT_ABNORMALITY("设备异常", "设备异常");

    @EnumValue
    private final String dbValue;
    private final String displayName;

    DefectTypeEnum(String dbValue, String displayName) {
        this.dbValue = dbValue;
        this.displayName = displayName;
    }

    public static DefectTypeEnum fromDbValue(String dbValue) {
        for (DefectTypeEnum type : values()) {
            if (type.dbValue.equals(dbValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的缺陷类型: " + dbValue);
    }
}