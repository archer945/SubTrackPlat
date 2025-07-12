package com.example.login.controller;

import com.example.login.entity.User;
import com.example.login.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        // 准备测试数据
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("password");
        user.setEmail("newuser@example.com");

        // 模拟依赖方法的行为
        when(userService.findByUsername("newuser")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userService.save(any(User.class))).thenReturn(true);

        // 执行测试
        Map<String, Object> result = authController.register(user);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("注册成功", result.get("message"));

        verify(userService).findByUsername("newuser");
        verify(passwordEncoder).encode("password");
        verify(userService).save(any(User.class));
        
        // 验证密码已加密
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());
        assertEquals("encodedPassword", userCaptor.getValue().getPassword());
    }

    @Test
    void testRegisterUsernameExists() {
        // 准备测试数据
        User user = new User();
        user.setUsername("existinguser");
        user.setPassword("password");
        user.setEmail("existinguser@example.com");

        User existingUser = new User();
        existingUser.setUserId(1L);
        existingUser.setUsername("existinguser");

        // 模拟依赖方法的行为
        when(userService.findByUsername("existinguser")).thenReturn(existingUser);

        // 执行测试
        Map<String, Object> result = authController.register(user);

        // 验证结果
        assertNotNull(result);
        assertEquals(400, result.get("code"));
        assertEquals("用户名已存在", result.get("message"));

        verify(userService).findByUsername("existinguser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userService, never()).save(any(User.class));
    }

    @Test
    void testResetPasswordSuccess() {
        // 准备测试数据
        String username = "testuser";
        
        User user = new User();
        user.setUserId(1L);
        user.setUsername(username);
        user.setPassword("oldEncodedPassword");

        // 模拟依赖方法的行为
        when(userService.findByUsername(username)).thenReturn(user);
        when(passwordEncoder.encode("123456")).thenReturn("newEncodedPassword");
        doNothing().when(userService).updateByUserId(any(User.class));

        // 执行测试
        Map<String, Object> result = authController.resetPassword(username);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("密码已重置为123456", result.get("message"));

        verify(userService).findByUsername(username);
        verify(passwordEncoder).encode("123456");
        
        // 验证密码已更新
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).updateByUserId(userCaptor.capture());
        assertEquals("newEncodedPassword", userCaptor.getValue().getPassword());
    }

    @Test
    void testResetPasswordUserNotFound() {
        // 准备测试数据
        String username = "nonexistentuser";

        // 模拟依赖方法的行为
        when(userService.findByUsername(username)).thenReturn(null);

        // 执行测试
        Map<String, Object> result = authController.resetPassword(username);

        // 验证结果
        assertNotNull(result);
        assertEquals(404, result.get("code"));
        assertEquals("用户不存在", result.get("message"));

        verify(userService).findByUsername(username);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userService, never()).updateByUserId(any(User.class));
    }
} 