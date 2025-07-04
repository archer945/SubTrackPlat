package com.systemManager.security.annotation;

import java.lang.annotation.*;

/**
 * 权限注解，用于标记需要特定权限的方法或类
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    
    /**
     * 权限标识
     */
    String[] value() default {};
    
    /**
     * 权限验证逻辑类型：AND | OR
     */
    Logical logical() default Logical.AND;
    
    /**
     * 逻辑枚举
     */
    enum Logical {
        /**
         * 必须具有所有指定权限
         */
        AND,
        
        /**
         * 只需具有其中一个指定权限
         */
        OR
    }
} 