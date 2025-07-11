package com.common.domain.vo.systemManager;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志视图对象
 */
@Data
public class LoginLogVO {
    
    /**
     * 日志ID
     */
    private Long logId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 登录状态（1成功 0失败）
     */
    private Integer status;
    
    /**
     * 提示消息
     */
    private String msg;
    
    /**
     * 登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;
} 