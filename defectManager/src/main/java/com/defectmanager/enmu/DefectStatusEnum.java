package com.defectmanager.enmu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum DefectStatusEnum {
    PENDING("待确认", "待确认"),
    CONFIRMED("已确认", "已确认"),
    REJECTED("已驳回", "已驳回"),      // 新增
    PROCESSING("处理中", "处理中"),
    FIXED("已整改", "已整改"),
    REVIEW_NEEDED("需复查", "需复查"),  // 新增
    CLOSED("已关闭", "已关闭");

    @EnumValue
    private final String dbValue;
    private final String displayName;

    DefectStatusEnum(String dbValue, String displayName) {
        this.dbValue = dbValue;
        this.displayName = displayName;
    }

    public static DefectStatusEnum fromDbValue(String dbValue) {
        for (DefectStatusEnum status : values()) {
            if (status.getDbValue().equals(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的缺陷状态: " + dbValue);
    }
}