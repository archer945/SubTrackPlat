package com.common.domain.vo.systemManager;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@ApiModel("用户查询响应模型")
@Data
public class UserVO {
    @ApiModelProperty(value = "用户ID", example = "1")
    private String userId;

    @ApiModelProperty(value = "用户名", example = "zhangsan")
    private String username;

    @ApiModelProperty(value = "真实姓名", example = "张三")
    private String realName;

    @ApiModelProperty(value = "手机号", example = "13888888888")
    private String phone;

    @ApiModelProperty(value = "状态", example = "正常")
    private String status;

    @ApiModelProperty(value = "部门", example = "技术部")
    private String deptName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间", example = "2025-06-17 00:00:00")
    private LocalDateTime startTime;
}
