package com.systemManager.controller;


import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.AddUserDTO;
import com.common.domain.dto.systemManager.BatchUserDTO;
import com.common.domain.dto.systemManager.UpdateUserDTO;
import com.common.domain.dto.systemManager.ResetPasswordDTO;
import com.common.domain.dto.systemManager.UserRoleDTO;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    
    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/password/reset")
    public JsonVO<String> resetPassword(@PathVariable Long id, @Validated @RequestBody ResetPasswordDTO dto) {
        return JsonVO.success(userService.resetPassword(id, dto));
    }
    
    @Operation(summary = "为用户分配角色")
    @PutMapping("/roles")
    public JsonVO<Boolean> assignRoles(@Validated @RequestBody UserRoleDTO dto) {
        boolean result = userService.assignRoles(dto.getUserId(), dto.getRoleIds());
        return JsonVO.success(result);
    }
    
    @Operation(summary = "获取用户的角色ID列表")
    @GetMapping("/{userId}/roles")
    public JsonVO<List<Long>> getUserRoles(@PathVariable Long userId) {
        List<Long> roleIds = userService.getRoleIdsByUserId(userId);
        return JsonVO.success(roleIds);
    }
    
    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    public JsonVO<String> batchRemoveUsers(@Validated @RequestBody BatchUserDTO dto) {
        return JsonVO.success(userService.batchRemoveUsers(dto));
    }
    
    @Operation(summary = "导入用户")
    @PostMapping("/import")
    public JsonVO<Map<String, Object>> importUsers(@RequestParam("file") MultipartFile file) throws IOException {
        return JsonVO.success(userService.importUsers(file));
    }
    
    @Operation(summary = "导出用户")
    @GetMapping("/export")
    public void exportUsers(@ParameterObject UserQuery userQuery, HttpServletResponse response) throws IOException {
        // 添加日志，记录导出请求
        System.out.println("接收到导出用户请求: " + userQuery);
        userService.exportUsers(userQuery, response);
        // 添加日志，记录导出完成
        System.out.println("用户导出完成");
    }
}

