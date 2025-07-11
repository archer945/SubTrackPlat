package com.systemManager.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.systemManager.entity.LoginLog;
import com.systemManager.mapper.LoginLogMapper;
import com.systemManager.service.LoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 登录日志表 服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2025-06-30
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;

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
    @Override
    public IPage<LoginLog> getLoginLogPage(Page<LoginLog> page, Long userId, Integer status, String startTime, String endTime) {
        return loginLogMapper.selectLoginLogPage(page, userId, status, startTime, endTime);
    }
} 