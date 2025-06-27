package com.common.domain.vo.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "部门查询响应模型")
@Data
public class DeptTreeVO {
    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    @Schema(description = "部门名称", example = "管理部")
    private String deptName;

    @Schema(description = "父部门ID", example = "1")
    private Long parentId;

    @Schema(description = "显示顺序", example = "1")
    private Integer orderNum;

    @Schema(description = "创建时间", example = "2025-06-17 00:00:00")
    private LocalDateTime createTime;

    @Schema(description = "子部门")
    private List<DeptTreeVO> children;
}