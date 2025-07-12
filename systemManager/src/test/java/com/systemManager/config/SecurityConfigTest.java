package com.systemManager.config;

import com.systemManager.security.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Test
    void testPasswordEncoder() {
        // 创建配置实例
        SecurityConfig securityConfig = new SecurityConfig();
        
        // 获取密码编码器
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        
        // 验证返回的是BCryptPasswordEncoder实例
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
        
        // 测试编码功能
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        // 验证编码后的密码不等于原始密码
        assertNotEquals(rawPassword, encodedPassword);
        
        // 验证可以正确匹配
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }
    
    @Test
    void testCorsConfigurationSource() {
        // 创建配置实例
        SecurityConfig securityConfig = new SecurityConfig();
        
        // 获取CORS配置源
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();
        
        // 验证不为空
        assertNotNull(corsConfigurationSource);
    }
    
    @Test
    void testSecurityFilterChain() throws Exception {
        // 创建配置实例
        SecurityConfig securityConfig = new SecurityConfig();
        
        // 模拟依赖
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        JwtAuthenticationFilter jwtAuthenticationFilter = mock(JwtAuthenticationFilter.class);
        DefaultSecurityFilterChain mockFilterChain = mock(DefaultSecurityFilterChain.class);
        
        // 设置模拟行为
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(mockFilterChain);
        
        // 使用反射设置私有字段
        ReflectionTestUtils.setField(securityConfig, "jwtAuthenticationFilter", jwtAuthenticationFilter);
        
        // 调用方法
        SecurityFilterChain securityFilterChain = securityConfig.securityFilterChain(httpSecurity);
        
        // 验证不为空
        assertNotNull(securityFilterChain);
        
        // 验证方法调用
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).addFilterBefore(any(), any());
        verify(httpSecurity).build();
    }
} 