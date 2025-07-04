package com.common.domain.vo.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Schema(description = "用户菜单响应模型")
@Data
public class UserMenuVO {
    @Schema(description = "菜单ID", example = "1")
    private Long menuId;

    @Schema(description = "菜单名称", example = "系统管理")
    private String menuName;

    @Schema(description = "父菜单ID", example = "0")
    private Long parentId;

    @Schema(description = "路由地址", example = "/system")
    private String path;

    @Schema(description = "组件路径", example = "system/index")
    private String component;

    @Schema(description = "菜单图标", example = "system")
    private String icon;

    @Schema(description = "菜单类型（M目录 C菜单 F按钮）", example = "M")
    private String menuType;

    @Schema(description = "权限标识", example = "system:user:list")
    private String perms;

    @Schema(description = "子菜单")
    private List<UserMenuVO> children;
} 