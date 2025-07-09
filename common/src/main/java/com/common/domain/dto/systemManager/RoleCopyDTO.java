package com.common.domain.dto.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "角色复制模型")
public class RoleCopyDTO {
    @Schema(description = "新角色名称", example = "复制 - 管理员", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @Schema(description = "新权限字符", example = "copy_admin", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @Schema(description = "新角色状态(0:停用 1:正常)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色状态不能为空")
    private String status;

    @Schema(description = "新角色描述", example = "复制的管理员角色")
    private String description;

    @Schema(description = "新数据权限(1:全部 2:本部门 3:仅本人)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据权限不能为空")
    private String dataScope;

    @Schema(description = "是否复制菜单权限", example = "true")
    private Boolean copyPermissions = true;

    @Schema(description = "是否复制数据权限设置", example = "true")
    private Boolean copyDataScope = true;
} 