package com.systemManager.controller;

import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.MenuDTO;
import com.common.domain.query.systemManager.MenuQuery;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.MenuTreeVO;
import com.common.domain.vo.systemManager.UserMenuVO;
import com.systemManager.service.IMenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MenuControllerTest {

    @Mock
    private IMenuService menuService;

    @InjectMocks
    private MenuController menuController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testQueryMenuList() {
        // 准备测试数据
        MenuQuery menuQuery = new MenuQuery();
        
        // 创建一个模拟的PageDTO对象
        PageDTO<MenuTreeVO> pageDTO = mock(PageDTO.class);
        
        // 模拟服务方法
        when(menuService.listMenu(any(MenuQuery.class))).thenReturn(pageDTO);
        
        // 执行测试
        JsonVO<PageDTO<MenuTreeVO>> result = menuController.queryMenuList(menuQuery);
        
        // 验证结果
        assertEquals(pageDTO, result.getData());
        assertEquals(10000, result.getCode());
        verify(menuService).listMenu(menuQuery);
    }

    @Test
    void testAddMenu() {
        // 准备测试数据
        MenuDTO menuDTO = new MenuDTO();
        
        // 模拟服务方法
        when(menuService.saveMenu(any(MenuDTO.class))).thenReturn("添加成功");
        
        // 执行测试
        JsonVO<String> result = menuController.addMenu(menuDTO);
        
        // 验证结果
        assertEquals("添加成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(menuService).saveMenu(menuDTO);
    }

    @Test
    void testUpdateMenu() {
        // 准备测试数据
        Long menuId = 1L;
        MenuDTO menuDTO = new MenuDTO();
        
        // 模拟服务方法
        when(menuService.updateMenu(eq(menuId), any(MenuDTO.class))).thenReturn("更新成功");
        
        // 执行测试
        JsonVO<String> result = menuController.updateMenu(menuId, menuDTO);
        
        // 验证结果
        assertEquals("更新成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(menuService).updateMenu(menuId, menuDTO);
    }

    @Test
    void testRemoveMenu() {
        // 准备测试数据
        Long menuId = 1L;
        
        // 模拟服务方法
        when(menuService.removeMenu(menuId)).thenReturn("删除成功");
        
        // 执行测试
        JsonVO<String> result = menuController.removeMenu(menuId);
        
        // 验证结果
        assertEquals("删除成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(menuService).removeMenu(menuId);
    }

    @Test
    void testGetUserMenus() {
        // 准备测试数据
        List<UserMenuVO> menus = new ArrayList<>();
        UserMenuVO menu = mock(UserMenuVO.class);
        menus.add(menu);
        
        // 模拟服务方法
        when(menuService.getCurrentUserMenus()).thenReturn(menus);
        
        // 执行测试
        JsonVO<List<UserMenuVO>> result = menuController.getUserMenus();
        
        // 验证结果
        assertEquals(menus, result.getData());
        assertEquals(10000, result.getCode());
        verify(menuService).getCurrentUserMenus();
    }

    @Test
    void testGetMenusByUserId() {
        // 准备测试数据
        Long userId = 1L;
        List<UserMenuVO> menus = new ArrayList<>();
        UserMenuVO menu = mock(UserMenuVO.class);
        menus.add(menu);
        
        // 模拟服务方法
        when(menuService.getUserMenus(userId)).thenReturn(menus);
        
        // 执行测试
        JsonVO<List<UserMenuVO>> result = menuController.getMenusByUserId(userId);
        
        // 验证结果
        assertEquals(menus, result.getData());
        assertEquals(10000, result.getCode());
        verify(menuService).getUserMenus(userId);
    }
} 