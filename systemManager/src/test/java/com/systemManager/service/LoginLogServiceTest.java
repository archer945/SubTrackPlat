package com.systemManager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.systemManager.entity.LoginLog;
import com.systemManager.mapper.LoginLogMapper;
import com.systemManager.service.impl.LoginLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    void testGetLoginLogPage() {
        // 准备测试数据
        Page<LoginLog> page = new Page<>(1, 10);
        Long userId = 1L;
        Integer status = 1;
        String startTime = "2023-01-01 00:00:00";
        String endTime = "2023-01-31 23:59:59";
        
        // 模拟Mapper返回结果
        Page<LoginLog> resultPage = new Page<>();
        resultPage.setTotal(5);
        when(loginLogMapper.selectLoginLogPage(any(), eq(userId), eq(status), eq(startTime), eq(endTime)))
                .thenReturn(resultPage);
        
        // 执行测试
        IPage<LoginLog> result = loginLogService.getLoginLogPage(page, userId, status, startTime, endTime);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(5, result.getTotal());
        verify(loginLogMapper, times(1)).selectLoginLogPage(any(), eq(userId), eq(status), eq(startTime), eq(endTime));
    }
    
    @Test
    void testGetLoginLogPageWithNullParameters() {
        // 准备测试数据
        Page<LoginLog> page = new Page<>(1, 10);
        
        // 模拟Mapper返回结果
        Page<LoginLog> resultPage = new Page<>();
        resultPage.setTotal(10);
        when(loginLogMapper.selectLoginLogPage(any(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(resultPage);
        
        // 执行测试
        IPage<LoginLog> result = loginLogService.getLoginLogPage(page, null, null, null, null);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(10, result.getTotal());
        verify(loginLogMapper, times(1)).selectLoginLogPage(any(), isNull(), isNull(), isNull(), isNull());
    }
} 