package com.common.domain.query.systemManager;

import com.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@ApiModel("用户查询模型")
@Data
public class UserQuery extends PageQuery {
    @ApiModelProperty(value = "用户名", example = "admin")
    private String username;

    @ApiModelProperty(value = "状态", example = "正常")
    private String status;

    @ApiModelProperty(value = "手机号码", example = "15888888888")
    private String tel;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间", example = "2025-06-17 00:00:00")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间", example = "2025-07-01 23:59:59")
    private LocalDateTime endTime;
}
