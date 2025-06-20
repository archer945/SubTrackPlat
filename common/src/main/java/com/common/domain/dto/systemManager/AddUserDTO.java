package com.common.domain.dto.systemManager;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("添加用户模型")
@Data
public class AddUserDTO {
    @ApiModelProperty(value = "用户名", example = "zhangsan", required = true)
    private String username;

    @ApiModelProperty(value = "密码", example = "123456", required = true)
    private String password;

    @ApiModelProperty(value = "真实姓名", example = "张三", required = true)
    private String realName;

    @ApiModelProperty(value = "邮箱", example = "zhangsan@qq.com", required = true)
    private String email;

    @ApiModelProperty(value = "手机号", example = "13888888888", required = true)
    private String phone;

    @ApiModelProperty(value = "部门id", example = "1", required = true)
    private Long deptId;

    @ApiModelProperty(value = "备注", example = "1")
    private Long remark;
}
