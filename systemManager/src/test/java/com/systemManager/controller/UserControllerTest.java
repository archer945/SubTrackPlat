package com.systemManager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.*;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.service.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private IUserService userService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testQueryUserList() {
        // 准备测试数据
        UserQuery userQuery = new UserQuery();
        
        // 创建一个模拟的PageDTO对象
        PageDTO<UserVO> pageDTO = mock(PageDTO.class);
        
        // 模拟服务方法
        when(userService.listUser(any(UserQuery.class))).thenReturn(pageDTO);
        
        // 执行测试
        JsonVO<PageDTO<UserVO>> result = userController.queryUserList(userQuery);
        
        // 验证结果
        assertEquals(pageDTO, result.getData());
        assertEquals(10000, result.getCode());
        verify(userService).listUser(userQuery);
    }

    @Test
    void testAddUser() {
        // 准备测试数据
        AddUserDTO addUserDTO = new AddUserDTO();
        
        // 模拟服务方法
        when(userService.saveUser(any(AddUserDTO.class))).thenReturn("添加成功");
        
        // 执行测试
        JsonVO<String> result = userController.addUser(addUserDTO);
        
        // 验证结果
        assertEquals("添加成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(userService).saveUser(addUserDTO);
    }

    @Test
    void testUpdateUser() {
        // 准备测试数据
        Long userId = 1L;
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        
        // 模拟服务方法
        when(userService.updateUser(eq(userId), any(UpdateUserDTO.class))).thenReturn("更新成功");
        
        // 执行测试
        JsonVO<String> result = userController.updateUser(userId, updateUserDTO);
        
        // 验证结果
        assertEquals("更新成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(userService).updateUser(userId, updateUserDTO);
    }

    @Test
    void testRemoveUser() {
        // 准备测试数据
        Long userId = 1L;
        
        // 模拟服务方法
        when(userService.removeUser(userId)).thenReturn("删除成功");
        
        // 执行测试
        JsonVO<String> result = userController.removeUser(userId);
        
        // 验证结果
        assertEquals("删除成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(userService).removeUser(userId);
    }

    @Test
    void testResetPassword() {
        // 准备测试数据
        Long userId = 1L;
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        
        // 模拟服务方法
        when(userService.resetPassword(eq(userId), any(ResetPasswordDTO.class))).thenReturn("密码重置成功");
        
        // 执行测试
        JsonVO<String> result = userController.resetPassword(userId, resetPasswordDTO);
        
        // 验证结果
        assertEquals("密码重置成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(userService).resetPassword(userId, resetPasswordDTO);
    }

    @Test
    void testAssignRoles() {
        // 准备测试数据
        UserRoleDTO userRoleDTO = new UserRoleDTO();
        userRoleDTO.setUserId(1L);
        List<Long> roleIds = new ArrayList<>();
        roleIds.add(1L);
        roleIds.add(2L);
        userRoleDTO.setRoleIds(roleIds);
        
        // 模拟服务方法
        when(userService.assignRoles(eq(1L), eq(roleIds))).thenReturn(true);
        
        // 执行测试
        JsonVO<Boolean> result = userController.assignRoles(userRoleDTO);
        
        // 验证结果
        assertTrue(result.getData());
        assertEquals(10000, result.getCode());
        verify(userService).assignRoles(1L, roleIds);
    }

    @Test
    void testGetUserRoles() {
        // 准备测试数据
        Long userId = 1L;
        List<Long> roleIds = new ArrayList<>();
        roleIds.add(1L);
        roleIds.add(2L);
        
        // 模拟服务方法
        when(userService.getRoleIdsByUserId(userId)).thenReturn(roleIds);
        
        // 执行测试
        JsonVO<List<Long>> result = userController.getUserRoles(userId);
        
        // 验证结果
        assertEquals(roleIds, result.getData());
        assertEquals(10000, result.getCode());
        verify(userService).getRoleIdsByUserId(userId);
    }

    @Test
    void testBatchRemoveUsers() {
        // 准备测试数据
        BatchUserDTO batchUserDTO = new BatchUserDTO();
        List<Long> userIds = new ArrayList<>();
        userIds.add(1L);
        userIds.add(2L);
        batchUserDTO.setUserIds(userIds);
        
        // 模拟服务方法
        when(userService.batchRemoveUsers(any(BatchUserDTO.class))).thenReturn("批量删除成功");
        
        // 执行测试
        JsonVO<String> result = userController.batchRemoveUsers(batchUserDTO);
        
        // 验证结果
        assertEquals("批量删除成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(userService).batchRemoveUsers(batchUserDTO);
    }

    @Test
    void testImportUsers() throws IOException {
        // 准备测试数据
        Map<String, Object> importResult = new HashMap<>();
        importResult.put("success", 5);
        importResult.put("fail", 0);
        
        // 模拟服务方法
        when(userService.importUsers(any(MultipartFile.class))).thenReturn(importResult);
        
        // 执行测试
        JsonVO<Map<String, Object>> result = userController.importUsers(file);
        
        // 验证结果
        assertEquals(importResult, result.getData());
        assertEquals(10000, result.getCode());
        verify(userService).importUsers(file);
    }

    @Test
    void testExportUsers() throws IOException {
        // 准备测试数据
        UserQuery userQuery = new UserQuery();
        
        // 执行测试
        userController.exportUsers(userQuery, response);
        
        // 验证服务方法被调用
        verify(userService).exportUsers(userQuery, response);
    }
} 