package com.common.domain.dto.systemManager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(description = "部门数据传输模型")
@Data
public class DeptDTO {
    @Schema(description = "部门名称", example = "管理部", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    @Schema(description = "部门编码", example = "managerDept", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "部门编码不能为空")
    private String deptCode;

    @Schema(description = "父部门ID", example = "1")
    private Long parentId;

    @Schema(description = "显示顺序", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    @Schema(description = "状态", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "负责人", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "负责人不能为空")
    private String leader;

    @Schema(description = "联系电话", example = "13888888888", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的中国大陆手机号")
    private String tel;

    @Schema(description = "邮箱", example = "zhangsan@qq.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请输入有效的邮箱地址")
    private String email;


}
