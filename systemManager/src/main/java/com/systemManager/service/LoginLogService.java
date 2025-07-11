package com.systemManager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.systemManager.entity.LoginLog;

/**
 * <p>
 * 登录日志表 服务接口
 * </p>
 *
 * @author yuyu
 * @since 2025-06-30
 */
public interface LoginLogService extends IService<LoginLog> {

    /**
     * 分页查询登录日志
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @param status 状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页数据
     */
    IPage<LoginLog> getLoginLogPage(Page<LoginLog> page, Long userId, Integer status, String startTime, String endTime);
} 