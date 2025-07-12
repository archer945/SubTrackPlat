package com.example.login.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CaptchaTest {
    @Test
    void testGettersAndSetters() {
        Captcha captcha = new Captcha();
        Long captchaId = 1L;
        Long userId = 2L;
        String captchaCode = "abc123";
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(5);

        captcha.setCaptchaId(captchaId);
        captcha.setUserId(userId);
        captcha.setCaptchaCode(captchaCode);
        captcha.setCreateTime(createTime);
        captcha.setExpireTime(expireTime);

        assertEquals(captchaId, captcha.getCaptchaId());
        assertEquals(userId, captcha.getUserId());
        assertEquals(captchaCode, captcha.getCaptchaCode());
        assertEquals(createTime, captcha.getCreateTime());
        assertEquals(expireTime, captcha.getExpireTime());
    }

    @Test
    void testEqualsAndHashCode() {
        Captcha c1 = new Captcha();
        c1.setCaptchaId(1L);
        c1.setCaptchaCode("abc");
        Captcha c2 = new Captcha();
        c2.setCaptchaId(1L);
        c2.setCaptchaCode("abc");
        Captcha c3 = new Captcha();
        c3.setCaptchaId(2L);
        c3.setCaptchaCode("def");
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertNotEquals(c1, null);
        assertNotEquals(c1, new Object());
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1.hashCode(), c3.hashCode());
    }

    @Test
    void testToString() {
        Captcha captcha = new Captcha();
        captcha.setCaptchaId(1L);
        captcha.setCaptchaCode("abc");
        String str = captcha.toString();
        assertTrue(str.contains("captchaId=1"));
        assertTrue(str.contains("captchaCode=abc"));
    }

    @Test
    void testNullAndEdgeCases() {
        Captcha captcha = new Captcha();
        captcha.setCaptchaCode(null);
        assertNull(captcha.getCaptchaCode());
        captcha.setUserId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, captcha.getUserId());
        captcha.setCaptchaId(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, captcha.getCaptchaId());
    }
} 