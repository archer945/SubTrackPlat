package com.systemManager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.DataScopeDTO;
import com.common.domain.dto.systemManager.RoleDTO;
import com.common.domain.dto.systemManager.RoleMenuDTO;
import com.common.domain.query.systemManager.RoleQuery;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.systemManager.RoleVO;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.entity.Role;
import jakarta.validation.Valid;

import java.util.List;

/**
 * <p>
 * 角色信息表 服务类
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
public interface IRoleService extends IService<Role> {

    PageDTO<RoleVO> listRoles(RoleQuery query);

    String saveRole(@Valid RoleDTO dto);

    String updateRole(Long id, @Valid RoleDTO dto);

    String removeRole(Long id);
    
    /**
     * 获取角色菜单权限ID列表
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getRoleMenuIds(Long roleId);
    
    /**
     * 分配角色菜单权限
     * @param roleId 角色ID
     * @param dto 菜单权限DTO
     * @return 是否成功
     */
    boolean assignRoleMenus(Long roleId, RoleMenuDTO dto);
    
    /**
     * 更新角色数据权限
     * @param roleId 角色ID
     * @param dto 数据权限DTO
     * @return 是否成功
     */
    boolean updateDataScope(Long roleId, DataScopeDTO dto);
    
    /**
     * 获取角色关联的用户列表
     * @param roleId 角色ID
     * @param query 查询条件
     * @return 用户列表（分页）
     */
    PageDTO<UserVO> getRoleUsers(Long roleId, UserQuery query);
}
