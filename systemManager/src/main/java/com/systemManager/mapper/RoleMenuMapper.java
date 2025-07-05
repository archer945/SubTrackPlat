package com.systemManager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.systemManager.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 角色菜单关联表 Mapper 接口
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
}
