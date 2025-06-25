package com.systemManager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.systemManager.entity.Menu;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
public interface MenuMapper extends BaseMapper<Menu> {

    int countRoleMenuByMenuId(Long menuId);
}
