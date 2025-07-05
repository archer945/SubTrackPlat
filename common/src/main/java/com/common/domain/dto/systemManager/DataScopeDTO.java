package com.common.domain.dto.systemManager;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 数据权限DTO
 */
@Data
public class DataScopeDTO {
    /**
     * 数据范围（1：全部数据权限 2：本部门数据权限 3：本人数据权限）
     */
    @NotBlank(message = "数据范围不能为空")
    private String dataScope;
    
    /**
     * 部门ID列表（当dataScope为2时使用）
     */
    private List<Long> deptIds;
} 