package com.common.domain.vo.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Schema(description = "菜单查询响应模型")
@Data
public class MenuTreeVO {
    @Schema(description = "菜单ID", example = "1")
    private Long menuId;

    @Schema(description = "菜单名称", example = "系统管理")
    private String menuName;

    @Schema(description = "路由地址", example = "/system")
    private String path;

    @Schema(description = "菜单图标", example = "/image")
    private String icon;

    @Schema(description = "权限标识", example = "system:user:list")
    private String perms;

    @Schema(description = "菜单排序", example = "1")
    private Integer orderNum;

    @Schema(description = "菜单类型(M:目录 C:菜单 F:按钮)", example = "M")
    private String menuType;

    @Schema(description = "组件路径（类型为C时必填）", example = "system/user/index")
    private String component;

    @Schema(description = "菜单状态(0:隐藏 1:显示)", example = "1")
    private Integer visible;

    @Schema(description = "子菜单")
    private List<MenuTreeVO> children;

    @Schema(description = "父菜单ID", example = "1")
    private Long parentId;
}