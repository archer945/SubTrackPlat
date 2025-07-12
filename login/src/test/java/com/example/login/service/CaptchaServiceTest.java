package com.example.login.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.login.entity.Captcha;
import com.example.login.mapper.CaptchaMapper;
import com.example.login.service.impl.CaptchaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CaptchaServiceTest {

    @Mock
    private CaptchaMapper captchaMapper;

    @InjectMocks
    private CaptchaServiceImpl captchaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateCaptcha() {
        // 模拟依赖方法的行为
        when(captchaMapper.insert(any(Captcha.class))).thenReturn(1);

        // 执行测试
        Captcha result = captchaService.generateCaptcha();

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getCaptchaCode());
        assertEquals(5, result.getCaptchaCode().length());
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getExpireTime());
        assertTrue(result.getExpireTime().isAfter(LocalDateTime.now()));
        
        verify(captchaMapper).insert(any(Captcha.class));
    }

    @Test
    void testValidateCaptchaSuccess() {
        // 准备测试数据
        Long userId = 1L;
        String code = "12345";
        
        Captcha captcha = new Captcha();
        captcha.setUserId(userId);
        captcha.setCaptchaCode(code);
        captcha.setCreateTime(LocalDateTime.now().minusMinutes(1));
        captcha.setExpireTime(LocalDateTime.now().plusMinutes(2));

        // 模拟依赖方法的行为
        when(captchaMapper.selectOne(any(QueryWrapper.class))).thenReturn(captcha);

        // 执行测试
        boolean result = captchaService.validateCaptcha(userId, code);

        // 验证结果
        assertTrue(result);
        verify(captchaMapper).selectOne(any(QueryWrapper.class));
    }

    @Test
    void testValidateCaptchaExpired() {
        // 准备测试数据
        Long userId = 1L;
        String code = "12345";
        
        Captcha captcha = new Captcha();
        captcha.setUserId(userId);
        captcha.setCaptchaCode(code);
        captcha.setCreateTime(LocalDateTime.now().minusMinutes(5));
        captcha.setExpireTime(LocalDateTime.now().minusMinutes(2));

        // 模拟依赖方法的行为
        when(captchaMapper.selectOne(any(QueryWrapper.class))).thenReturn(captcha);

        // 执行测试
        boolean result = captchaService.validateCaptcha(userId, code);

        // 验证结果
        assertFalse(result);
        verify(captchaMapper).selectOne(any(QueryWrapper.class));
    }

    @Test
    void testValidateCaptchaWrongCode() {
        // 准备测试数据
        Long userId = 1L;
        String code = "12345";
        String wrongCode = "54321";
        
        Captcha captcha = new Captcha();
        captcha.setUserId(userId);
        captcha.setCaptchaCode(code);
        captcha.setCreateTime(LocalDateTime.now().minusMinutes(1));
        captcha.setExpireTime(LocalDateTime.now().plusMinutes(2));

        // 模拟依赖方法的行为
        when(captchaMapper.selectOne(any(QueryWrapper.class))).thenReturn(captcha);

        // 执行测试
        boolean result = captchaService.validateCaptcha(userId, wrongCode);

        // 验证结果
        assertFalse(result);
        verify(captchaMapper).selectOne(any(QueryWrapper.class));
    }

    @Test
    void testValidateCaptchaNotFound() {
        // 准备测试数据
        Long userId = 999L;
        String code = "12345";

        // 模拟依赖方法的行为
        when(captchaMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // 执行测试
        boolean result = captchaService.validateCaptcha(userId, code);

        // 验证结果
        assertFalse(result);
        verify(captchaMapper).selectOne(any(QueryWrapper.class));
    }
} 