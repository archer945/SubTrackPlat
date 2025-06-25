package com.systemManager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.MenuDTO;
import com.common.domain.query.systemManager.MenuQuery;
import com.common.domain.vo.systemManager.MenuTreeVO;
import com.systemManager.entity.Menu;

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
}
