package com.example.login.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Captcha {
    private Long captchaId;
    private Long userId;
    private String captchaCode;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;

    public Long getCaptchaId() {
        return captchaId;
    }

    public void setCaptchaId(Long captchaId) {
        this.captchaId = captchaId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
}