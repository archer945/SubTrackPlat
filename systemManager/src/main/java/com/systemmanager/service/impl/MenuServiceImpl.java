package com.systemmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.systemmanager.entity.Menu;
import com.systemmanager.mapper.MenuMapper;
import com.systemmanager.service.IMenuService;
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

}
