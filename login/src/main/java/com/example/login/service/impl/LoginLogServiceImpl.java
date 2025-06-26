package com.example.login.service.impl;

import com.example.login.entity.LoginLog;
import com.example.login.mapper.LoginLogMapper;
import com.example.login.service.LoginLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogMapper loginLogMapper;

    @Override
    public void record(Long userId, String username, boolean success, String msg) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setStatus(success ? 1 : 0);
        log.setMsg(msg);
        loginLogMapper.insert(log);
    }
}