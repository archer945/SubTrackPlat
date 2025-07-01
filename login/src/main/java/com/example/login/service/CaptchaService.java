package com.example.login.service;

import com.example.login.entity.Captcha;

public interface CaptchaService {
    Captcha generateCaptcha();
    boolean validateCaptcha(Long userId, String code);
}