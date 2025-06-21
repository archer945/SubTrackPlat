package com.common.domain.query.systemManager;

import com.common.domain.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "角色查询模型")
@Data
public class RoleQuery extends PageQuery {
    @Schema(description = "角色名称", example = "超级管理员")
    private String roleName;

    @Schema(description = "权限字符", example = "admin")
    private String roleCode;

    @Schema(description = "状态", example = "正常")
    private String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间", example = "2025-06-17 00:00:00")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间", example = "2025-07-01 23:59:59")
    private LocalDateTime endTime;
}
