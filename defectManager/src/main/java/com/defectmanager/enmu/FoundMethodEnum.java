package com.defectmanager.enmu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum FoundMethodEnum {
    MANUAL_INSPECTION("人工巡检", "人工巡检"),
    EQUIPMENT_DETECTION("设备检测", "设备检测"),
    ROUTINE_CHECK("例行检查", "例行检查");

    @EnumValue
    private final String dbValue;
    private final String displayName;

    FoundMethodEnum(String dbValue, String displayName) {
        this.dbValue = dbValue;
        this.displayName = displayName;
    }

    public static FoundMethodEnum fromDbValue(String dbValue) {
        for (FoundMethodEnum method : values()) {
            if (method.dbValue.equals(dbValue)) {
                return method;
            }
        }
        throw new IllegalArgumentException("无效的发现方式: " + dbValue);
    }
}