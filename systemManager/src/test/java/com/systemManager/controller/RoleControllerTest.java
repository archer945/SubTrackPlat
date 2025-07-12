package com.systemManager.controller;

import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.*;
import com.common.domain.query.systemManager.RoleQuery;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.RoleVO;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.service.IRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RoleControllerTest {

    @Mock
    private IRoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testList() {
        // 准备测试数据
        RoleQuery roleQuery = new RoleQuery();
        
        // 创建一个模拟的PageDTO对象
        PageDTO<RoleVO> pageDTO = mock(PageDTO.class);
        
        // 模拟服务方法
        when(roleService.listRoles(any(RoleQuery.class))).thenReturn(pageDTO);
        
        // 执行测试
        JsonVO<PageDTO<RoleVO>> result = roleController.list(roleQuery);
        
        // 验证结果
        assertEquals(pageDTO, result.getData());
        assertEquals(10000, result.getCode());
        verify(roleService).listRoles(roleQuery);
    }

    @Test
    void testAddRole() {
        // 准备测试数据
        RoleDTO roleDTO = new RoleDTO();
        
        // 模拟服务方法
        when(roleService.saveRole(any(RoleDTO.class))).thenReturn("添加成功");
        
        // 执行测试
        JsonVO<String> result = roleController.addRole(roleDTO);
        
        // 验证结果
        assertEquals("添加成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(roleService).saveRole(roleDTO);
    }

    @Test
    void testUpdateRole() {
        // 准备测试数据
        Long roleId = 1L;
        RoleDTO roleDTO = new RoleDTO();
        
        // 模拟服务方法
        when(roleService.updateRole(eq(roleId), any(RoleDTO.class))).thenReturn("更新成功");
        
        // 执行测试
        JsonVO<String> result = roleController.updateRole(roleId, roleDTO);
        
        // 验证结果
        assertEquals("更新成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(roleService).updateRole(roleId, roleDTO);
    }

    @Test
    void testRemoveRole() {
        // 准备测试数据
        Long roleId = 1L;
        
        // 模拟服务方法
        when(roleService.removeRole(roleId)).thenReturn("删除成功");
        
        // 执行测试
        JsonVO<String> result = roleController.removeRole(roleId);
        
        // 验证结果
        assertEquals("删除成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(roleService).removeRole(roleId);
    }

    @Test
    void testGetRoleMenus() {
        // 准备测试数据
        Long roleId = 1L;
        List<Long> menuIds = new ArrayList<>();
        menuIds.add(1L);
        menuIds.add(2L);
        
        // 模拟服务方法
        when(roleService.getRoleMenuIds(roleId)).thenReturn(menuIds);
        
        // 执行测试
        JsonVO<List<Long>> result = roleController.getRoleMenus(roleId);
        
        // 验证结果
        assertEquals(menuIds, result.getData());
        assertEquals(10000, result.getCode());
        verify(roleService).getRoleMenuIds(roleId);
    }

    @Test
    void testAssignRoleMenus() {
        // 准备测试数据
        Long roleId = 1L;
        RoleMenuDTO roleMenuDTO = new RoleMenuDTO();
        
        // 模拟服务方法
        when(roleService.assignRoleMenus(eq(roleId), any(RoleMenuDTO.class))).thenReturn(true);
        
        // 执行测试
        JsonVO<Boolean> result = roleController.assignRoleMenus(roleId, roleMenuDTO);
        
        // 验证结果
        assertTrue(result.getData());
        assertEquals(10000, result.getCode());
        verify(roleService).assignRoleMenus(roleId, roleMenuDTO);
    }

    @Test
    void testUpdateDataScope() {
        // 准备测试数据
        Long roleId = 1L;
        DataScopeDTO dataScopeDTO = new DataScopeDTO();
        
        // 模拟服务方法
        when(roleService.updateDataScope(eq(roleId), any(DataScopeDTO.class))).thenReturn(true);
        
        // 执行测试
        JsonVO<Boolean> result = roleController.updateDataScope(roleId, dataScopeDTO);
        
        // 验证结果
        assertTrue(result.getData());
        assertEquals(10000, result.getCode());
        verify(roleService).updateDataScope(roleId, dataScopeDTO);
    }

    @Test
    void testGetRoleUsers() {
        // 准备测试数据
        Long roleId = 1L;
        UserQuery userQuery = new UserQuery();
        
        // 创建一个模拟的PageDTO对象
        PageDTO<UserVO> pageDTO = mock(PageDTO.class);
        
        // 模拟服务方法
        when(roleService.getRoleUsers(eq(roleId), any(UserQuery.class))).thenReturn(pageDTO);
        
        // 执行测试
        JsonVO<PageDTO<UserVO>> result = roleController.getRoleUsers(roleId, userQuery);
        
        // 验证结果
        assertEquals(pageDTO, result.getData());
        assertEquals(10000, result.getCode());
        verify(roleService).getRoleUsers(roleId, userQuery);
    }

    @Test
    void testCopyRole() {
        // 准备测试数据
        Long sourceRoleId = 1L;
        RoleCopyDTO roleCopyDTO = new RoleCopyDTO();
        
        // 模拟服务方法
        when(roleService.copyRole(eq(sourceRoleId), any(RoleCopyDTO.class))).thenReturn("复制成功");
        
        // 执行测试
        JsonVO<String> result = roleController.copyRole(sourceRoleId, roleCopyDTO);
        
        // 验证结果
        assertEquals("复制成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(roleService).copyRole(sourceRoleId, roleCopyDTO);
    }
} 