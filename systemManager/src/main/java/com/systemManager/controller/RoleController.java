package com.systemManager.controller;


import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.DataScopeDTO;
import com.common.domain.dto.systemManager.RoleCopyDTO;
import com.common.domain.dto.systemManager.RoleDTO;
import com.common.domain.dto.systemManager.RoleMenuDTO;
import com.common.domain.query.systemManager.RoleQuery;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.RoleVO;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.security.annotation.RequiresPermission;
import com.systemManager.service.IRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 角色信息表 前端控制器
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@RestController
@RequestMapping("/systemManager/role")
@Tag(name = "角色管理")
@Validated
public class RoleController {
    @Resource
    private IRoleService roleService;

    @GetMapping
    @Operation(summary = "获取角色列表（条件+分页）")
    @RequiresPermission("system:role:list")
    public JsonVO<PageDTO<RoleVO>> list(@ParameterObject @Validated RoleQuery query) {
        return JsonVO.success(roleService.listRoles(query));
    }

    @PostMapping
    @Operation(summary = "添加角色")
    @RequiresPermission("system:role:add")
    public JsonVO<String> addRole(@Valid @RequestBody RoleDTO dto) {
        return JsonVO.success(roleService.saveRole(dto));
    }

//    @GetMapping("/{id}")
//    public JsonVO<RoleVO> detail(@PathVariable Long id) {
//        return JsonVO.success(roleService.getRoleById(id));
//    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    @RequiresPermission("system:role:edit")
    public JsonVO<String> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO dto) {
        return JsonVO.success( roleService.updateRole(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    @RequiresPermission("system:role:remove")
    public JsonVO<String> removeRole(@PathVariable Long id) {
        return JsonVO.success(roleService.removeRole(id));
    }

    @GetMapping("/{roleId}/menus")
    @Operation(summary = "获取角色菜单权限")
    public JsonVO<List<Long>> getRoleMenus(@PathVariable Long roleId) {
        return JsonVO.success(roleService.getRoleMenuIds(roleId));
    }

    @PutMapping("/{roleId}/menus")
    @Operation(summary = "分配角色菜单权限")
    public JsonVO<Boolean> assignRoleMenus(@PathVariable Long roleId, @Valid @RequestBody RoleMenuDTO dto) {
        return JsonVO.success(roleService.assignRoleMenus(roleId, dto));
    }

    @PutMapping("/{roleId}/dataScope")
    @Operation(summary = "更新角色数据权限")
    public JsonVO<Boolean> updateDataScope(@PathVariable Long roleId, @Valid @RequestBody DataScopeDTO dto) {
        return JsonVO.success(roleService.updateDataScope(roleId, dto));
    }
    
    @GetMapping("/{roleId}/users")
    @Operation(summary = "获取角色用户列表")
    public JsonVO<PageDTO<UserVO>> getRoleUsers(@PathVariable Long roleId, @ParameterObject @Validated UserQuery query) {
        return JsonVO.success(roleService.getRoleUsers(roleId, query));
    }
    
    @PostMapping("/copy/{sourceRoleId}")
    @Operation(summary = "复制角色")
    public JsonVO<String> copyRole(@PathVariable Long sourceRoleId, @Valid @RequestBody RoleCopyDTO dto) {
        return JsonVO.success(roleService.copyRole(sourceRoleId, dto));
    }
}

