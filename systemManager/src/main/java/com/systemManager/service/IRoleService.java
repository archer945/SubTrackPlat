package com.systemManager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.RoleDTO;
import com.common.domain.query.systemManager.RoleQuery;
import com.common.domain.vo.systemManager.RoleVO;
import com.systemManager.entity.Role;
import jakarta.validation.Valid;

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
}
