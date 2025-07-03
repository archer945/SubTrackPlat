package com.common.domain.dto.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 用户角色分配DTO
 */
@Data
@Schema(description = "用户角色分配")
public class UserRoleDTO {

    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "角色ID列表", example = "[1,2]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "角色列表不能为空")
    private List<Long> roleIds;
} 