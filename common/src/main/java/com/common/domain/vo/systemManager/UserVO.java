package com.common.domain.vo.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "用户查询响应模型")
@Data
public class UserVO {
    @Schema(description = "用户ID", example = "1")
    private String userId;

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "手机号", example = "13888888888")
    private String phone;

    @Schema(description = "状态", example = "正常")
    private String status;

    @Schema(description = "部门", example = "技术部")
    private String deptName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间", example = "2025-06-17 00:00:00")
    private LocalDateTime startTime;
}
