package com.systemManager.controller;


import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.AddUserDTO;
import com.common.domain.dto.systemManager.UpdateUserDTO;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springdoc.core.annotations.ParameterObject;
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
@Validated
public class UserController {
    @Resource
    private IUserService userService;

    @Operation(summary = "获取用户列表（条件+分页）")
    @GetMapping
    public JsonVO<PageDTO<UserVO>> queryUserList(@ParameterObject @Validated UserQuery userQuery) {
        return JsonVO.success(userService.listUser(userQuery));
    }

    @Operation(summary = "添加用户")
    @PostMapping
    public JsonVO<String> addUser(@Validated @RequestBody AddUserDTO addUserDTO) {
        return JsonVO.success(userService.saveUser(addUserDTO));
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public JsonVO<String> updateUser(@PathVariable Long id, @Validated @RequestBody UpdateUserDTO dto) {
        return JsonVO.success(userService.updateUser(id, dto));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public JsonVO<String> removeUser(@PathVariable Long id) {
        return JsonVO.success(userService.removeUser(id));
    }
}

