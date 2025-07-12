package com.systemManager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.domain.dto.PageDTO;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.LoginLogVO;
import com.systemManager.entity.LoginLog;
import com.systemManager.service.LoginLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LoginLogControllerTest {

    @Mock
    private LoginLogService loginLogService;

    @Mock
    private IPage<LoginLog> pageData;

    @InjectMocks
    private LoginLogController loginLogController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLoginLogs() {
        // 准备测试数据
        Integer pageIndex = 1;
        Integer pageSize = 10;
        Integer status = null;
        String startTime = null;
        String endTime = null;
        
        List<LoginLog> loginLogs = new ArrayList<>();
        LoginLog loginLog = mock(LoginLog.class);
        loginLogs.add(loginLog);
        
        // 模拟服务方法
        when(loginLogService.getLoginLogPage(any(Page.class), eq(null), eq(status), eq(startTime), eq(endTime)))
            .thenReturn(pageData);
        when(pageData.getRecords()).thenReturn(loginLogs);
        when(pageData.getTotal()).thenReturn(1L);
        when(pageData.getPages()).thenReturn(1L);
        
        // 执行测试
        JsonVO<PageDTO<LoginLogVO>> result = loginLogController.getLoginLogs(pageIndex, pageSize, status, startTime, endTime);
        
        // 验证结果
        assertEquals(10000, result.getCode());
        assertEquals(1L, result.getData().getTotal());
        assertEquals(1L, result.getData().getPages());
        assertEquals(10L, result.getData().getPageSize());
        assertEquals(1L, result.getData().getPageIndex());
        verify(loginLogService).getLoginLogPage(any(Page.class), eq(null), eq(status), eq(startTime), eq(endTime));
    }

    @Test
    void testGetUserLoginLogs() {
        // 准备测试数据
        Long userId = 1L;
        Integer pageIndex = 1;
        Integer pageSize = 10;
        Integer status = null;
        String startTime = null;
        String endTime = null;
        
        List<LoginLog> loginLogs = new ArrayList<>();
        LoginLog loginLog = mock(LoginLog.class);
        loginLogs.add(loginLog);
        
        // 模拟服务方法
        when(loginLogService.getLoginLogPage(any(Page.class), eq(userId), eq(status), eq(startTime), eq(endTime)))
            .thenReturn(pageData);
        when(pageData.getRecords()).thenReturn(loginLogs);
        when(pageData.getTotal()).thenReturn(1L);
        when(pageData.getPages()).thenReturn(1L);
        
        // 执行测试
        JsonVO<PageDTO<LoginLogVO>> result = loginLogController.getUserLoginLogs(userId, pageIndex, pageSize, status, startTime, endTime);
        
        // 验证结果
        assertEquals(10000, result.getCode());
        assertEquals(1L, result.getData().getTotal());
        assertEquals(1L, result.getData().getPages());
        assertEquals(10L, result.getData().getPageSize());
        assertEquals(1L, result.getData().getPageIndex());
        verify(loginLogService).getLoginLogPage(any(Page.class), eq(userId), eq(status), eq(startTime), eq(endTime));
    }
} 