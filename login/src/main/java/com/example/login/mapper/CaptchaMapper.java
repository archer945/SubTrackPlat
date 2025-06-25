package com.example.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.login.entity.Captcha;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CaptchaMapper extends BaseMapper<Captcha> {
}
