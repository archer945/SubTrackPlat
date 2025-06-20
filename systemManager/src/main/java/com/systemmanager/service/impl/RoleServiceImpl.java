package com.systemmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.systemmanager.entity.Role;
import com.systemmanager.mapper.RoleMapper;
import com.systemmanager.service.IRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色信息表 服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
