package com.systemManager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.AddMenuDTO;
import com.common.domain.dto.systemManager.UpdateMenuDTO;
import com.common.domain.query.systemManager.MenuQuery;
import com.common.domain.vo.systemManager.MenuTreeVO;
import com.systemManager.entity.Menu;
import com.systemManager.mapper.MenuMapper;
import com.systemManager.mapper.MsMenuMapper;
import com.systemManager.service.IMenuService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {
    @Resource
    private MenuMapper menuMapper;

    @Autowired
    @Qualifier("msMenuMapperImpl")
    private MsMenuMapper msMapper;

    @Override
    public PageDTO<MenuTreeVO> listMenu(MenuQuery menuQuery) {
        return null;
    }

    @Override
    public String saveMenu(AddMenuDTO addMenuDTO) {
        return "";
    }

    @Override
    public String updateMenu(Long id, UpdateMenuDTO dto) {
        return "";
    }

    @Override
    public String removeMenu(Long id) {
        return "";
    }

    // 私有方法：校验菜单规则
    private void validateMenu(AddMenuDTO dto) {
        if ("F".equals(dto.getMenuType())) {
            if (dto.getParentId() == null || dto.getParentId() == 0) {
                throw new IllegalArgumentException("按钮必须绑定到菜单或目录");
            }
            if (dto.getPerms() == null) {
                throw new IllegalArgumentException("按钮权限标识不能为空");
            }
        }
    }
}
