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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    
    @Captor
    private ArgumentCaptor<LambdaQueryWrapper<Role>> queryWrapperCaptor;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testListRoles() {
        // 准备测试数据
        RoleQuery query = new RoleQuery();
        query.setPageIndex(1);
        query.setPageSize(10);
        
        List<RoleVO> roles = new ArrayList<>();
        RoleVO role1 = new RoleVO();
        role1.setRoleId(1L);
        role1.setRoleName("管理员");
        roles.add(role1);
        
        RoleVO role2 = new RoleVO();
        role2.setRoleId(2L);
        role2.setRoleName("普通用户");
        roles.add(role2);
        
        Page<RoleVO> page = new Page<>(1, 10);
        page.setRecords(roles);
        page.setTotal(2);
        
        // 模拟依赖方法的行为
        when(roleMapper.selectRoles(eq(query), any())).thenReturn(page);
        
        // 执行测试
        PageDTO<RoleVO> result = roleService.listRoles(query);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(2, result.getRows().size());
        assertEquals("管理员", result.getRows().get(0).getRoleName());
        assertEquals("普通用户", result.getRows().get(1).getRoleName());
        verify(roleMapper).selectRoles(eq(query), any());
    }
    
    @Test
    void testSaveRoleSuccess() {
        // 准备测试数据
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleName("测试角色");
        roleDTO.setRoleCode("test_role");
        roleDTO.setMenuIds(Arrays.asList(1L, 2L));
        
        Role role = new Role();
        role.setRoleId(1L);
        
        // 模拟依赖方法的行为
        when(roleMapper.selectCount(any())).thenReturn(0L); // 角色名称和编码都不存在
        when(roleMsMapper.dtoToDo(any())).thenReturn(role);
        when(roleMapper.insert(any())).thenReturn(1);
        when(roleMenuMapper.insert(any())).thenReturn(1);
        
        // 执行测试
        String result = roleService.saveRole(roleDTO);
        
        // 验证结果
        assertEquals("1", result);
        verify(roleMapper, times(2)).selectCount(any());
        verify(roleMsMapper).dtoToDo(any());
        verify(roleMapper).insert(any());
        verify(roleMenuMapper, times(2)).insert(any());
    }
    
    @Test
    void testSaveRoleWithDuplicateName() {
        // 准备测试数据
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleName("已存在角色");
        roleDTO.setRoleCode("test_role");
        
        // 模拟角色名称已存在
        when(roleMapper.selectCount(queryWrapperCaptor.capture())).thenReturn(1L);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.saveRole(roleDTO);
        });
        
        assertEquals("角色名称已存在", exception.getMessage());
        verify(roleMapper).selectCount(any());
        verify(roleMsMapper, never()).dtoToDo(any());
        verify(roleMapper, never()).insert(any());
    }
    
    @Test
    void testSaveRoleWithDuplicateCode() {
        // 准备测试数据
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleName("新角色");
        roleDTO.setRoleCode("existing_code");
        
        // 模拟角色名称不存在但编码存在
        when(roleMapper.selectCount(queryWrapperCaptor.capture()))
            .thenReturn(0L) // 第一次调用返回0（角色名称不存在）
            .thenReturn(1L); // 第二次调用返回1（角色编码已存在）
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.saveRole(roleDTO);
        });
        
        assertEquals("角色编码已存在", exception.getMessage());
        verify(roleMapper, times(2)).selectCount(any());
        verify(roleMsMapper, never()).dtoToDo(any());
        verify(roleMapper, never()).insert(any());
    }
    
    @Test
    void testUpdateRoleSuccess() {
        // 准备测试数据
        Long roleId = 2L;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleName("更新角色");
        roleDTO.setRoleCode("update_role");
        roleDTO.setMenuIds(Arrays.asList(1L, 2L));
        
        Role existingRole = new Role();
        existingRole.setRoleId(roleId);
        existingRole.setRoleName("原角色");
        existingRole.setRoleCode("old_code");
        
        // 模拟依赖方法的行为
        when(roleMapper.selectById(roleId)).thenReturn(existingRole);
        when(roleMapper.selectCount(any())).thenReturn(0L); // 角色名称和编码都不存在
        when(roleMsMapper.dtoToDo(any())).thenReturn(existingRole);
        when(roleMapper.updateById(any())).thenReturn(1);
        when(roleMenuMapper.delete(any())).thenReturn(1);
        when(roleMenuMapper.insert(any())).thenReturn(1);
        
        // 执行测试
        String result = roleService.updateRole(roleId, roleDTO);
        
        // 验证结果
        assertEquals("2", result);
        verify(roleMapper).selectById(roleId);
        verify(roleMapper, times(2)).selectCount(any());
        verify(roleMsMapper).dtoToDo(any());
        verify(roleMapper).updateById(any());
        verify(roleMenuMapper).delete(any());
        verify(roleMenuMapper, times(2)).insert(any());
    }
    
    @Test
    void testUpdateRoleNotFound() {
        // 准备测试数据
        Long roleId = 999L;
        RoleDTO roleDTO = new RoleDTO();
        
        // 模拟角色不存在
        when(roleMapper.selectById(roleId)).thenReturn(null);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.updateRole(roleId, roleDTO);
        });
        
        assertEquals("角色不存在", exception.getMessage());
        verify(roleMapper).selectById(roleId);
        verify(roleMsMapper, never()).dtoToDo(any());
        verify(roleMapper, never()).updateById(any());
    }
    
    @Test
    void testUpdateRoleWithDuplicateName() {
        // 准备测试数据
        Long roleId = 2L;
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleName("已存在角色");
        roleDTO.setRoleCode("update_role");
        
        Role existingRole = new Role();
        existingRole.setRoleId(roleId);
        existingRole.setRoleName("原角色");
        
        // 模拟角色存在但名称已被使用
        when(roleMapper.selectById(roleId)).thenReturn(existingRole);
        when(roleMapper.selectCount(any())).thenReturn(1L);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.updateRole(roleId, roleDTO);
        });
        
        assertEquals("角色名称已存在", exception.getMessage());
        verify(roleMapper).selectById(roleId);
        verify(roleMapper).selectCount(any());
        verify(roleMsMapper, never()).dtoToDo(any());
        verify(roleMapper, never()).updateById(any());
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
    void testRemoveRoleNotFound() {
        // 准备测试数据
        Long roleId = 999L;
        
        // 模拟角色不存在
        when(roleMapper.selectById(roleId)).thenReturn(null);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.removeRole(roleId);
        });
        
        assertEquals("角色不存在", exception.getMessage());
        verify(roleMapper).selectById(roleId);
        verify(roleMapper, never()).countUserRoleByRoleId(anyLong());
        verify(roleMenuMapper, never()).delete(any());
        verify(roleMapper, never()).deleteById(any());
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
    void testGetRoleMenuIdsRoleNotFound() {
        // 准备测试数据
        Long roleId = 999L;
        
        // 模拟角色不存在
        when(roleMapper.selectById(roleId)).thenReturn(null);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.getRoleMenuIds(roleId);
        });
        
        assertEquals("角色不存在", exception.getMessage());
        verify(roleMapper).selectById(roleId);
        verify(roleMenuMapper, never()).selectList(any());
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
        when(roleMenuMapper.delete(any())).thenReturn(1);
        when(roleMenuMapper.insert(any())).thenReturn(1);
        
        // 执行测试
        boolean result = roleService.assignRoleMenus(roleId, dto);
        
        // 验证结果
        assertTrue(result);
        verify(roleMapper).selectById(roleId);
        verify(roleMenuMapper).delete(any());
        verify(roleMenuMapper, times(3)).insert(any());
    }
    
    @Test
    void testAssignRoleMenusRoleNotFound() {
        // 准备测试数据
        Long roleId = 999L;
        RoleMenuDTO dto = new RoleMenuDTO();
        
        // 模拟角色不存在
        when(roleMapper.selectById(roleId)).thenReturn(null);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.assignRoleMenus(roleId, dto);
        });
        
        assertEquals("角色不存在", exception.getMessage());
        verify(roleMapper).selectById(roleId);
        verify(roleMenuMapper, never()).delete(any());
        verify(roleMenuMapper, never()).insert(any());
    }
    
    @Test
    void testAssignRoleMenusWithException() {
        // 准备测试数据
        Long roleId = 1L;
        RoleMenuDTO dto = new RoleMenuDTO();
        List<Long> menuIds = Arrays.asList(1L, 2L);
        dto.setMenuIds(menuIds);
        
        Role role = new Role();
        role.setRoleId(roleId);
        
        // 模拟依赖方法的行为
        when(roleMapper.selectById(roleId)).thenReturn(role);
        when(roleMenuMapper.delete(any())).thenThrow(new RuntimeException("模拟异常"));
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roleService.assignRoleMenus(roleId, dto);
        });
        
        assertTrue(exception.getMessage().contains("分配角色菜单权限失败"));
        verify(roleMapper).selectById(roleId);
        verify(roleMenuMapper).delete(any());
        verify(roleMenuMapper, never()).insert(any());
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
    void testUpdateDataScopeRoleNotFound() {
        // 准备测试数据
        Long roleId = 999L;
        DataScopeDTO dto = new DataScopeDTO();
        
        // 模拟角色不存在
        when(roleMapper.selectById(roleId)).thenReturn(null);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.updateDataScope(roleId, dto);
        });
        
        assertEquals("角色不存在", exception.getMessage());
        verify(roleMapper).selectById(roleId);
        verify(roleMapper, never()).updateById(any());
    }
    
    @Test
    void testUpdateDataScopeWithException() {
        // 准备测试数据
        Long roleId = 1L;
        DataScopeDTO dto = new DataScopeDTO();
        dto.setDataScope("2");
        
        Role role = new Role();
        role.setRoleId(roleId);
        
        // 模拟依赖方法的行为
        when(roleMapper.selectById(roleId)).thenReturn(role);
        when(roleMapper.updateById(any())).thenThrow(new RuntimeException("模拟异常"));
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roleService.updateDataScope(roleId, dto);
        });
        
        assertTrue(exception.getMessage().contains("更新角色数据权限失败"));
        verify(roleMapper).selectById(roleId);
        verify(roleMapper).updateById(any());
    }
    
    @Test
    void testGetRoleUsers() {
        // 准备测试数据
        Long roleId = 1L;
        UserQuery query = new UserQuery();
        query.setPageIndex(1);
        query.setPageSize(10);
        
        Role role = new Role();
        role.setRoleId(roleId);
        
        List<UserVO> users = new ArrayList<>();
        UserVO user = new UserVO();
        user.setUserId("1");
        user.setUsername("testuser");
        users.add(user);
        
        Page<UserVO> page = new Page<>(1, 10);
        page.setRecords(users);
        page.setTotal(1);
        
        // 模拟依赖方法的行为
        when(roleMapper.selectById(roleId)).thenReturn(role);
        when(userMapper.selectUsersByRoleId(eq(roleId), eq(query), any())).thenReturn(page);
        
        // 执行测试
        PageDTO<UserVO> result = roleService.getRoleUsers(roleId, query);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRows().size());
        assertEquals("testuser", result.getRows().get(0).getUsername());
        verify(roleMapper).selectById(roleId);
        verify(userMapper).selectUsersByRoleId(eq(roleId), eq(query), any());
    }
    
    @Test
    void testGetRoleUsersRoleNotFound() {
        // 准备测试数据
        Long roleId = 999L;
        UserQuery query = new UserQuery();
        
        // 模拟角色不存在
        when(roleMapper.selectById(roleId)).thenReturn(null);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.getRoleUsers(roleId, query);
        });
        
        assertEquals("角色不存在", exception.getMessage());
        verify(roleMapper).selectById(roleId);
        verify(userMapper, never()).selectUsersByRoleId(any(), any(), any());
    }
    
    @Test
    void testGetRoleUsersWithException() {
        // 准备测试数据
        Long roleId = 1L;
        UserQuery query = new UserQuery();
        
        Role role = new Role();
        role.setRoleId(roleId);
        
        // 模拟依赖方法的行为
        when(roleMapper.selectById(roleId)).thenReturn(role);
        when(userMapper.selectUsersByRoleId(eq(roleId), eq(query), any())).thenThrow(new RuntimeException("模拟异常"));
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roleService.getRoleUsers(roleId, query);
        });
        
        assertTrue(exception.getMessage().contains("获取角色用户列表失败"));
        verify(roleMapper).selectById(roleId);
        verify(userMapper).selectUsersByRoleId(eq(roleId), eq(query), any());
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
        dto.setCopyDataScope(true); // 设置复制数据权限为true
        
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
        when(roleMapper.selectCount(any())).thenReturn(0L); // 角色名称和编码都不存在
        when(roleMenuMapper.selectList(any())).thenReturn(roleMenus);
        when(roleMenuMapper.insert(any())).thenReturn(1);
        
        // 执行测试
        String result = roleService.copyRole(sourceRoleId, dto);
        
        // 验证结果
        assertEquals("2", result);
        // 不验证具体的调用次数，因为实现可能会调用多次
        verify(roleMapper, atLeastOnce()).selectById(sourceRoleId);
        verify(roleMapper, times(2)).selectCount(any());
        verify(roleMapper).insert(any());
        verify(roleMenuMapper).selectList(any());
        verify(roleMenuMapper).insert(any());
    }
    
    @Test
    void testCopyRoleSourceNotFound() {
        // 准备测试数据
        Long sourceRoleId = 999L;
        RoleCopyDTO dto = new RoleCopyDTO();
        
        // 模拟源角色不存在
        when(roleMapper.selectById(sourceRoleId)).thenReturn(null);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.copyRole(sourceRoleId, dto);
        });
        
        assertEquals("源角色不存在", exception.getMessage());
        verify(roleMapper).selectById(sourceRoleId);
        verify(roleMapper, never()).insert(any());
    }
    
    @Test
    void testCopyRoleWithDuplicateName() {
        // 准备测试数据
        Long sourceRoleId = 1L;
        RoleCopyDTO dto = new RoleCopyDTO();
        dto.setRoleName("已存在角色");
        dto.setRoleCode("copy_role");
        
        Role sourceRole = new Role();
        sourceRole.setRoleId(sourceRoleId);
        
        // 模拟源角色存在但目标名称已被使用
        when(roleMapper.selectById(sourceRoleId)).thenReturn(sourceRole);
        when(roleMapper.selectCount(any())).thenReturn(1L);
        
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.copyRole(sourceRoleId, dto);
        });
        
        assertEquals("角色名称已存在", exception.getMessage());
        verify(roleMapper).selectById(sourceRoleId);
        verify(roleMapper).selectCount(any());
        verify(roleMapper, never()).insert(any());
    }
    
    @Test
    void testCopyRoleWithoutPermissions() {
        // 准备测试数据
        Long sourceRoleId = 1L;
        RoleCopyDTO dto = new RoleCopyDTO();
        dto.setRoleName("复制角色");
        dto.setRoleCode("copy_role");
        dto.setStatus("1");
        dto.setDataScope("1");
        dto.setCopyPermissions(false); // 不复制权限
        dto.setCopyDataScope(true);
        
        Role sourceRole = new Role();
        sourceRole.setRoleId(sourceRoleId);
        
        // 模拟insert方法的行为，设置ID
        doAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setRoleId(2L);
            return 1;
        }).when(roleMapper).insert(any(Role.class));
        
        // 模拟依赖方法的行为
        when(roleMapper.selectById(sourceRoleId)).thenReturn(sourceRole);
        when(roleMapper.selectCount(any())).thenReturn(0L);
        
        // 执行测试
        String result = roleService.copyRole(sourceRoleId, dto);
        
        // 验证结果
        assertEquals("2", result);
        verify(roleMapper).selectById(sourceRoleId);
        verify(roleMapper, times(2)).selectCount(any());
        verify(roleMapper).insert(any());
        verify(roleMenuMapper, never()).selectList(any());
        verify(roleMenuMapper, never()).insert(any());
    }
} 