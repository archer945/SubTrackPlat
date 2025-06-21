package com.common.domain.query.systemManager;

import com.common.domain.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "部门查询模型")
@Data
public class DeptQuery extends PageQuery {
    @Schema(description = "部门名称", example = "技术部")
    private String menuName;

    @Schema(description = "状态", example = "正常")
    private String status;
}
