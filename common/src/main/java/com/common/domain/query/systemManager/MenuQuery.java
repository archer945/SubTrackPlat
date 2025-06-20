package com.common.domain.query.systemManager;

import com.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("菜单查询模型")
@Data
public class MenuQuery extends PageQuery {
    @ApiModelProperty(value = "菜单名称", example = "系统管理")
    private String menuName;

    @ApiModelProperty(value = "状态", example = "显示")
    private String visible;
}
