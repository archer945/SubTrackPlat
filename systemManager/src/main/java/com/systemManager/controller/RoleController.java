package com.systemManager.controller;


import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.RoleDTO;
import com.common.domain.query.systemManager.RoleQuery;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.RoleVO;
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
    public JsonVO<PageDTO<RoleVO>> list(@ParameterObject @Validated RoleQuery query) {
        return JsonVO.success(roleService.listRoles(query));
    }

    @PostMapping
    @Operation(summary = "添加角色")
    public JsonVO<String> addRole(@Valid @RequestBody RoleDTO dto) {
        return JsonVO.success(roleService.saveRole(dto));
    }

//    @GetMapping("/{id}")
//    public JsonVO<RoleVO> detail(@PathVariable Long id) {
//        return JsonVO.success(roleService.getRoleById(id));
//    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    public JsonVO<String> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO dto) {
        return JsonVO.success( roleService.updateRole(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public JsonVO<String> removeRole(@PathVariable Long id) {
        return JsonVO.success(roleService.removeRole(id));
    }

//    @GetMapping("/menuIds/{roleId}")
//    public JsonVO<List<Long>> getMenuIds(@PathVariable Long roleId) {
//        return JsonVO.success(roleService.getMenuIdsByRoleId(roleId));
//    }
//
//    @PutMapping("/dataScope")
//    public JsonVO<Void> authDataScope(@RequestBody RoleDTO dto) {
//        roleService.authDataScope(dto);
//        return JsonVO.success();
//    }
//
//    @PutMapping("/assignUsers")
//    public JsonVO<Void> assignUsers(@RequestBody RoleUserDTO dto) {
//        roleService.assignUsers(dto.getRoleId(), dto.getUserIds());
//        return JsonVO.success();
//    }
}

