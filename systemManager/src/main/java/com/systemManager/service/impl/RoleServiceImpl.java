package com.systemManager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.RoleDTO;
import com.common.domain.query.systemManager.RoleQuery;
import com.common.domain.vo.systemManager.RoleVO;
import com.systemManager.entity.Role;
import com.systemManager.entity.RoleMenu;
import com.systemManager.mapper.RoleMapper;
import com.systemManager.mapper.RoleMenuMapper;
import com.systemManager.mapper.RoleMsMapper;
import com.systemManager.service.IRoleService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色信息表 服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Resource
    private RoleMapper roleMapper;

    @Autowired
    @Qualifier("roleMsMapperImpl")
    private RoleMsMapper msMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    public PageDTO<RoleVO> listRoles(RoleQuery query) {
        // 分页对象
        Page<RoleVO> page = new Page<>(query.getPageIndex(), query.getPageSize());
        // 查询
        Page<RoleVO> rolePage = roleMapper.selectUser(query, page);
        return PageDTO.create(rolePage);
    }

    @Override
    @Transactional
    public String saveRole(RoleDTO dto) {
        // 校验角色名称和编码唯一性
        checkRoleNameUnique(dto.getRoleName());
        checkRoleCodeUnique(dto.getRoleCode());

        // 转换为DO
        Role role = msMapper.dtoToDo(dto);
        role.setStatus(1);
        // 插入
        if(roleMapper.insert(role) != 1){
            throw new RuntimeException("添加角色失败");
        }
        // 插入角色菜单关联
        if (dto.getMenuIds() != null && !dto.getMenuIds().isEmpty()) {
            insertRoleMenu(role.getRoleId(), dto.getMenuIds());
        }

        return role.getRoleId().toString();
    }

    @Override
    @Transactional
    public String updateRole(Long id, RoleDTO dto) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }

        // 校验角色名称和编码唯一性（排除自己）
        checkRoleNameUnique(dto.getRoleName(), id);
        checkRoleCodeUnique(dto.getRoleCode(), id);

        role = msMapper.dtoToDo(dto);
        role.setRoleId(id);
        role.setUpdateTime(LocalDateTime.now());
        if (roleMapper.updateById(role) == 0) {
            throw new RuntimeException("修改角色失败");
        }

        // 更新角色菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, role.getRoleId()));
        if (dto.getMenuIds() != null && !dto.getMenuIds().isEmpty()) {
            insertRoleMenu(role.getRoleId(), dto.getMenuIds());
        }
        return id.toString();
    }

    @Override
    @Transactional
    public String removeRole(Long id) {

        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }

        // 检查是否被用户关联
        int userCount = roleMapper.countUserRoleByRoleId(id);
        if (userCount > 0) {
            throw new IllegalArgumentException(String.format("角色【%s】已分配用户，不能删除", role.getRoleName()));
        }

        // 删除角色菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, id));

        // 删除角色
        if (roleMapper.deleteById(role) == 0) {
            throw new RuntimeException("删除角色失败");
        }

        return id.toString();
    }

    // 校验角色名称唯一性
    private void checkRoleNameUnique(String roleName) {
        checkRoleNameUnique(roleName, null);
    }

    private void checkRoleNameUnique(String roleName, Long excludeId) {
        Long count = roleMapper.selectCount(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleName, roleName)
                .ne(excludeId != null, Role::getRoleId, excludeId));
        if (count > 0) {
            throw new IllegalArgumentException("角色名称已存在");
        }
    }

    // 校验角色编码唯一性
    private void checkRoleCodeUnique(String roleCode) {
        checkRoleCodeUnique(roleCode, null);
    }

    private void checkRoleCodeUnique(String roleCode, Long excludeId) {
        Long count = roleMapper.selectCount(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, roleCode)
                .ne(excludeId != null, Role::getRoleId, excludeId));
        if (count > 0) {
            throw new IllegalArgumentException("角色编码已存在");
        }
    }

    // 插入角色菜单关联
    private void insertRoleMenu(Long roleId, List<Long> menuIds) {
        List<RoleMenu> list = menuIds.stream()
                .map(menuId -> new RoleMenu(roleId, menuId))
                .collect(Collectors.toList());
        roleMenuMapper.insertBatch(list);
    }
}
