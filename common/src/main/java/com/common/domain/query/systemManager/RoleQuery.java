package com.common.domain.query.systemManager;

import com.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@ApiModel("角色查询模型")
@Data
public class RoleQuery extends PageQuery {
    @ApiModelProperty(value = "角色名称", example = "超级管理员")
    private String roleName;

    @ApiModelProperty(value = "权限字符", example = "admin")
    private String roleCode;

    @ApiModelProperty(value = "状态", example = "正常")
    private String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间", example = "2025-06-17 00:00:00")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间", example = "2025-07-01 23:59:59")
    private LocalDateTime endTime;
}
