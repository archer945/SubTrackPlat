package com.systemManager.security.filter;

import com.systemManager.entity.User;
import com.systemManager.security.service.UserDetailsServiceImpl;
import com.systemManager.security.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 确保每个测试前清空SecurityContext
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // 确保每个测试后清空SecurityContext
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_WithValidToken() throws ServletException, IOException {
        // 准备测试数据
        String token = "valid_token";
        String username = "testuser";
        Long userId = 1L;
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setAuthorities(authorities);

        // 模拟请求头中有token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        
        // 模拟token验证和解析
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getUsernameFromToken(token)).thenReturn(username);
        when(jwtUtils.getUserIdFromToken(token)).thenReturn(userId);
        
        // 模拟用户加载
        when(userDetailsService.loadUserByUsername(username)).thenReturn(user);

        // 执行过滤器
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证认证信息是否正确设置
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(username, ((User) authentication.getPrincipal()).getUsername());
        assertEquals(userId, ((User) authentication.getPrincipal()).getUserId());
        
        // 验证过滤器链是否继续执行
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithInvalidToken() throws ServletException, IOException {
        // 准备测试数据
        String token = "invalid_token";

        // 模拟请求头中有token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        
        // 模拟token验证失败
        when(jwtUtils.validateToken(token)).thenReturn(false);

        // 执行过滤器
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证认证信息未设置
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        // 验证过滤器链是否继续执行
        verify(filterChain).doFilter(request, response);
        
        // 验证未调用用户加载
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void testDoFilterInternal_WithNoToken() throws ServletException, IOException {
        // 模拟请求头中没有token
        when(request.getHeader("Authorization")).thenReturn(null);

        // 执行过滤器
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证认证信息未设置
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        // 验证过滤器链是否继续执行
        verify(filterChain).doFilter(request, response);
        
        // 验证未调用token验证和用户加载
        verify(jwtUtils, never()).validateToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void testDoFilterInternal_WithMalformedToken() throws ServletException, IOException {
        // 准备测试数据 - 不是以Bearer开头
        when(request.getHeader("Authorization")).thenReturn("Token invalid_format");

        // 执行过滤器
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证认证信息未设置
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        // 验证过滤器链是否继续执行
        verify(filterChain).doFilter(request, response);
        
        // 验证未调用token验证和用户加载
        verify(jwtUtils, never()).validateToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void testDoFilterInternal_WithTokenButUserNotFound() throws ServletException, IOException {
        // 准备测试数据
        String token = "valid_token";
        String username = "nonexistent";

        // 模拟请求头中有token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        
        // 模拟token验证和解析
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getUsernameFromToken(token)).thenReturn(username);
        
        // 模拟用户不存在
        when(userDetailsService.loadUserByUsername(username)).thenReturn(null);

        // 执行过滤器
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证认证信息未设置
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        // 验证过滤器链是否继续执行
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithTokenButExceptionThrown() throws ServletException, IOException {
        // 准备测试数据
        String token = "valid_token";

        // 模拟请求头中有token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        
        // 模拟token验证成功但解析异常
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getUsernameFromToken(token)).thenThrow(new RuntimeException("Token解析异常"));

        // 执行过滤器
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证认证信息未设置
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        // 验证过滤器链是否继续执行
        verify(filterChain).doFilter(request, response);
    }
} 