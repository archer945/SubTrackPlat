package com.example.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.login.entity.Captcha;
import com.example.login.mapper.CaptchaMapper;
import com.example.login.service.CaptchaService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Resource
    private CaptchaMapper captchaMapper;

    @Override
    public Captcha generateCaptcha(Long userId) {
        String code = UUID.randomUUID().toString().substring(0, 5);
        Captcha captcha = new Captcha();
        captcha.setUserId(userId);
        captcha.setCaptchaCode(code);
        captcha.setCreateTime(LocalDateTime.now());
        captcha.setExpireTime(LocalDateTime.now().plusMinutes(3));
        captchaMapper.insert(captcha);
        return captcha;
    }

    @Override
    public boolean validateCaptcha(Long userId, String code) {
        Captcha latest = captchaMapper.selectOne(new QueryWrapper<Captcha>()
            .eq("user_id", userId)
            .orderByDesc("create_time")
            .last("LIMIT 1"));
        return latest != null && latest.getExpireTime().isAfter(LocalDateTime.now()) &&
               latest.getCaptchaCode().equalsIgnoreCase(code);
    }
}