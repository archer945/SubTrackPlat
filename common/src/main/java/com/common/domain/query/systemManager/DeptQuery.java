package com.common.domain.query.systemManager;

import com.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("部门查询模型")
@Data
public class DeptQuery extends PageQuery {
    @ApiModelProperty(value = "部门名称", example = "技术部")
    private String menuName;

    @ApiModelProperty(value = "状态", example = "正常")
    private String status;
}
