package com.common.domain.dto.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "角色模型")
public class RoleDTO {
    @Schema(description = "角色名称", example = "管理员")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @Schema(description = "权限字符", example = "admin")
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @Schema(description = "角色描述", example = "负责管理.....")
    private String description;

    @Schema(description = "数据权限(1:全部 2:本部门 3:仅本人)", example = "1")
    @NotBlank(message = "数据权限不能为空")
    private String dataScope;

    @Schema(description = "菜单ID列表", example = "[1,2,3]")
    private List<Long> menuIds;
}