package com.common.domain.query.systemManager;

import com.common.domain.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "菜单查询模型")
@Data
public class MenuQuery extends PageQuery {
    @Schema(description = "菜单名称", example = "系统管理")
    private String menuName;

    @Schema(description = "状态", example = "显示")
    private String visible;
}
