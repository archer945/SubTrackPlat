package com.systemManager.security.aspect;

import com.systemManager.security.annotation.RequiresPermission;
import com.systemManager.security.exception.NoPermissionException;
import com.systemManager.security.service.PermissionService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PermissionAspectTest {

    @Mock
    private PermissionService permissionService;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @InjectMocks
    private PermissionAspect permissionAspect;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 测试方法
    @RequiresPermission(value = {"system:user:list", "system:user:add"}, logical = RequiresPermission.Logical.AND)
    public void testMethodWithAndPermission() {
        // 测试方法
    }

    // 测试方法
    @RequiresPermission(value = {"system:user:list", "system:user:add"}, logical = RequiresPermission.Logical.OR)
    public void testMethodWithOrPermission() {
        // 测试方法
    }

    // 测试方法，没有权限注解
    public void testMethodWithoutPermission() {
        // 测试方法
    }

    @Test
    void testCheckPermission_WithAndLogical_HasPermission() throws NoSuchMethodException {
        // 准备测试数据
        Method method = this.getClass().getDeclaredMethod("testMethodWithAndPermission");
        
        // 模拟方法签名
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        
        // 模拟权限检查
        when(permissionService.hasAllPermissions(new String[]{"system:user:list", "system:user:add"})).thenReturn(true);
        
        // 执行测试
        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
        
        // 验证方法调用
        verify(permissionService).hasAllPermissions(new String[]{"system:user:list", "system:user:add"});
        verify(permissionService, never()).hasAnyPermission(any());
    }

    @Test
    void testCheckPermission_WithAndLogical_NoPermission() throws NoSuchMethodException {
        // 准备测试数据
        Method method = this.getClass().getDeclaredMethod("testMethodWithAndPermission");
        
        // 模拟方法签名
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        
        // 模拟权限检查
        when(permissionService.hasAllPermissions(new String[]{"system:user:list", "system:user:add"})).thenReturn(false);
        
        // 执行测试并验证异常
        NoPermissionException exception = assertThrows(NoPermissionException.class, () -> {
            permissionAspect.checkPermission(joinPoint);
        });
        
        // 验证异常消息
        assertEquals("当前用户没有足够的权限", exception.getMessage());
        
        // 验证方法调用
        verify(permissionService).hasAllPermissions(new String[]{"system:user:list", "system:user:add"});
        verify(permissionService, never()).hasAnyPermission(any());
    }

    @Test
    void testCheckPermission_WithOrLogical_HasPermission() throws NoSuchMethodException {
        // 准备测试数据
        Method method = this.getClass().getDeclaredMethod("testMethodWithOrPermission");
        
        // 模拟方法签名
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        
        // 模拟权限检查
        when(permissionService.hasAnyPermission(new String[]{"system:user:list", "system:user:add"})).thenReturn(true);
        
        // 执行测试
        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
        
        // 验证方法调用
        verify(permissionService).hasAnyPermission(new String[]{"system:user:list", "system:user:add"});
        verify(permissionService, never()).hasAllPermissions(any());
    }

    @Test
    void testCheckPermission_WithOrLogical_NoPermission() throws NoSuchMethodException {
        // 准备测试数据
        Method method = this.getClass().getDeclaredMethod("testMethodWithOrPermission");
        
        // 模拟方法签名
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        
        // 模拟权限检查
        when(permissionService.hasAnyPermission(new String[]{"system:user:list", "system:user:add"})).thenReturn(false);
        
        // 执行测试并验证异常
        NoPermissionException exception = assertThrows(NoPermissionException.class, () -> {
            permissionAspect.checkPermission(joinPoint);
        });
        
        // 验证异常消息
        assertEquals("当前用户没有足够的权限", exception.getMessage());
        
        // 验证方法调用
        verify(permissionService).hasAnyPermission(new String[]{"system:user:list", "system:user:add"});
        verify(permissionService, never()).hasAllPermissions(any());
    }

    @Test
    void testCheckPermission_WithoutAnnotation() throws NoSuchMethodException {
        // 准备测试数据
        Method method = this.getClass().getDeclaredMethod("testMethodWithoutPermission");
        
        // 模拟方法签名
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        
        // 执行测试
        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
        
        // 验证方法调用
        verify(permissionService, never()).hasAllPermissions(any());
        verify(permissionService, never()).hasAnyPermission(any());
    }
} 