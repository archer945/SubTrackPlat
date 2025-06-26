package com.systemManager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.domain.query.systemManager.RoleQuery;
import com.common.domain.vo.systemManager.RoleVO;
import com.systemManager.entity.Role;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 角色信息表 Mapper 接口
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
public interface RoleMapper extends BaseMapper<Role> {

    @Select("SELECT COUNT(1) FROM user_role WHERE role_id = #{roleId}")
    int countUserRoleByRoleId(Long roleId);

    Page<RoleVO> selectUser(RoleQuery query, Page<RoleVO> page);
}
