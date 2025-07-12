package com.systemManager.security.util;

import com.systemManager.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityUtilsTest {

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;
    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // 准备模拟SecurityContextHolder
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        
        // 设置SecurityContextHolder的静态方法
        mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @AfterEach
    void tearDown() {
        // 关闭静态模拟
        if (mockedSecurityContextHolder != null) {
            mockedSecurityContextHolder.close();
        }
    }

    @Test
    void testGetCurrentUser_WithUserPrincipal() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("testuser");
        
        // 模拟Authentication返回User类型的Principal
        when(authentication.getPrincipal()).thenReturn(mockUser);
        
        // 执行测试
        User result = SecurityUtils.getCurrentUser();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testGetCurrentUser_WithNonUserPrincipal() {
        // 模拟Authentication返回非User类型的Principal
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        
        // 执行测试
        User result = SecurityUtils.getCurrentUser();
        
        // 验证结果
        assertNull(result);
    }

    @Test
    void testGetCurrentUser_WithNullAuthentication() {
        // 模拟没有Authentication
        when(securityContext.getAuthentication()).thenReturn(null);
        
        // 执行测试
        User result = SecurityUtils.getCurrentUser();
        
        // 验证结果
        assertNull(result);
    }

    @Test
    void testGetCurrentUserId_WithValidUser() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setUserId(1L);
        
        // 模拟Authentication返回User类型的Principal
        when(authentication.getPrincipal()).thenReturn(mockUser);
        
        // 执行测试
        Long result = SecurityUtils.getCurrentUserId();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result);
    }

    @Test
    void testGetCurrentUserId_WithNullUser() {
        // 模拟没有用户
        when(authentication.getPrincipal()).thenReturn(null);
        
        // 执行测试
        Long result = SecurityUtils.getCurrentUserId();
        
        // 验证结果
        assertNull(result);
    }

    @Test
    void testGetAuthentication() {
        // 执行测试
        Authentication result = SecurityUtils.getAuthentication();
        
        // 验证结果
        assertNotNull(result);
        assertSame(authentication, result);
    }

    @Test
    void testIsAdmin_WithAdminUser() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("admin");
        
        // 执行测试
        boolean result = SecurityUtils.isAdmin(mockUser);
        
        // 验证结果
        assertTrue(result);
    }

    @Test
    void testIsAdmin_WithNonAdminUser() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("testuser");
        
        // 执行测试
        boolean result = SecurityUtils.isAdmin(mockUser);
        
        // 验证结果
        assertFalse(result);
    }

    @Test
    void testIsAdmin_WithNullUser() {
        // 执行测试
        boolean result = SecurityUtils.isAdmin((User) null);
        
        // 验证结果
        assertFalse(result);
    }

    @Test
    void testIsAdmin_WithNullUserId() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setUsername("admin");
        // userId为null
        
        // 执行测试
        boolean result = SecurityUtils.isAdmin(mockUser);
        
        // 验证结果
        assertFalse(result);
    }

    @Test
    void testIsAdmin_CurrentUser() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("admin");
        
        // 模拟当前用户是管理员
        when(authentication.getPrincipal()).thenReturn(mockUser);
        
        // 执行测试
        boolean result = SecurityUtils.isAdmin();
        
        // 验证结果
        assertTrue(result);
    }

    @Test
    void testIsAdmin_CurrentUserNotAdmin() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("testuser");
        
        // 模拟当前用户不是管理员
        when(authentication.getPrincipal()).thenReturn(mockUser);
        
        // 执行测试
        boolean result = SecurityUtils.isAdmin();
        
        // 验证结果
        assertFalse(result);
    }
} 