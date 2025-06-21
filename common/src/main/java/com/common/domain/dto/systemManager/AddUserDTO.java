package com.common.domain.dto.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "添加用户模型")
@Data
public class AddUserDTO {
    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "真实姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String realName;

    @Schema(description = "邮箱", example = "zhangsan@qq.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "手机号", example = "13888888888", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @Schema(description = "部门id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long deptId;

    @Schema(description = "备注", example = "1")
    private Long remark;
}
