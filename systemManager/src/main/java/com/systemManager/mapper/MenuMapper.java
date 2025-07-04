package com.systemManager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.systemManager.entity.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据菜单ID统计关联的角色数量
     * 
     * @param menuId 菜单ID
     * @return 关联的角色数量
     */
    int countRoleMenuByMenuId(Long menuId);
    
    /**
     * 查询系统所有菜单
     * 
     * @return 菜单列表
     */
    List<Menu> selectAllMenus();
    
    /**
     * 根据用户ID查询权限标识
     * 
     * @param userId 用户ID
     * @return 权限标识集合
     */
    Set<String> selectPermsByUserId(@Param("userId") Long userId);
    
    /**
     * 根据角色ID查询权限标识
     * 
     * @param roleId 角色ID
     * @return 权限标识集合
     */
    Set<String> selectPermsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<Menu> selectMenusByUserId(@Param("userId") Long userId);
    
    /**
     * 根据角色ID查询菜单
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<Menu> selectMenusByRoleId(@Param("roleId") Long roleId);
}
