package com.systemManager.controller;


import com.baomidou.mybatisplus.extension.service.IService;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.AddMenuDTO;
import com.common.domain.dto.systemManager.AddUserDTO;
import com.common.domain.dto.systemManager.UpdateMenuDTO;
import com.common.domain.dto.systemManager.UpdateUserDTO;
import com.common.domain.query.systemManager.MenuQuery;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.MenuTreeVO;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.service.IMenuService;
import com.systemManager.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping
    public JsonVO<PageDTO<MenuTreeVO>> queryMenuList(@ParameterObject @Validated MenuQuery menuQuery) {
        return JsonVO.success(menuService.listMenu(menuQuery));
    }

    @Operation(summary = "添加菜单")
    @PostMapping
    public JsonVO<String> addMenu(@Validated @RequestBody AddMenuDTO addMenuDTO) {
        return JsonVO.success(menuService.saveMenu(addMenuDTO));
    }

    @Operation(summary = "更新菜单")
    @PutMapping("/{id}")
    public JsonVO<String> updateMenu(@PathVariable Long id, @Validated @RequestBody UpdateMenuDTO dto) {
        return JsonVO.success(menuService.updateMenu(id, dto));
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public JsonVO<String> deleteUser(@PathVariable Long id) {
        return JsonVO.success(menuService.removeMenu(id));
    }
}

