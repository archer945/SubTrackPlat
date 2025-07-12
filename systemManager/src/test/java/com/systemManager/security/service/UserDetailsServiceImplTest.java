package com.systemManager.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.systemManager.entity.User;
import com.systemManager.mapper.MenuMapper;
import com.systemManager.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        // 准备测试数据
        String username = "testuser";
        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword("password");

        Set<String> permissions = new HashSet<>();
        permissions.add("system:user:list");
        permissions.add("system:user:add");

        // 模拟依赖方法的行为
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(mockUser);
        when(menuMapper.selectPermsByUserId(1L)).thenReturn(permissions);

        // 执行测试
        User result = userDetailsService.loadUserByUsername(username);

        // 验证结果
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(1L, result.getUserId());
        
        // 验证权限
        assertNotNull(result.getAuthorities());
        assertEquals(2, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("system:user:list")));
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("system:user:add")));

        // 验证方法调用
        verify(userMapper).selectOne(any(LambdaQueryWrapper.class));
        verify(menuMapper).selectPermsByUserId(1L);
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        // 准备测试数据
        String username = "nonexistent";

        // 模拟依赖方法的行为
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // 执行测试并验证异常
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });

        // 验证异常消息
        assertEquals("用户不存在", exception.getMessage());

        // 验证方法调用
        verify(userMapper).selectOne(any(LambdaQueryWrapper.class));
        verify(menuMapper, never()).selectPermsByUserId(anyLong());
    }
} 