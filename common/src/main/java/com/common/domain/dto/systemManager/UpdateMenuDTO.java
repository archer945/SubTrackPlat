package com.common.domain.dto.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "更新菜单模型")
@Data
public class UpdateMenuDTO extends AddMenuDTO{
    @Schema(description = "菜单id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "菜单id不能为空")
    private Long menuId;
}
