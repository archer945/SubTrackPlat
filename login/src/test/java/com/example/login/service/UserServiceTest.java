package com.example.login.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.login.entity.User;
import com.example.login.mapper.UserMapper;
import com.example.login.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 手动设置baseMapper字段，解决NullPointerException问题
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);
    }

    @Test
    void testFindByUsernameSuccess() {
        // 准备测试数据
        String username = "testuser";
        User expectedUser = new User();
        expectedUser.setUserId(1L);
        expectedUser.setUsername(username);
        expectedUser.setPassword("encodedPassword");

        // 模拟依赖方法的行为，修改为匹配两个参数
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(expectedUser);

        // 执行测试
        User result = userService.findByUsername(username);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(username, result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        
        verify(userMapper).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void testFindByUsernameNotFound() {
        // 准备测试数据
        String username = "nonexistentuser";

        // 模拟依赖方法的行为，修改为匹配两个参数
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(null);

        // 执行测试
        User result = userService.findByUsername(username);

        // 验证结果
        assertNull(result);
        
        verify(userMapper).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void testUpdateByUserIdSuccess() {
        // 准备测试数据
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setPassword("newEncodedPassword");

        // 模拟依赖方法的行为
        when(userMapper.update(any(User.class), any(UpdateWrapper.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userService.updateByUserId(user));

        // 验证结果
        verify(userMapper).update(any(User.class), any(UpdateWrapper.class));
    }

    @Test
    void testUpdateByUserIdWithNullUser() {
        // 准备测试数据
        User user = null;

        // 执行测试并验证异常
        assertThrows(NullPointerException.class, () -> userService.updateByUserId(user));

        // 验证结果
        verify(userMapper, never()).update(any(User.class), any(UpdateWrapper.class));
    }
} 