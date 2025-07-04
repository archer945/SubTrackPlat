package com.systemManager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.MenuDTO;
import com.common.domain.query.systemManager.MenuQuery;
import com.common.domain.vo.systemManager.MenuTreeVO;
import com.common.domain.vo.systemManager.UserMenuVO;
import com.systemManager.entity.Menu;
import com.systemManager.mapper.MenuMapper;
import com.systemManager.mapper.ms.MenuMsMapper;
import com.systemManager.security.exception.NoPermissionException;
import com.systemManager.security.util.SecurityUtils;
import com.systemManager.service.IMenuService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Qualifier("menuMsMapperImpl")
    private MenuMsMapper msMapper;

    @Override
    public PageDTO<MenuTreeVO> listMenu(MenuQuery menuQuery) {
        // 构建查询条件
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<Menu>()
                .eq(menuQuery.getVisible() != null, Menu::getVisible, menuQuery.getVisible())
                .like(StringUtils.hasText(menuQuery.getMenuName()), Menu::getMenuName, menuQuery.getMenuName())
                .orderByAsc(Menu::getParentId, Menu::getOrderNum);

        // 分页查询原始菜单数据
        Page<Menu> page = new Page<>(menuQuery.getPageIndex(), menuQuery.getPageSize());
        page = menuMapper.selectPage(page, queryWrapper);

        // 转换为树形结构
        List<MenuTreeVO> menuTree = buildMenuTree(page.getRecords());

        // 构建分页结果
        PageDTO<MenuTreeVO> pageResult = new PageDTO<>();
        pageResult.setPageIndex(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setTotal(page.getTotal());
        pageResult.setPages(page.getPages());
        pageResult.setRows(menuTree);
        return pageResult;
    }

    @Override
    @Transactional
    public String saveMenu(MenuDTO dto) {
        // 校验菜单数据
        validateMenu(dto);

        // 检查同级菜单名称唯一性
        checkMenuNameUnique(dto.getMenuName(), dto.getParentId());

        // 检查路由唯一性
        if (StringUtils.hasText(dto.getPath())) {
            checkPathUnique(dto.getPath());
        }

        // 转换并保存
        Menu menu = msMapper.dtoToDo(dto);
        if(menuMapper.insert(menu) != 1){
            throw new RuntimeException("添加菜单失败");
        }

        return menu.getMenuId().toString();
    }

    @Override
    @Transactional
    public String updateMenu(Long id, MenuDTO dto) {
        Menu existingMenu = menuMapper.selectById(id);
        if (existingMenu == null) {
            throw new IllegalArgumentException("菜单不存在");
        }
        // 校验菜单数据
        validateMenu(dto);
        // 检查同级菜单名称唯一性（排除自己）
        checkMenuNameUnique(dto.getMenuName(), dto.getParentId(), id);

        // 检查路由唯一性（排除自己）
        if (StringUtils.hasText(dto.getPath()) && !dto.getPath().equals(existingMenu.getPath())) {
            checkPathUnique(dto.getPath());
        }

        // 更新菜单
        Menu menu = msMapper.dtoToDo(dto);
        menu.setMenuId(id);
        if (menuMapper.updateById(menu) == 0) {
            throw new RuntimeException("修改菜单失败");
        }

        return id.toString();
    }

    @Override
    @Transactional
    public String removeMenu(Long id) {
        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("菜单ID不合法");
        }

        // 2. 检查菜单是否存在
        Menu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new IllegalArgumentException("菜单不存在或已被删除");
        }

        // 3. 检查是否是按钮类型且无父菜单（防御性校验）
        if ("F".equals(menu.getMenuType()) && (menu.getParentId() == null || menu.getParentId() == 0)) {
            throw new IllegalStateException("数据异常：按钮类型菜单必须关联父菜单");
        }

        // 4. 级联删除检查与执行（根据规则"删除父菜单会同时删除子菜单"）
        handleCascadeDelete(id);

        // 5. 检查角色关联
        if (menuMapper.countRoleMenuByMenuId(id) > 0) {
            throw new IllegalArgumentException("菜单已被角色关联，请先解除关联");
        }

        // 6. 执行删除
        if (menuMapper.deleteById(menu) == 0) {
            throw new RuntimeException("删除菜单失败");
        }
        return id.toString();
    }

    /**
     * 级联删除所有子菜单
     */
    private void handleCascadeDelete(Long parentId) {
        // 查找所有子菜单（按创建时间排序，先删子孙再删父）
        List<Menu> children = menuMapper.selectList(
                new LambdaQueryWrapper<Menu>()
                        .eq(Menu::getParentId, parentId)
                        .orderByAsc(Menu::getCreateTime)
        );

        // 递归删除子菜单
        children.forEach(child -> {
            // 先检查角色关联
            if (menuMapper.countRoleMenuByMenuId(child.getMenuId()) > 0) {
                throw new IllegalArgumentException(
                        String.format("子菜单【%s】已被角色关联，无法级联删除", child.getMenuName())
                );
            }
            // 递归处理子菜单的子菜单
            handleCascadeDelete(child.getMenuId());
            // 删除当前子菜单
            menuMapper.deleteById(child.getMenuId());
        });
    }

    // 构建菜单树
    private List<MenuTreeVO> buildMenuTree(List<Menu> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }

        // 按父ID分组
        Map<Long, List<Menu>> menuMap = menus.stream()
                .collect(Collectors.groupingBy(Menu::getParentId));

        // 构建树形结构
        return buildTreeNodes(menuMap, 0L);
    }

    private List<MenuTreeVO> buildTreeNodes(Map<Long, List<Menu>> menuMap, Long parentId) {
        List<Menu> children = menuMap.getOrDefault(parentId, Collections.emptyList());
        if (children.isEmpty()) {
            return Collections.emptyList();
        }

        return children.stream()
                .sorted(Comparator.comparing(Menu::getOrderNum))
                .map(menu -> {
                    MenuTreeVO node = new MenuTreeVO();
                    BeanUtils.copyProperties(menu, node);
                    node.setChildren(buildTreeNodes(menuMap, menu.getMenuId()));
                    return node;
                })
                .collect(Collectors.toList());
    }

    // 校验菜单规则
    private void validateMenu(MenuDTO dto) {
        if ("F".equals(dto.getMenuType())) {
            if (dto.getParentId() == null || dto.getParentId() == 0) {
                throw new IllegalArgumentException("按钮必须绑定到菜单或目录");
            }
            if (!StringUtils.hasText(dto.getPerms())) {
                throw new IllegalArgumentException("按钮权限标识不能为空");
            }
            if (StringUtils.hasText(dto.getPath())) {
                throw new IllegalArgumentException("按钮类型不能设置路由地址");
            }
            if (StringUtils.hasText(dto.getComponent())) {
                throw new IllegalArgumentException("按钮类型不能设置组件路径");
            }
        } else {
            if (!StringUtils.hasText(dto.getPath())) {
                throw new IllegalArgumentException("路由地址不能为空");
            }
            if ("C".equals(dto.getMenuType()) && !StringUtils.hasText(dto.getComponent())) {
                throw new IllegalArgumentException("菜单类型必须设置组件路径");
            }
        }
    }

    // 检查同级菜单名称唯一性
    private void checkMenuNameUnique(String menuName, Long parentId) {
        checkMenuNameUnique(menuName, parentId, null);
    }

    private void checkMenuNameUnique(String menuName, Long parentId, Long excludeId) {
        Long count = menuMapper.selectCount(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getMenuName, menuName)
                .eq(Menu::getParentId, parentId)
                .ne(excludeId != null, Menu::getMenuId, excludeId));
        if (count > 0) {
            throw new IllegalArgumentException("同级菜单名称已存在");
        }
    }

    // 检查路由唯一性
    private void checkPathUnique(String path) {
        Long count = menuMapper.selectCount(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getPath, path));
        if (count > 0) {
            throw new IllegalArgumentException("路由地址已存在");
        }
    }

    @Override
    public List<UserMenuVO> getCurrentUserMenus() {
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new NoPermissionException("用户未登录");
        }
        return getUserMenus(userId);
    }

    @Override
    public List<UserMenuVO> getUserMenus(Long userId) {
        // 判断是否为管理员
        boolean isAdmin = userId != null && userId == 11L; // 假设ID为1的用户是管理员
        
        // 获取菜单列表
        List<Menu> menus;
        if (isAdmin) {
            // 管理员可以查看所有菜单
            menus = menuMapper.selectList(
                    new LambdaQueryWrapper<Menu>()
                            .eq(Menu::getVisible, 1)  // 只查询可见的菜单
                            .orderByAsc(Menu::getParentId, Menu::getOrderNum)
            );
        } else {
            // 普通用户根据角色查询菜单
            menus = menuMapper.selectMenusByUserId(userId);
        }
        
        // 构建菜单树
        return buildUserMenuTree(menus);
    }

    /**
     * 构建用户菜单树
     */
    private List<UserMenuVO> buildUserMenuTree(List<Menu> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }
        
        // 按父ID分组
        Map<Long, List<Menu>> menuMap = menus.stream()
                .collect(Collectors.groupingBy(Menu::getParentId));
        
        // 构建树形结构
        return buildUserMenuTreeNodes(menuMap, 0L);
    }

    /**
     * 构建用户菜单树节点
     */
    private List<UserMenuVO> buildUserMenuTreeNodes(Map<Long, List<Menu>> menuMap, Long parentId) {
        List<Menu> children = menuMap.getOrDefault(parentId, Collections.emptyList());
        if (children.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 转换并排序
        return children.stream()
                .map(menu -> {
                    UserMenuVO vo = new UserMenuVO();
                    vo.setMenuId(menu.getMenuId());
                    vo.setMenuName(menu.getMenuName());
                    vo.setParentId(menu.getParentId());
                    vo.setPath(menu.getPath());
                    vo.setComponent(menu.getComponent());
                    vo.setIcon(menu.getIcon());
                    vo.setMenuType(menu.getMenuType());
                    vo.setPerms(menu.getPerms());
                    
                    // 递归设置子菜单
                    vo.setChildren(buildUserMenuTreeNodes(menuMap, menu.getMenuId()));
                    return vo;
                })
                .sorted(Comparator.comparing(UserMenuVO::getMenuId))
                .collect(Collectors.toList());
    }
}
