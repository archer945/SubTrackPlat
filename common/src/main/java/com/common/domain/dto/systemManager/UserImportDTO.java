package com.common.domain.dto.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * <p>
 * 用户导入DTO
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@Data
@Schema(description = "用户导入DTO")
public class UserImportDTO {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3到20个字符之间")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6到20个字符之间")
    @Schema(description = "密码")
    private String password;

    @NotBlank(message = "用户昵称不能为空")
    @Schema(description = "用户昵称")
    private String realName;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号码")
    private String tel;

    @Schema(description = "部门ID")
    private Long deptId;
    
    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "状态（1正常 0停用）")
    private Integer status = 1;

    @Schema(description = "备注")
    private String remark;
} 