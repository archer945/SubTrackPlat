package com.common.domain.dto.systemManager;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户数据传输模型")
@Data
public class UserDTO extends AddUserDTO{
    @ApiModelProperty(value = "用户ID", example = "1", required = true)
    private String userId;
}
