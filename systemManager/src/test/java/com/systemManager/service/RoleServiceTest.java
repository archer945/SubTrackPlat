package com.systemManager.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.DataScopeDTO;
import com.common.domain.dto.systemManager.RoleCopyDTO;
import com.common.domain.dto.systemManager.RoleDTO;
import com.common.domain.dto.systemManager.RoleMenuDTO;
import com.common.domain.query.systemManager.RoleQuery;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.systemManager.RoleVO;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.entity.Role;
import com.systemManager.entity.RoleMenu;
import com.systemManager.mapper.RoleMapper;
import com.systemManager.mapper.RoleMenuMapper;
import com.systemManager.mapper.UserMapper;
import com.systemManager.mapper.ms.RoleMsMapper;
import com.systemManager.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleMapper roleMapper;
    
    @Mock
    private RoleMenuMapper roleMenuMapper;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private RoleMsMapper roleMsMapper;
    
    @InjectMocks
    private RoleServiceImpl roleService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testSaveRoleSuccess() {
        // 准备测试数据
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleName("测试角色");
        roleDTO.setRoleCode("test_role");
        
        Role role = new Role();
        role.setRoleId(1L);
        
        // 模拟私有方法行为
        ReflectionTestUtils.setField(roleService, "roleMapper", roleMapper);
        ReflectionTestUtils.setField(roleService, "msMapper", roleMsMapper);
        
        // 模拟依赖方法的行为
        when(roleMsMapper.dtoToDo(any())).thenReturn(role);
        when(roleMapper.insert(any())).thenReturn(1);
        
        // 执行测试
        String result = roleService.saveRole(roleDTO);
        
        // 验证结果
        assertEquals("1", result);
        verify(roleMsMapper).dtoToDo(any());
        verify(roleMapper).insert(any());
    }
    
    @Test
    void testUpdateRoleSuccess() {
        // 准备测试数据
        Long roleId = 2L;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleName("更新角色");
        roleDTO.setRoleCode("update_role");
        
        Role existingRole = new Role();
        existingRole.setRoleId(roleId);
        existingRole.setRoleName("原角色");
        existingRole.setRoleCode("old_code");
        
        // 模拟依赖方法的行为
        when(roleMapper.selectById(roleId)).thenReturn(existingRole);
        when(roleMsMapper.dtoToDo(any())).thenReturn(existingRole);
        when(roleMapper.updateById(any())).thenReturn(1);
        // 修改为正确的mock方式
        when(roleMenuMapper.delete(any())).thenReturn(1);
        
        // 执行测试
        String result = roleService.updateRole(roleId, roleDTO);
        
        // 验证结果
        assertEquals("2", result);
        verify(roleMapper).selectById(roleId);
        verify(roleMsMapper).dtoToDo(any());
        verify(roleMapper).updateById(any());
        verify(roleMenuMapper).delete(any());
    }
    
    @Test
    void testRemoveRoleSuccess() {
        // 准备测试数据
        Long roleId = 2L;
        Role role = new Role();
        role.setRoleId(roleId);
        role.setRoleName("普通角色");
        
        // 模拟依赖方法的行为
        when(roleMapper.selectById(roleId)).thenReturn(role);
        when(roleMapper.countUserRoleByRoleId(roleId)).thenReturn(0);
        // 修改为正确的mock方式
        when(roleMenuMapper.delete(any())).thenReturn(1);
        when(roleMapper.deleteById(any())).thenReturn(1);
        
        // 执行测试
        String result = roleService.removeRole(roleId);
        
        // 验证结果
        assertEquals("2", result);
        verify(roleMapper).selectById(roleId);
        verify(roleMapper).countUserRoleByRoleId(roleId);
        verify(roleMenuMapper).delete(any());
        verify(roleMapper).deleteById(any());
    }
    
    @Test
    void testRemoveRoleWithUsers() {
        // 准备测试数据
        Long roleId = 2L;
        Role role = new Role();
        role.setRoleId(roleId);
        role.setRoleName("普通角色");
        
        // 模拟角色下有用户
        when(roleMapper.selectById(roleId)).thenReturn(role);
        when(roleMapper.countUserRoleByRoleId(roleId)).thenReturn(5);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.removeRole(roleId);
        });
        
        assertTrue(exception.getMessage().contains("已分配用户"));
        verify(roleMapper).selectById(roleId);
        verify(roleMapper).countUserRoleByRoleId(roleId);
        verify(roleMenuMapper, never()).delete(any());
        verify(roleMapper, never()).deleteById(any());
    }
    
    @Test
    void testGetRoleMenuIds() {
        // 准备测试数据
        Long roleId = 1L;
        List<Long> menuIds = Arrays.asList(1L, 2L, 3L);
        
        Role role = new Role();
        role.setRoleId(roleId);
        
        List<RoleMenu> roleMenus = new ArrayList<>();
        for (Long menuId : menuIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenus.add(roleMenu);
        }
        
        // 模拟依赖方法的行为
        when(roleMapper.selectById(roleId)).thenReturn(role);
        when(roleMenuMapper.selectList(any())).thenReturn(roleMenus);
        
        // 执行测试
        List<Long> result = roleService.getRoleMenuIds(roleId);
        
        // 验证结果
        assertEquals(3, result.size());
        assertTrue(result.containsAll(menuIds));
        verify(roleMapper).selectById(roleId);
        verify(roleMenuMapper).selectList(any());
    }
    
    @Test
    void testAssignRoleMenus() {
        // 准备测试数据
        Long roleId = 1L;
        RoleMenuDTO dto = new RoleMenuDTO();
        List<Long> menuIds = Arrays.asList(1L, 2L, 3L);
        dto.setMenuIds(menuIds);
        
        Role role = new Role();
        role.setRoleId(roleId);
        
        // 模拟依赖方法的行为
        when(roleMapper.selectById(roleId)).thenReturn(role);
        // 修改为正确的mock方式
        when(roleMenuMapper.delete(any())).thenReturn(1);
        
        // 执行测试
        boolean result = roleService.assignRoleMenus(roleId, dto);
        
        // 验证结果
        assertTrue(result);
        verify(roleMapper).selectById(roleId);
        verify(roleMenuMapper).delete(any());
    }
    
    @Test
    void testUpdateDataScope() {
        // 准备测试数据
        Long roleId = 1L;
        DataScopeDTO dto = new DataScopeDTO();
        dto.setDataScope("2");
        List<Long> deptIds = Arrays.asList(1L, 2L);
        dto.setDeptIds(deptIds);
        
        Role role = new Role();
        role.setRoleId(roleId);
        
        // 模拟依赖方法的行为
        when(roleMapper.selectById(roleId)).thenReturn(role);
        when(roleMapper.updateById(any())).thenReturn(1);
        
        // 执行测试
        boolean result = roleService.updateDataScope(roleId, dto);
        
        // 验证结果
        assertTrue(result);
        verify(roleMapper).selectById(roleId);
        verify(roleMapper).updateById(any());
    }
    
    @Test
    void testCopyRole() {
        // 准备测试数据
        Long sourceRoleId = 1L;
        RoleCopyDTO dto = new RoleCopyDTO();
        dto.setRoleName("复制角色");
        dto.setRoleCode("copy_role");
        dto.setStatus("1"); // 使用正确的status属性
        dto.setDataScope("1"); // 添加数据权限范围
        dto.setCopyPermissions(true); // 设置复制权限为true
        
        Role sourceRole = new Role();
        sourceRole.setRoleId(sourceRoleId);
        sourceRole.setRoleName("源角色");
        
        // 创建新角色并设置ID
        Role newRole = new Role();
        newRole.setRoleId(2L); // 确保设置了ID
        
        // 模拟insert方法的行为，设置ID
        doAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setRoleId(2L);
            return 1;
        }).when(roleMapper).insert(any(Role.class));
        
        List<RoleMenu> roleMenus = new ArrayList<>();
        RoleMenu roleMenu = new RoleMenu();
        roleMenu.setRoleId(sourceRoleId);
        roleMenu.setMenuId(1L);
        roleMenus.add(roleMenu);
        
        // 模拟依赖方法的行为
        when(roleMapper.selectById(sourceRoleId)).thenReturn(sourceRole);
        when(roleMenuMapper.selectList(any())).thenReturn(roleMenus);
        
        // 执行测试
        String result = roleService.copyRole(sourceRoleId, dto);
        
        // 验证结果
        assertEquals("2", result);
        verify(roleMapper, times(2)).selectById(sourceRoleId);
        verify(roleMapper).insert(any());
        verify(roleMenuMapper).selectList(any());
    }
} 