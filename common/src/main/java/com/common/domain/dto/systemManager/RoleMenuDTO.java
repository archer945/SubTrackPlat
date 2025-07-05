package com.common.domain.dto.systemManager;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 角色菜单权限DTO
 */
@Data
public class RoleMenuDTO {
    /**
     * 菜单ID列表
     */
    @NotEmpty(message = "菜单ID列表不能为空")
    private List<Long> menuIds;
} 