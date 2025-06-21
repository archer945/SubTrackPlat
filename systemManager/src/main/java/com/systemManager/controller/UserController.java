package com.systemManager.controller;


import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.AddUserDTO;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@RestController
@RequestMapping("/systemManager/user")
@Tag(name = "用户管理")
public class UserController {
    @Resource
    private IUserService userService;

    @Operation(summary = "获取用户列表（条件+分页）")
    @GetMapping
    public JsonVO<PageDTO<UserVO>> queryUserList(@Validated UserQuery userQuery) {
        return JsonVO.success(userService.listUser(userQuery));
    }

    @Operation(summary = "添加用户")
    @PostMapping
    public JsonVO<String> addUser(@Validated @RequestBody AddUserDTO addUserDTO) {
        return JsonVO.success(userService.saveUser(addUserDTO));
    }
}

