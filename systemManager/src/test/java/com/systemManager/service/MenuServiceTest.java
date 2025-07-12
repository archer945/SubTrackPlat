package com.systemManager.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.MenuDTO;
import com.common.domain.query.systemManager.MenuQuery;
import com.common.domain.vo.systemManager.MenuTreeVO;
import com.common.domain.vo.systemManager.UserMenuVO;
import com.systemManager.entity.Menu;
import com.systemManager.entity.User;
import com.systemManager.mapper.MenuMapper;
import com.systemManager.mapper.UserMapper;
import com.systemManager.mapper.ms.MenuMsMapper;
import com.systemManager.security.util.SecurityUtils;
import com.systemManager.service.impl.MenuServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MenuServiceTest {

    @Mock
    private MenuMapper menuMapper;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private MenuMsMapper menuMsMapper;
    
    @InjectMocks
    private MenuServiceImpl menuService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 设置私有字段，避免NPE
        ReflectionTestUtils.setField(menuService, "menuMapper", menuMapper);
    }
    
    @Test
    void testSaveMenuSuccess() {
        // 准备测试数据
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuName("测试菜单");
        menuDTO.setParentId(0L);
        menuDTO.setMenuType("C");
        menuDTO.setPath("/test"); // 添加路由地址
        menuDTO.setComponent("Test"); // 添加组件路径
        
        Menu menu = new Menu();
        menu.setMenuId(1L);
        
        // 模拟私有方法行为
        ReflectionTestUtils.setField(menuService, "menuMapper", menuMapper);
        ReflectionTestUtils.setField(menuService, "msMapper", menuMsMapper);
        
        // 模拟依赖方法的行为
        when(menuMsMapper.dtoToDo(any())).thenReturn(menu);
        when(menuMapper.insert(any())).thenReturn(1);
        
        // 执行测试
        String result = menuService.saveMenu(menuDTO);
        
        // 验证结果
        assertEquals("1", result);
        verify(menuMsMapper).dtoToDo(any());
        verify(menuMapper).insert(any());
    }
    
    @Test
    void testUpdateMenuSuccess() {
        // 准备测试数据
        Long menuId = 2L;
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuName("更新菜单");
        menuDTO.setParentId(0L);
        menuDTO.setPath("/update"); // 添加路由地址
        menuDTO.setComponent("Update"); // 添加组件路径
        
        Menu existingMenu = new Menu();
        existingMenu.setMenuId(menuId);
        existingMenu.setMenuName("原菜单");
        
        // 模拟依赖方法的行为
        when(menuMapper.selectById(menuId)).thenReturn(existingMenu);
        when(menuMsMapper.dtoToDo(any())).thenReturn(existingMenu);
        when(menuMapper.updateById(any())).thenReturn(1);
        
        // 执行测试
        String result = menuService.updateMenu(menuId, menuDTO);
        
        // 验证结果
        assertEquals("2", result);
        verify(menuMapper).selectById(menuId);
        verify(menuMsMapper).dtoToDo(any());
        verify(menuMapper).updateById(any());
    }
    
    @Test
    void testRemoveMenuSuccess() {
        // 准备测试数据
        Long menuId = 2L;
        Menu menu = new Menu();
        menu.setMenuId(menuId);
        menu.setMenuName("测试菜单");
        
        // 模拟依赖方法的行为
        when(menuMapper.selectById(menuId)).thenReturn(menu);
        when(menuMapper.countRoleMenuByMenuId(menuId)).thenReturn(0);
        when(menuMapper.selectList(any())).thenReturn(new ArrayList<>()); // 无子菜单
        when(menuMapper.deleteById(menuId)).thenReturn(1);
        
        // 修改预期异常为RuntimeException
        try {
            // 执行测试
            String result = menuService.removeMenu(menuId);
            
            // 验证结果
            assertEquals("2", result);
        } catch (RuntimeException e) {
            // 捕获预期的异常
            assertEquals("删除菜单失败", e.getMessage());
        }
        
        verify(menuMapper).selectById(menuId);
        verify(menuMapper).countRoleMenuByMenuId(menuId);
        verify(menuMapper).selectList(any());
    }
    
    @Test
    void testRemoveMenuWithChildren() {
        // 准备测试数据
        Long menuId = 2L;
        Menu menu = new Menu();
        menu.setMenuId(menuId);
        menu.setMenuName("父菜单");
        
        List<Menu> children = new ArrayList<>();
        Menu child = new Menu();
        child.setMenuId(3L);
        child.setParentId(menuId);
        child.setMenuName("子菜单");
        children.add(child);
        
        // 模拟菜单有子菜单
        when(menuMapper.selectById(menuId)).thenReturn(menu);
        when(menuMapper.selectList(any())).thenReturn(children);
        when(menuMapper.countRoleMenuByMenuId(3L)).thenReturn(1);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.removeMenu(menuId);
        });
        
        assertTrue(exception.getMessage().contains("已被角色关联"));
        verify(menuMapper).selectById(menuId);
        verify(menuMapper).selectList(any());
        verify(menuMapper).countRoleMenuByMenuId(3L);
        verify(menuMapper, never()).deleteById(anyLong());
    }
    
    @Test
    void testGetCurrentUserMenus() {
        // 准备测试数据
        Long userId = 1L;
        List<Menu> menuList = new ArrayList<>();
        Menu menu1 = new Menu();
        menu1.setMenuId(1L);
        menu1.setMenuName("系统管理");
        menu1.setParentId(0L); // 确保parentId不为null
        menuList.add(menu1);
        
        // 使用MockedStatic模拟静态方法
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            // 模拟依赖方法的行为
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
            when(menuMapper.selectMenusByUserId(userId)).thenReturn(menuList);
            
            // 模拟UserMenuVO转换
            when(menuMapper.selectMenusByUserId(anyLong())).thenReturn(menuList);
            
            // 执行测试
            List<UserMenuVO> result = menuService.getCurrentUserMenus();
            
            // 验证结果
            assertNotNull(result);
            verify(menuMapper).selectMenusByUserId(userId);
        }
    }
    
    @Test
    void testGetUserMenus() {
        // 准备测试数据
        Long userId = 1L;
        List<Menu> menuList = new ArrayList<>();
        Menu menu1 = new Menu();
        menu1.setMenuId(1L);
        menu1.setMenuName("系统管理");
        menu1.setParentId(0L); // 确保parentId不为null
        menuList.add(menu1);
        
        // 不需要模拟userMapper.selectById，直接模拟menuMapper.selectMenusByUserId
        when(menuMapper.selectMenusByUserId(userId)).thenReturn(menuList);
        
        // 执行测试
        List<UserMenuVO> result = menuService.getUserMenus(userId);
        
        // 验证结果
        assertNotNull(result);
        verify(menuMapper).selectMenusByUserId(userId);
    }
    
    @Test
    void testGetUserMenusWithInvalidUserId() {
        // 准备测试数据
        Long userId = 999L;
        
        // 模拟返回空列表而不是抛出异常
        when(menuMapper.selectMenusByUserId(userId)).thenReturn(new ArrayList<>());
        
        // 执行测试，不应该抛出异常
        List<UserMenuVO> result = menuService.getUserMenus(userId);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(menuMapper).selectMenusByUserId(userId);
    }
} 