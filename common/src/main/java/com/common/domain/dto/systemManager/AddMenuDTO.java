package com.common.domain.dto.systemManager;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "添加菜单模型")
@Data
public class AddMenuDTO {
    @Schema(description = "菜单名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "菜单名不能为空")
    private String menuName;

    @Schema(description = "父菜单ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long parentId;

    @Schema(description = "显示顺序", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    @Schema(description = "路由地址", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 200, message = "路由地址长度不能超过200个字符")
    private String path;

    @Schema(description = "组件路径", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 255, message = "组件路径长度不能超过255个字符")
    private String component;

    @Schema(description = "菜单类型", example = "M", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "菜单类型不能为空")
    @Pattern(regexp = "M|C|F", message = "菜单类型必须是M(目录)、C(菜单)或F(按钮)")
    private String menuType;

    @Schema(description = "菜单状态", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "菜单状态不能为空")
    private Integer visible;

    @Schema(description = "权限标识", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "权限标识不能为空")
    private String perms;

    @Schema(description = "菜单图标", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "菜单图标不能为空")
    private String icon;

}
