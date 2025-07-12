package com.systemManager.security.annotation;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class RequiresPermissionTest {

    @RequiresPermission(value = {"system:user:list"})
    public void methodWithDefaultLogical() {
        // 测试方法
    }

    @RequiresPermission(value = {"system:user:list", "system:user:add"}, logical = RequiresPermission.Logical.AND)
    public void methodWithAndLogical() {
        // 测试方法
    }

    @RequiresPermission(value = {"system:user:list", "system:user:add"}, logical = RequiresPermission.Logical.OR)
    public void methodWithOrLogical() {
        // 测试方法
    }

    @Test
    void testRequiresPermissionDefaultLogical() throws NoSuchMethodException {
        // 获取测试方法
        Method method = getClass().getDeclaredMethod("methodWithDefaultLogical");
        
        // 获取注解
        RequiresPermission annotation = method.getAnnotation(RequiresPermission.class);
        
        // 验证注解属性
        assertNotNull(annotation);
        assertEquals(1, annotation.value().length);
        assertEquals("system:user:list", annotation.value()[0]);
        assertEquals(RequiresPermission.Logical.AND, annotation.logical());
    }

    @Test
    void testRequiresPermissionAndLogical() throws NoSuchMethodException {
        // 获取测试方法
        Method method = getClass().getDeclaredMethod("methodWithAndLogical");
        
        // 获取注解
        RequiresPermission annotation = method.getAnnotation(RequiresPermission.class);
        
        // 验证注解属性
        assertNotNull(annotation);
        assertEquals(2, annotation.value().length);
        assertEquals("system:user:list", annotation.value()[0]);
        assertEquals("system:user:add", annotation.value()[1]);
        assertEquals(RequiresPermission.Logical.AND, annotation.logical());
    }

    @Test
    void testRequiresPermissionOrLogical() throws NoSuchMethodException {
        // 获取测试方法
        Method method = getClass().getDeclaredMethod("methodWithOrLogical");
        
        // 获取注解
        RequiresPermission annotation = method.getAnnotation(RequiresPermission.class);
        
        // 验证注解属性
        assertNotNull(annotation);
        assertEquals(2, annotation.value().length);
        assertEquals("system:user:list", annotation.value()[0]);
        assertEquals("system:user:add", annotation.value()[1]);
        assertEquals(RequiresPermission.Logical.OR, annotation.logical());
    }

    @Test
    void testLogicalEnumValues() {
        // 验证枚举值
        RequiresPermission.Logical[] values = RequiresPermission.Logical.values();
        assertEquals(2, values.length);
        assertEquals(RequiresPermission.Logical.AND, values[0]);
        assertEquals(RequiresPermission.Logical.OR, values[1]);
    }
} 