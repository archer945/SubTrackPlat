package com.systemManager.security.aspect;

import com.systemManager.security.annotation.RequiresPermission;
import com.systemManager.security.exception.NoPermissionException;
import com.systemManager.security.service.PermissionService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 权限检查切面
 */
@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private PermissionService permissionService;

    /**
     * 前置通知，在方法执行前进行权限校验
     *
     * @param joinPoint 切点
     */
    @Before("@annotation(com.systemManager.security.annotation.RequiresPermission)")
    public void checkPermission(JoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取权限注解
        RequiresPermission requiresPermission = method.getAnnotation(RequiresPermission.class);
        if (requiresPermission == null) {
            return;
        }

        // 获取权限标识和逻辑类型
        String[] permissions = requiresPermission.value();
        RequiresPermission.Logical logical = requiresPermission.logical();

        // 校验权限
        boolean hasPermission;
        if (logical == RequiresPermission.Logical.AND) {
            // 必须具有所有权限
            hasPermission = permissionService.hasAllPermissions(permissions);
        } else {
            // 只需具有其中一个权限
            hasPermission = permissionService.hasAnyPermission(permissions);
        }

        // 如果没有权限，抛出异常
        if (!hasPermission) {
            throw new NoPermissionException("当前用户没有足够的权限");
        }
    }
} 