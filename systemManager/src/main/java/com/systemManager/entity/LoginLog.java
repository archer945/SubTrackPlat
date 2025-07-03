package com.systemManager.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 登录日志表
 * </p>
 *
 * @author yuyu
 * @since 2025-06-30
 */
@Getter
@Setter
@TableName(value = "login_log", schema = "sub_track_plat")
public class LoginLog implements Serializable {

    private static final long serialVersionUID = 1L;
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
     * 登录状态 0:失败 1:成功
     */
    private Integer status;
    /**
     * 提示消息
     */
    private String msg;
    /**
     * 登录时间
     */
    private LocalDateTime loginTime;
}
