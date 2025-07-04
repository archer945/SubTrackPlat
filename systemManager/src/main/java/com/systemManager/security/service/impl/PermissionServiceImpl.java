package com.systemManager.security.service.impl;

import com.systemManager.entity.Menu;
import com.systemManager.entity.User;
import com.systemManager.mapper.MenuMapper;
import com.systemManager.security.service.PermissionService;
import com.systemManager.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private MenuMapper menuMapper;

    /**
     * 获取当前用户的权限列表
     */
    @Override
    public Set<String> getUserPermissions() {
        User user = SecurityUtils.getCurrentUser();
        if (user == null) {
            return new HashSet<>();
        }
        
        // 如果是超级管理员，返回所有权限
        if (SecurityUtils.isAdmin(user)) {
            return getAllPermissions();
        }
        
        // 根据用户ID查询权限
        return menuMapper.selectPermsByUserId(user.getUserId());
    }

    /**
     * 判断当前用户是否具有指定权限
     */
    @Override
    public boolean hasPermission(String permission) {
        // 如果权限标识为空，则视为无权限
        if (permission == null || permission.isEmpty()) {
            return false;
        }
        
        // 获取用户权限列表
        Set<String> permissions = getUserPermissions();
        
        // 判断是否包含指定权限或通配符权限
        return permissions.contains(permission) || permissions.contains("*:*:*");
    }

    /**
     * 判断当前用户是否具有所有指定权限
     */
    @Override
    public boolean hasAllPermissions(String... permissions) {
        // 如果权限数组为空，则视为有权限
        if (permissions == null || permissions.length == 0) {
            return true;
        }
        
        Set<String> userPermissions = getUserPermissions();
        
        // 如果用户拥有通配符权限，则视为拥有所有权限
        if (userPermissions.contains("*:*:*")) {
            return true;
        }
        
        // 判断是否包含所有指定权限
        for (String permission : permissions) {
            if (!userPermissions.contains(permission)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * 判断当前用户是否具有任意一个指定权限
     */
    @Override
    public boolean hasAnyPermission(String... permissions) {
        // 如果权限数组为空，则视为无权限
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        
        Set<String> userPermissions = getUserPermissions();
        
        // 如果用户拥有通配符权限，则视为拥有所有权限
        if (userPermissions.contains("*:*:*")) {
            return true;
        }
        
        // 判断是否包含任意一个指定权限
        for (String permission : permissions) {
            if (userPermissions.contains(permission)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 获取系统中所有权限
     */
    private Set<String> getAllPermissions() {
        List<Menu> menus = menuMapper.selectAllMenus();
        Set<String> permissions = new HashSet<>();
        
        for (Menu menu : menus) {
            if (menu.getPerms() != null && !menu.getPerms().isEmpty()) {
                permissions.add(menu.getPerms());
            }
        }
        
        return permissions;
    }
} 