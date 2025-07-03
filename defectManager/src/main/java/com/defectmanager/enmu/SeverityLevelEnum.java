package com.defectmanager.enmu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum SeverityLevelEnum {
    HIGH("高", "高"),
    MEDIUM("中", "中"),
    LOW("低", "低");

    @EnumValue
    private final String dbValue;
    private final String displayName;

    SeverityLevelEnum(String dbValue, String displayName) {
        this.dbValue = dbValue;
        this.displayName = displayName;
    }

    public static SeverityLevelEnum fromDbValue(String dbValue) {
        for (SeverityLevelEnum level : values()) {
            if (level.name().equals(dbValue)) {
                return level;
            }
        }
        throw new IllegalArgumentException("无效的严重等级: " + dbValue);
    }
}