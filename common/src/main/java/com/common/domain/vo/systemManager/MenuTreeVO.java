package com.common.domain.vo.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "菜单状态(0:隐藏 1:显示)", example = "1")
    private Integer visible;

    @Schema(description = "子菜单")
    private List<MenuTreeVO> children;
}