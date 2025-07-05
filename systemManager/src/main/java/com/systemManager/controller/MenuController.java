package com.systemManager.controller;


import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.MenuDTO;
import com.common.domain.query.systemManager.MenuQuery;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.MenuTreeVO;
import com.common.domain.vo.systemManager.UserMenuVO;
import com.systemManager.security.annotation.RequiresPermission;
import com.systemManager.service.IMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单权限表 前端控制器
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@RestController
@RequestMapping("/systemManager/menu")
@Tag(name = "菜单管理")
@Validated
public class MenuController {
    @Resource
    private IMenuService menuService;

    @Operation(summary = "获取菜单列表（条件+分页）")
    //@RequiresPermission("system:menu:list")
    @GetMapping
    public JsonVO<PageDTO<MenuTreeVO>> queryMenuList(@ParameterObject @Validated MenuQuery menuQuery) {
        return JsonVO.success(menuService.listMenu(menuQuery));
    }

    @Operation(summary = "添加菜单")
    //@RequiresPermission("system:menu:add")
    @PostMapping
    public JsonVO<String> addMenu(@Validated @RequestBody MenuDTO dto) {
        return JsonVO.success(menuService.saveMenu(dto));
    }

    @Operation(summary = "更新菜单")
    //@RequiresPermission("system:menu:edit")
    @PutMapping("/{id}")
    public JsonVO<String> updateMenu(@PathVariable Long id, @Validated @RequestBody MenuDTO dto) {
        return JsonVO.success(menuService.updateMenu(id, dto));
    }

    @Operation(summary = "删除菜单")
    //@RequiresPermission("system:menu:remove")
    @DeleteMapping("/{id}")
    public JsonVO<String> removeMenu(@PathVariable Long id) {
        return JsonVO.success(menuService.removeMenu(id));
    }
    
//    @Operation(summary = "获取当前用户菜单")
//    @GetMapping("/user/menus")
//    public JsonVO<List<UserMenuVO>> getUserMenus() {
//        return JsonVO.success(menuService.getCurrentUserMenus());
//    }
    
    @Operation(summary = "根据用户ID获取菜单")
    //@RequiresPermission("system:menu:query")
    @GetMapping("/user/{userId}")
    public JsonVO<List<UserMenuVO>> getMenusByUserId(@PathVariable Long userId) {
        return JsonVO.success(menuService.getUserMenus(userId));
    }
}

