package com.systemManager.security.service;

import java.util.Set;

/**
 * 权限服务接口
 */
public interface PermissionService {

    /**
     * 获取当前用户的权限列表
     *
     * @return 权限标识集合
     */
    Set<String> getUserPermissions();

    /**
     * 判断当前用户是否具有指定权限
     *
     * @param permission 权限标识
     * @return true:有权限 false:无权限
     */
    boolean hasPermission(String permission);

    /**
     * 判断当前用户是否具有所有指定权限
     *
     * @param permissions 权限标识数组
     * @return true:有所有权限 false:至少缺少一个权限
     */
    boolean hasAllPermissions(String... permissions);

    /**
     * 判断当前用户是否具有任意一个指定权限
     *
     * @param permissions 权限标识数组
     * @return true:有至少一个权限 false:一个权限都没有
     */
    boolean hasAnyPermission(String... permissions);
} 