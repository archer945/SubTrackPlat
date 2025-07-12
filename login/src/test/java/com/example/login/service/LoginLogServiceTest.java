package com.example.login.service;

import com.example.login.entity.LoginLog;
import com.example.login.mapper.LoginLogMapper;
import com.example.login.service.impl.LoginLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoginLogServiceTest {

    @Mock
    private LoginLogMapper loginLogMapper;

    @InjectMocks
    private LoginLogServiceImpl loginLogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRecordSuccessfulLogin() {
        // 准备测试数据
        Long userId = 1L;
        String username = "testuser";
        boolean success = true;
        String msg = "登录成功";

        // 模拟依赖方法的行为
        when(loginLogMapper.insert(any(LoginLog.class))).thenReturn(1);

        // 执行测试
        loginLogService.record(userId, username, success, msg);

        // 验证结果
        ArgumentCaptor<LoginLog> logCaptor = ArgumentCaptor.forClass(LoginLog.class);
        verify(loginLogMapper).insert(logCaptor.capture());
        
        LoginLog capturedLog = logCaptor.getValue();
        assertEquals(userId, capturedLog.getUserId());
        assertEquals(username, capturedLog.getUsername());
        assertEquals(1, capturedLog.getStatus());
        assertEquals(msg, capturedLog.getMsg());
    }

    @Test
    void testRecordFailedLogin() {
        // 准备测试数据
        Long userId = 1L;
        String username = "testuser";
        boolean success = false;
        String msg = "密码错误";

        // 模拟依赖方法的行为
        when(loginLogMapper.insert(any(LoginLog.class))).thenReturn(1);

        // 执行测试
        loginLogService.record(userId, username, success, msg);

        // 验证结果
        ArgumentCaptor<LoginLog> logCaptor = ArgumentCaptor.forClass(LoginLog.class);
        verify(loginLogMapper).insert(logCaptor.capture());
        
        LoginLog capturedLog = logCaptor.getValue();
        assertEquals(userId, capturedLog.getUserId());
        assertEquals(username, capturedLog.getUsername());
        assertEquals(0, capturedLog.getStatus());
        assertEquals(msg, capturedLog.getMsg());
    }

    @Test
    void testRecordLoginWithNullUserId() {
        // 准备测试数据
        Long userId = null;
        String username = "nonexistentuser";
        boolean success = false;
        String msg = "用户不存在";

        // 模拟依赖方法的行为
        when(loginLogMapper.insert(any(LoginLog.class))).thenReturn(1);

        // 执行测试
        loginLogService.record(userId, username, success, msg);

        // 验证结果
        ArgumentCaptor<LoginLog> logCaptor = ArgumentCaptor.forClass(LoginLog.class);
        verify(loginLogMapper).insert(logCaptor.capture());
        
        LoginLog capturedLog = logCaptor.getValue();
        assertNull(capturedLog.getUserId());
        assertEquals(username, capturedLog.getUsername());
        assertEquals(0, capturedLog.getStatus());
        assertEquals(msg, capturedLog.getMsg());
    }
} 