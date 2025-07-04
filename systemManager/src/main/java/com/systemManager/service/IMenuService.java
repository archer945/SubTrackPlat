package com.systemManager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.MenuDTO;
import com.common.domain.query.systemManager.MenuQuery;
import com.common.domain.vo.systemManager.MenuTreeVO;
import com.common.domain.vo.systemManager.UserMenuVO;
import com.systemManager.entity.Menu;

import java.util.List;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
public interface IMenuService extends IService<Menu> {

    PageDTO<MenuTreeVO> listMenu(MenuQuery menuQuery);

    String saveMenu(MenuDTO dto);

    String updateMenu(Long id, MenuDTO dto);

    String removeMenu(Long id);
    
    /**
     * 获取当前用户的菜单列表
     *
     * @return 菜单树形列表
     */
    List<UserMenuVO> getCurrentUserMenus();
    
    /**
     * 根据用户ID获取菜单列表
     *
     * @param userId 用户ID
     * @return 菜单树形列表
     */
    List<UserMenuVO> getUserMenus(Long userId);
}
