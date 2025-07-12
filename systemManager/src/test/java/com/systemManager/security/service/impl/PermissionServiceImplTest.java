package com.systemManager.security.service.impl;

import com.systemManager.entity.Menu;
import com.systemManager.entity.User;
import com.systemManager.mapper.MenuMapper;
import com.systemManager.security.util.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PermissionServiceImplTest {

    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityUtilsMock = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMock.close();
    }

    @Test
    void testGetUserPermissions_WithCurrentUser() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("testuser");
        
        Set<String> mockPermissions = new HashSet<>();
        mockPermissions.add("system:user:list");
        mockPermissions.add("system:user:add");
        
        // 模拟静态方法
        securityUtilsMock.when(SecurityUtils::getCurrentUser).thenReturn(mockUser);
        securityUtilsMock.when(() -> SecurityUtils.isAdmin(mockUser)).thenReturn(false);
        
        // 模拟依赖方法
        when(menuMapper.selectPermsByUserId(1L)).thenReturn(mockPermissions);
        
        // 执行测试
        Set<String> result = permissionService.getUserPermissions();
        
        // 验证结果
        assertEquals(2, result.size());
        assertTrue(result.contains("system:user:list"));
        assertTrue(result.contains("system:user:add"));
        
        // 验证方法调用
        verify(menuMapper).selectPermsByUserId(1L);
    }

    @Test
    void testGetUserPermissions_WithAdminUser() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("admin");
        
        List<Menu> mockMenus = new ArrayList<>();
        Menu menu1 = new Menu();
        menu1.setMenuId(1L);
        menu1.setPerms("system:user:list");
        Menu menu2 = new Menu();
        menu2.setMenuId(2L);
        menu2.setPerms("system:user:add");
        Menu menu3 = new Menu();
        menu3.setMenuId(3L);
        menu3.setPerms(""); // 测试空权限
        mockMenus.add(menu1);
        mockMenus.add(menu2);
        mockMenus.add(menu3);
        
        // 模拟静态方法
        securityUtilsMock.when(SecurityUtils::getCurrentUser).thenReturn(mockUser);
        securityUtilsMock.when(() -> SecurityUtils.isAdmin(mockUser)).thenReturn(true);
        
        // 模拟依赖方法
        when(menuMapper.selectAllMenus()).thenReturn(mockMenus);
        
        // 执行测试
        Set<String> result = permissionService.getUserPermissions();
        
        // 验证结果
        assertEquals(2, result.size());
        assertTrue(result.contains("system:user:list"));
        assertTrue(result.contains("system:user:add"));
        
        // 验证方法调用
        verify(menuMapper).selectAllMenus();
        verify(menuMapper, never()).selectPermsByUserId(anyLong());
    }

    @Test
    void testGetUserPermissions_WithNoUser() {
        // 模拟静态方法
        securityUtilsMock.when(SecurityUtils::getCurrentUser).thenReturn(null);
        
        // 执行测试
        Set<String> result = permissionService.getUserPermissions();
        
        // 验证结果
        assertTrue(result.isEmpty());
        
        // 验证方法调用
        verify(menuMapper, never()).selectPermsByUserId(anyLong());
        verify(menuMapper, never()).selectAllMenus();
    }

    @Test
    void testHasPermission_WithPermission() {
        // 准备测试数据
        String permission = "system:user:list";
        Set<String> mockPermissions = new HashSet<>();
        mockPermissions.add("system:user:list");
        mockPermissions.add("system:user:add");
        
        // 模拟方法
        PermissionServiceImpl spyService = spy(permissionService);
        doReturn(mockPermissions).when(spyService).getUserPermissions();
        
        // 执行测试
        boolean result = spyService.hasPermission(permission);
        
        // 验证结果
        assertTrue(result);
    }

    @Test
    void testHasPermission_WithWildcardPermission() {
        // 准备测试数据
        String permission = "system:user:list";
        Set<String> mockPermissions = new HashSet<>();
        mockPermissions.add("*:*:*");
        
        // 模拟方法
        PermissionServiceImpl spyService = spy(permissionService);
        doReturn(mockPermissions).when(spyService).getUserPermissions();
        
        // 执行测试
        boolean result = spyService.hasPermission(permission);
        
        // 验证结果
        assertTrue(result);
    }

    @Test
    void testHasPermission_WithoutPermission() {
        // 准备测试数据
        String permission = "system:user:delete";
        Set<String> mockPermissions = new HashSet<>();
        mockPermissions.add("system:user:list");
        mockPermissions.add("system:user:add");
        
        // 模拟方法
        PermissionServiceImpl spyService = spy(permissionService);
        doReturn(mockPermissions).when(spyService).getUserPermissions();
        
        // 执行测试
        boolean result = spyService.hasPermission(permission);
        
        // 验证结果
        assertFalse(result);
    }

    @Test
    void testHasPermission_WithNullPermission() {
        // 准备测试数据
        String permission = null;
        
        // 执行测试
        boolean result = permissionService.hasPermission(permission);
        
        // 验证结果
        assertFalse(result);
    }

    @Test
    void testHasPermission_WithEmptyPermission() {
        // 准备测试数据
        String permission = "";
        
        // 执行测试
        boolean result = permissionService.hasPermission(permission);
        
        // 验证结果
        assertFalse(result);
    }

    @Test
    void testHasAllPermissions_WithAllPermissions() {
        // 准备测试数据
        String[] permissions = {"system:user:list", "system:user:add"};
        Set<String> mockPermissions = new HashSet<>();
        mockPermissions.add("system:user:list");
        mockPermissions.add("system:user:add");
        mockPermissions.add("system:user:edit");
        
        // 模拟方法
        PermissionServiceImpl spyService = spy(permissionService);
        doReturn(mockPermissions).when(spyService).getUserPermissions();
        
        // 执行测试
        boolean result = spyService.hasAllPermissions(permissions);
        
        // 验证结果
        assertTrue(result);
    }

    @Test
    void testHasAllPermissions_WithWildcardPermission() {
        // 准备测试数据
        String[] permissions = {"system:user:list", "system:user:add"};
        Set<String> mockPermissions = new HashSet<>();
        mockPermissions.add("*:*:*");
        
        // 模拟方法
        PermissionServiceImpl spyService = spy(permissionService);
        doReturn(mockPermissions).when(spyService).getUserPermissions();
        
        // 执行测试
        boolean result = spyService.hasAllPermissions(permissions);
        
        // 验证结果
        assertTrue(result);
    }

    @Test
    void testHasAllPermissions_WithSomePermissions() {
        // 准备测试数据
        String[] permissions = {"system:user:list", "system:user:delete"};
        Set<String> mockPermissions = new HashSet<>();
        mockPermissions.add("system:user:list");
        mockPermissions.add("system:user:add");
        
        // 模拟方法
        PermissionServiceImpl spyService = spy(permissionService);
        doReturn(mockPermissions).when(spyService).getUserPermissions();
        
        // 执行测试
        boolean result = spyService.hasAllPermissions(permissions);
        
        // 验证结果
        assertFalse(result);
    }

    @Test
    void testHasAllPermissions_WithNullPermissions() {
        // 准备测试数据
        String[] permissions = null;
        
        // 执行测试
        boolean result = permissionService.hasAllPermissions(permissions);
        
        // 验证结果
        assertTrue(result);
    }

    @Test
    void testHasAllPermissions_WithEmptyPermissions() {
        // 准备测试数据
        String[] permissions = new String[0];
        
        // 执行测试
        boolean result = permissionService.hasAllPermissions(permissions);
        
        // 验证结果
        assertTrue(result);
    }

    @Test
    void testHasAnyPermission_WithOnePermission() {
        // 准备测试数据
        String[] permissions = {"system:user:list", "system:user:delete"};
        Set<String> mockPermissions = new HashSet<>();
        mockPermissions.add("system:user:list");
        mockPermissions.add("system:user:add");
        
        // 模拟方法
        PermissionServiceImpl spyService = spy(permissionService);
        doReturn(mockPermissions).when(spyService).getUserPermissions();
        
        // 执行测试
        boolean result = spyService.hasAnyPermission(permissions);
        
        // 验证结果
        assertTrue(result);
    }

    @Test
    void testHasAnyPermission_WithWildcardPermission() {
        // 准备测试数据
        String[] permissions = {"system:user:list", "system:user:delete"};
        Set<String> mockPermissions = new HashSet<>();
        mockPermissions.add("*:*:*");
        
        // 模拟方法
        PermissionServiceImpl spyService = spy(permissionService);
        doReturn(mockPermissions).when(spyService).getUserPermissions();
        
        // 执行测试
        boolean result = spyService.hasAnyPermission(permissions);
        
        // 验证结果
        assertTrue(result);
    }

    @Test
    void testHasAnyPermission_WithNoPermissions() {
        // 准备测试数据
        String[] permissions = {"system:user:delete", "system:user:export"};
        Set<String> mockPermissions = new HashSet<>();
        mockPermissions.add("system:user:list");
        mockPermissions.add("system:user:add");
        
        // 模拟方法
        PermissionServiceImpl spyService = spy(permissionService);
        doReturn(mockPermissions).when(spyService).getUserPermissions();
        
        // 执行测试
        boolean result = spyService.hasAnyPermission(permissions);
        
        // 验证结果
        assertFalse(result);
    }

    @Test
    void testHasAnyPermission_WithNullPermissions() {
        // 准备测试数据
        String[] permissions = null;
        
        // 执行测试
        boolean result = permissionService.hasAnyPermission(permissions);
        
        // 验证结果
        assertFalse(result);
    }

    @Test
    void testHasAnyPermission_WithEmptyPermissions() {
        // 准备测试数据
        String[] permissions = new String[0];
        
        // 执行测试
        boolean result = permissionService.hasAnyPermission(permissions);
        
        // 验证结果
        assertFalse(result);
    }
} 