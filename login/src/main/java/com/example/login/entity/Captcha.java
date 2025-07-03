package com.example.login.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class Captcha {

    private Long captchaId;
    private Long userId;
    private String captchaCode;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;

}