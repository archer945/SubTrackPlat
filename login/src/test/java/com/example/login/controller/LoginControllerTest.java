package com.example.login.controller;

import com.example.login.config.JwtUtils;
import com.example.login.dto.UserDTO;
import com.example.login.entity.User;
import com.example.login.service.CaptchaService;
import com.example.login.service.LoginLogService;
import com.example.login.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private CaptchaService captchaService;

    @Mock
    private LoginLogService loginLogService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");

        User user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        // 模拟依赖方法的行为
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken("testuser")).thenReturn("jwt-token");
        doNothing().when(loginLogService).record(anyLong(), anyString(), anyBoolean(), anyString());
        doNothing().when(userService).updateByUserId(any(User.class));

        // 执行测试
        Map<String, Object> result = loginController.login(userDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.get("code"));
        assertEquals("登录成功", result.get("message"));
        assertEquals("jwt-token", result.get("token"));
        assertEquals(1L, result.get("userId"));
        assertEquals("testuser", result.get("username"));

        verify(userService).findByUsername("testuser");
        verify(passwordEncoder).matches("password", "encodedPassword");
        verify(jwtUtils).generateToken("testuser");
        verify(loginLogService).record(eq(1L), eq("testuser"), eq(true), eq("登录成功"));
        verify(userService).updateByUserId(any(User.class));
    }

    @Test
    void testLoginUserNotFound() {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("nonexistentuser");
        userDTO.setPassword("password");

        // 模拟依赖方法的行为
        when(userService.findByUsername("nonexistentuser")).thenReturn(null);
        doNothing().when(loginLogService).record(isNull(), eq("nonexistentuser"), eq(false), eq("用户不存在"));

        // 执行测试
        Map<String, Object> result = loginController.login(userDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(401, result.get("code"));
        assertEquals("用户不存在", result.get("message"));

        verify(userService).findByUsername("nonexistentuser");
        verify(loginLogService).record(isNull(), eq("nonexistentuser"), eq(false), eq("用户不存在"));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtils, never()).generateToken(anyString());
        verify(userService, never()).updateByUserId(any(User.class));
    }

    @Test
    void testLoginWrongPassword() {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("wrongpassword");

        User user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        // 模拟依赖方法的行为
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);
        doNothing().when(loginLogService).record(eq(1L), eq("testuser"), eq(false), eq("密码错误"));

        // 执行测试
        Map<String, Object> result = loginController.login(userDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(403, result.get("code"));
        assertEquals("密码错误", result.get("message"));

        verify(userService).findByUsername("testuser");
        verify(passwordEncoder).matches("wrongpassword", "encodedPassword");
        verify(loginLogService).record(eq(1L), eq("testuser"), eq(false), eq("密码错误"));
        verify(jwtUtils, never()).generateToken(anyString());
        verify(userService, never()).updateByUserId(any(User.class));
    }
} 