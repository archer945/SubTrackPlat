package com.systemManager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.AddMenuDTO;
import com.common.domain.dto.systemManager.UpdateMenuDTO;
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

    String saveMenu(AddMenuDTO addMenuDTO);

    String updateMenu(Long id, UpdateMenuDTO dto);

    String removeMenu(Long id);
}
