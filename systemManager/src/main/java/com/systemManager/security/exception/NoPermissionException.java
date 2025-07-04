package com.systemManager.security.exception;

/**
 * 无权限异常
 */
public class NoPermissionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoPermissionException() {
        super("没有权限执行此操作");
    }

    public NoPermissionException(String message) {
        super(message);
    }
} 