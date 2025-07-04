package com.common.domain.dto.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 重置密码DTO
 */
@Data
@Schema(description = "重置密码数据传输对象")
public class ResetPasswordDTO {

    @NotBlank(message = "密码不能为空")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
        message = "密码必须包含大小写字母、数字和特殊字符，且长度不少于8位"
    )
    @Schema(description = "新密码")
    private String password;
} 