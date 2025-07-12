package com.systemManager.security.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoPermissionExceptionTest {

    @Test
    void testNoPermissionExceptionDefaultConstructor() {
        // 创建异常对象
        NoPermissionException exception = new NoPermissionException();
        
        // 验证默认消息
        assertEquals("没有权限执行此操作", exception.getMessage());
    }

    @Test
    void testNoPermissionExceptionWithCustomMessage() {
        // 创建异常对象
        String customMessage = "用户无权访问该资源";
        NoPermissionException exception = new NoPermissionException(customMessage);
        
        // 验证自定义消息
        assertEquals(customMessage, exception.getMessage());
    }
} 