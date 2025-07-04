package com.common.domain.vo.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "角色查询响应模型")
@Data
public class RoleVO {
    @Schema(description = "角色ID", example = "1")
    private Long roleId;

    @Schema(description = "角色名称", example = "超级管理员")
    private String roleName;

    @Schema(description = "权限字符", example = "admin")
    private String roleCode;

    @Schema(description = "数据权限(1:全部 2:本部门 3:仅本人)", example = "1")
    private String dataScope;

    @Schema(description = "状态(1:正常 2:禁用)", example = "1")
    private String status;

    @Schema(description = "创建时间", example = "2025-06-17 00:00:00")
    private LocalDateTime createTime;
}
