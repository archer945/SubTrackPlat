package com.common.domain.query.systemManager;

import com.common.domain.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "用户查询模型")
@Data
public class UserQuery extends PageQuery {
    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "手机号码", example = "15888888888")
    private String tel;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间", example = "2025-06-17 00:00:00")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间", example = "2025-07-01 23:59:59")
    private LocalDateTime endTime;
}
