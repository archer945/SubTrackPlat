package com.common.domain.dto.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(description = "更新用户模型")
@Data
public class UpdateUserDTO{

    @Schema(description = "真实姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @Schema(description = "邮箱", example = "zhangsan@qq.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "请输入有效的邮箱地址")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @Schema(description = "手机号", example = "13888888888", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的中国大陆手机号")
    private String tel;

    @Schema(description = "部门id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "部门id不能为空")
    private Long deptId;

    @Schema(description = "状态 0:禁用 1:正常 2:锁定", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "状态不能为空")
    private String status;
}
