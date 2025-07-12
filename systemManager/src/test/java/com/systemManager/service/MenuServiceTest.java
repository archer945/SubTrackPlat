package com.systemManager.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.systemManager.security.exception.NoPermissionException;
import com.systemManager.security.util.SecurityUtils;
import com.systemManager.service.impl.MenuServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    
    @Captor
    private ArgumentCaptor<LambdaQueryWrapper<Menu>> queryWrapperCaptor;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 设置私有字段，避免NPE
        ReflectionTestUtils.setField(menuService, "menuMapper", menuMapper);
    }
    
    @Test
    void testListMenu() {
        // 准备测试数据
        MenuQuery menuQuery = new MenuQuery();
        menuQuery.setMenuName("测试");
        menuQuery.setVisible(1);
        menuQuery.setPageIndex(1);
        menuQuery.setPageSize(10);
        
        List<Menu> menus = new ArrayList<>();
        Menu menu1 = new Menu();
        menu1.setMenuId(1L);
        menu1.setMenuName("测试菜单1");
        menu1.setParentId(0L);
        menu1.setOrderNum(1);
        menus.add(menu1);
        
        Menu menu2 = new Menu();
        menu2.setMenuId(2L);
        menu2.setMenuName("测试菜单2");
        menu2.setParentId(0L);
        menu2.setOrderNum(2);
        menus.add(menu2);
        
        Page<Menu> page = new Page<>(1, 10);
        page.setRecords(menus);
        page.setTotal(2);
        
        // 模拟依赖方法的行为
        when(menuMapper.selectPage(any(), any())).thenReturn(page);
        
        // 执行测试
        PageDTO<MenuTreeVO> result = menuService.listMenu(menuQuery);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(2, result.getRows().size());
        verify(menuMapper).selectPage(any(), any());
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
        when(menuMapper.selectCount(any())).thenReturn(0L); // 菜单名称和路径都不存在
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
    void testSaveMenuWithDuplicateName() {
        // 准备测试数据
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuName("已存在菜单");
        menuDTO.setParentId(0L);
        menuDTO.setMenuType("C");
        menuDTO.setPath("/test");
        menuDTO.setComponent("Test");
        
        // 模拟菜单名称已存在
        when(menuMapper.selectCount(any())).thenReturn(1L);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.saveMenu(menuDTO);
        });
        
        assertEquals("同级菜单名称已存在", exception.getMessage());
        verify(menuMapper).selectCount(any());
        verify(menuMsMapper, never()).dtoToDo(any());
        verify(menuMapper, never()).insert(any());
    }
    
    @Test
    void testSaveMenuWithDuplicatePath() {
        // 准备测试数据
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuName("新菜单");
        menuDTO.setParentId(0L);
        menuDTO.setMenuType("C");
        menuDTO.setPath("/existing");
        menuDTO.setComponent("Test");
        
        // 模拟菜单名称不存在但路径存在
        when(menuMapper.selectCount(queryWrapperCaptor.capture()))
            .thenReturn(0L) // 第一次调用返回0（菜单名称不存在）
            .thenReturn(1L); // 第二次调用返回1（路径已存在）
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.saveMenu(menuDTO);
        });
        
        assertEquals("路由地址已存在", exception.getMessage());
        verify(menuMapper, times(2)).selectCount(any());
        verify(menuMsMapper, never()).dtoToDo(any());
        verify(menuMapper, never()).insert(any());
    }
    
    @Test
    void testSaveMenuWithInvalidButtonType() {
        // 准备测试数据 - 按钮类型但没有父菜单
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuName("按钮");
        menuDTO.setParentId(0L); // 无父菜单
        menuDTO.setMenuType("F"); // 按钮类型
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.saveMenu(menuDTO);
        });
        
        assertEquals("按钮必须绑定到菜单或目录", exception.getMessage());
        verify(menuMapper, never()).selectCount(any());
        verify(menuMsMapper, never()).dtoToDo(any());
        verify(menuMapper, never()).insert(any());
    }
    
    @Test
    void testSaveMenuWithButtonNoPerms() {
        // 准备测试数据 - 按钮类型但没有权限标识
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuName("按钮");
        menuDTO.setParentId(1L); // 有父菜单
        menuDTO.setMenuType("F"); // 按钮类型
        menuDTO.setPerms(""); // 无权限标识
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.saveMenu(menuDTO);
        });
        
        assertEquals("按钮权限标识不能为空", exception.getMessage());
        verify(menuMapper, never()).selectCount(any());
        verify(menuMsMapper, never()).dtoToDo(any());
        verify(menuMapper, never()).insert(any());
    }
    
    @Test
    void testSaveMenuWithButtonAndPath() {
        // 准备测试数据 - 按钮类型但设置了路由地址
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuName("按钮");
        menuDTO.setParentId(1L); // 有父菜单
        menuDTO.setMenuType("F"); // 按钮类型
        menuDTO.setPerms("test:button"); // 有权限标识
        menuDTO.setPath("/button"); // 不应该有路由地址
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.saveMenu(menuDTO);
        });
        
        assertEquals("按钮类型不能设置路由地址", exception.getMessage());
        verify(menuMapper, never()).selectCount(any());
        verify(menuMsMapper, never()).dtoToDo(any());
        verify(menuMapper, never()).insert(any());
    }
    
    @Test
    void testSaveMenuWithButtonAndComponent() {
        // 准备测试数据 - 按钮类型但设置了组件路径
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuName("按钮");
        menuDTO.setParentId(1L); // 有父菜单
        menuDTO.setMenuType("F"); // 按钮类型
        menuDTO.setPerms("test:button"); // 有权限标识
        menuDTO.setComponent("Button"); // 不应该有组件路径
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.saveMenu(menuDTO);
        });
        
        assertEquals("按钮类型不能设置组件路径", exception.getMessage());
        verify(menuMapper, never()).selectCount(any());
        verify(menuMsMapper, never()).dtoToDo(any());
        verify(menuMapper, never()).insert(any());
    }
    
    @Test
    void testSaveMenuWithNoPath() {
        // 准备测试数据 - 非按钮类型但没有路由地址
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuName("菜单");
        menuDTO.setParentId(0L);
        menuDTO.setMenuType("C"); // 菜单类型
        menuDTO.setPath(""); // 无路由地址
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.saveMenu(menuDTO);
        });
        
        assertEquals("路由地址不能为空", exception.getMessage());
        verify(menuMapper, never()).selectCount(any());
        verify(menuMsMapper, never()).dtoToDo(any());
        verify(menuMapper, never()).insert(any());
    }
    
    @Test
    void testSaveMenuWithNoComponent() {
        // 准备测试数据 - 菜单类型但没有组件路径
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuName("菜单");
        menuDTO.setParentId(0L);
        menuDTO.setMenuType("C"); // 菜单类型
        menuDTO.setPath("/menu"); // 有路由地址
        menuDTO.setComponent(""); // 无组件路径
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.saveMenu(menuDTO);
        });
        
        assertEquals("菜单类型必须设置组件路径", exception.getMessage());
        verify(menuMapper, never()).selectCount(any());
        verify(menuMsMapper, never()).dtoToDo(any());
        verify(menuMapper, never()).insert(any());
    }
    
    @Test
    void testUpdateMenuSuccess() {
        // 准备测试数据
        Long menuId = 2L;
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuName("更新菜单");
        menuDTO.setParentId(0L);
        menuDTO.setMenuType("C");
        menuDTO.setPath("/update"); // 添加路由地址
        menuDTO.setComponent("Update"); // 添加组件路径
        
        Menu existingMenu = new Menu();
        existingMenu.setMenuId(menuId);
        existingMenu.setMenuName("原菜单");
        existingMenu.setPath("/original");
        
        // 模拟依赖方法的行为
        when(menuMapper.selectById(menuId)).thenReturn(existingMenu);
        when(menuMapper.selectCount(any())).thenReturn(0L); // 菜单名称和路径都不存在
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
    void testUpdateMenuNotFound() {
        // 准备测试数据
        Long menuId = 999L;
        MenuDTO menuDTO = new MenuDTO();
        
        // 模拟菜单不存在
        when(menuMapper.selectById(menuId)).thenReturn(null);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.updateMenu(menuId, menuDTO);
        });
        
        assertEquals("菜单不存在", exception.getMessage());
        verify(menuMapper).selectById(menuId);
        verify(menuMsMapper, never()).dtoToDo(any());
        verify(menuMapper, never()).updateById(any());
    }
    
    @Test
    void testUpdateMenuWithDuplicateName() {
        // 准备测试数据
        Long menuId = 2L;
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuName("已存在菜单");
        menuDTO.setParentId(0L);
        menuDTO.setMenuType("C");
        menuDTO.setPath("/update");
        menuDTO.setComponent("Update");
        
        Menu existingMenu = new Menu();
        existingMenu.setMenuId(menuId);
        existingMenu.setMenuName("原菜单");
        existingMenu.setPath("/original");
        
        // 模拟菜单存在但名称已被使用
        when(menuMapper.selectById(menuId)).thenReturn(existingMenu);
        when(menuMapper.selectCount(any())).thenReturn(1L);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.updateMenu(menuId, menuDTO);
        });
        
        assertEquals("同级菜单名称已存在", exception.getMessage());
        verify(menuMapper).selectById(menuId);
        verify(menuMapper).selectCount(any());
        verify(menuMsMapper, never()).dtoToDo(any());
        verify(menuMapper, never()).updateById(any());
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
        when(menuMapper.deleteById(any())).thenReturn(1);
        
        // 执行测试
        String result = menuService.removeMenu(menuId);
        
        // 验证结果
        assertEquals("2", result);
        verify(menuMapper).selectById(menuId);
        verify(menuMapper).countRoleMenuByMenuId(menuId);
        verify(menuMapper).selectList(any());
        verify(menuMapper).deleteById(any());
    }
    
    @Test
    void testRemoveMenuWithInvalidId() {
        // 准备测试数据
        Long menuId = -1L;
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.removeMenu(menuId);
        });
        
        assertEquals("菜单ID不合法", exception.getMessage());
        verify(menuMapper, never()).selectById(anyLong());
        verify(menuMapper, never()).deleteById(anyLong());
    }
    
    @Test
    void testRemoveMenuNotFound() {
        // 准备测试数据
        Long menuId = 999L;
        
        // 模拟菜单不存在
        when(menuMapper.selectById(menuId)).thenReturn(null);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.removeMenu(menuId);
        });
        
        assertEquals("菜单不存在或已被删除", exception.getMessage());
        verify(menuMapper).selectById(menuId);
        verify(menuMapper, never()).deleteById(anyLong());
    }
    
    @Test
    void testRemoveMenuWithInvalidButtonType() {
        // 准备测试数据 - 按钮类型但没有父菜单（异常情况）
        Long menuId = 3L;
        Menu menu = new Menu();
        menu.setMenuId(menuId);
        menu.setMenuName("异常按钮");
        menu.setMenuType("F");
        menu.setParentId(0L); // 无父菜单，这是异常情况
        
        // 模拟菜单存在
        when(menuMapper.selectById(menuId)).thenReturn(menu);
        
        // 执行测试并验证异常
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            menuService.removeMenu(menuId);
        });
        
        assertEquals("数据异常：按钮类型菜单必须关联父菜单", exception.getMessage());
        verify(menuMapper).selectById(menuId);
        verify(menuMapper, never()).deleteById(anyLong());
    }
    
    @Test
    void testRemoveMenuWithRoleAssociation() {
        // 准备测试数据
        Long menuId = 4L;
        Menu menu = new Menu();
        menu.setMenuId(menuId);
        menu.setMenuName("已关联菜单");
        
        // 模拟菜单存在且已被角色关联
        when(menuMapper.selectById(menuId)).thenReturn(menu);
        when(menuMapper.countRoleMenuByMenuId(menuId)).thenReturn(1);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.removeMenu(menuId);
        });
        
        assertEquals("菜单已被角色关联，请先解除关联", exception.getMessage());
        verify(menuMapper).selectById(menuId);
        verify(menuMapper).countRoleMenuByMenuId(menuId);
        verify(menuMapper, never()).deleteById(anyLong());
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
        child.setCreateTime(LocalDateTime.now());
        children.add(child);
        
        // 模拟菜单有子菜单
        when(menuMapper.selectById(menuId)).thenReturn(menu);
        when(menuMapper.selectList(any())).thenReturn(children);
        when(menuMapper.countRoleMenuByMenuId(3L)).thenReturn(1);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.removeMenu(menuId);
        });
        
        assertTrue(exception.getMessage().contains("已被角色关联，无法级联删除"));
        verify(menuMapper).selectById(menuId);
        verify(menuMapper).selectList(any());
        verify(menuMapper).countRoleMenuByMenuId(3L);
        verify(menuMapper, never()).deleteById(anyLong());
    }
    
    @Test
    void testRemoveMenuWithCascadeDeleteSuccess() {
        // 准备测试数据
        Long menuId = 5L;
        Menu menu = new Menu();
        menu.setMenuId(menuId);
        menu.setMenuName("父菜单");
        
        List<Menu> children = new ArrayList<>();
        Menu child = new Menu();
        child.setMenuId(6L);
        child.setParentId(menuId);
        child.setMenuName("子菜单");
        child.setCreateTime(LocalDateTime.now());
        children.add(child);
        
        // 使用spy代替mock以允许部分方法执行
        MenuServiceImpl spyMenuService = spy(menuService);
        
        // 模拟菜单有子菜单，但子菜单没有角色关联
        when(menuMapper.selectById(menuId)).thenReturn(menu);
        when(menuMapper.selectList(any())).thenReturn(children).thenReturn(Collections.emptyList());
        when(menuMapper.countRoleMenuByMenuId(menuId)).thenReturn(0);
        when(menuMapper.countRoleMenuByMenuId(6L)).thenReturn(0);
        
        // 确保deleteById方法返回1，表示删除成功
        when(menuMapper.deleteById(eq(6L))).thenReturn(1);
        when(menuMapper.deleteById(eq(menuId))).thenReturn(1);
        when(menuMapper.deleteById(any(Menu.class))).thenReturn(1);
        
        // 避免递归调用导致堆栈溢出
        doReturn("6").when(spyMenuService).removeMenu(6L);
        
        // 执行测试
        String result = spyMenuService.removeMenu(menuId);
        
        // 验证结果
        assertEquals("5", result);
        verify(menuMapper).selectById(menuId);
        verify(menuMapper, atLeastOnce()).selectList(any());
        verify(menuMapper).countRoleMenuByMenuId(menuId);
        verify(menuMapper, atMostOnce()).countRoleMenuByMenuId(6L);
        verify(menuMapper, atMostOnce()).deleteById(6L);
        verify(menuMapper).deleteById(any(Menu.class)); // 使用any()而不是具体的menuId
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
            
            // 执行测试
            List<UserMenuVO> result = menuService.getCurrentUserMenus();
            
            // 验证结果
            assertNotNull(result);
            verify(menuMapper).selectMenusByUserId(userId);
        }
    }
    
    @Test
    void testGetCurrentUserMenusNotLoggedIn() {
        // 使用MockedStatic模拟静态方法
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            // 模拟用户未登录
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(null);
            
            // 执行测试并验证异常
            NoPermissionException exception = assertThrows(NoPermissionException.class, () -> {
                menuService.getCurrentUserMenus();
            });
            
            assertEquals("用户未登录", exception.getMessage());
            verify(menuMapper, never()).selectMenusByUserId(anyLong());
        }
    }
    
    @Test
    void testGetUserMenusForAdmin() {
        // 准备测试数据
        Long userId = 11L; // 假设ID为11的用户是管理员
        List<Menu> menuList = Arrays.asList(
            createMenu(1L, "系统管理", 0L),
            createMenu(2L, "用户管理", 1L)
        );
        
        // 使用MockedStatic模拟静态方法
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            // 模拟是管理员
            securityUtils.when(SecurityUtils::isAdmin).thenReturn(true);
            when(menuMapper.selectList(any())).thenReturn(menuList);
            
            // 执行测试
            List<UserMenuVO> result = menuService.getUserMenus(userId);
            
            // 验证结果
            assertNotNull(result);
            assertEquals(1, result.size()); // 只有一个顶级菜单
            assertEquals(1, result.get(0).getChildren().size()); // 有一个子菜单
            verify(menuMapper).selectList(any());
            verify(menuMapper, never()).selectMenusByUserId(anyLong());
        }
    }
    
    @Test
    void testGetUserMenus() {
        // 准备测试数据
        Long userId = 1L;
        List<Menu> menuList = Arrays.asList(
            createMenu(1L, "系统管理", 0L),
            createMenu(2L, "用户管理", 1L)
        );
        
        // 使用MockedStatic模拟静态方法
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            // 模拟不是管理员
            securityUtils.when(SecurityUtils::isAdmin).thenReturn(false);
            when(menuMapper.selectMenusByUserId(userId)).thenReturn(menuList);
            
            // 执行测试
            List<UserMenuVO> result = menuService.getUserMenus(userId);
            
            // 验证结果
            assertNotNull(result);
            assertEquals(1, result.size()); // 只有一个顶级菜单
            assertEquals(1, result.get(0).getChildren().size()); // 有一个子菜单
            verify(menuMapper).selectMenusByUserId(userId);
            verify(menuMapper, never()).selectList(any());
        }
    }
    
    @Test
    void testGetUserMenusWithEmptyList() {
        // 准备测试数据
        Long userId = 999L;
        
        // 使用MockedStatic模拟静态方法
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            // 模拟不是管理员
            securityUtils.when(SecurityUtils::isAdmin).thenReturn(false);
            when(menuMapper.selectMenusByUserId(userId)).thenReturn(Collections.emptyList());
            
            // 执行测试
            List<UserMenuVO> result = menuService.getUserMenus(userId);
            
            // 验证结果
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(menuMapper).selectMenusByUserId(userId);
        }
    }
    
    // 辅助方法：创建Menu对象
    private Menu createMenu(Long menuId, String menuName, Long parentId) {
        Menu menu = new Menu();
        menu.setMenuId(menuId);
        menu.setMenuName(menuName);
        menu.setParentId(parentId);
        menu.setPath("/" + menuName.toLowerCase().replace(" ", ""));
        menu.setComponent(menuName.replace(" ", ""));
        menu.setVisible(1);
        menu.setMenuType(parentId == 0L ? "M" : "C");
        menu.setOrderNum(menuId.intValue());
        return menu;
    }
} 