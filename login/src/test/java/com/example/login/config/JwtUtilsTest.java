package com.example.login.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
    }

    @Test
    void testGenerateToken() {
        String username = "testuser";
        String token = jwtUtils.generateToken(username);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testGetUsernameFromToken() {
        String username = "testuser";
        String token = jwtUtils.generateToken(username);
        
        String extractedUsername = jwtUtils.getUsernameFromToken(token);
        
        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken_ValidToken() {
        String username = "testuser";
        String token = jwtUtils.generateToken(username);
        
        boolean isValid = jwtUtils.validateToken(token);
        
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalid.token.string";
        
        boolean isValid = jwtUtils.validateToken(invalidToken);
        
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_ExpiredToken() throws Exception {
        // 创建一个已过期的令牌
        // 由于不能直接修改静态 final 字段，我们需要手动创建一个过期的令牌
        String username = "testuser";
        
        // 使用反射创建一个过期的令牌（过期时间设为当前时间之前）
        Method generateTokenMethod = JwtUtils.class.getDeclaredMethod("generateToken", String.class);
        generateTokenMethod.setAccessible(true);
        
        // 获取 SECRET_KEY 字段
        Field secretKeyField = JwtUtils.class.getDeclaredField("SECRET_KEY");
        secretKeyField.setAccessible(true);
        Key secretKey = (Key) secretKeyField.get(null);
        
        // 手动创建一个过期的令牌
        String expiredToken = io.jsonwebtoken.Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000)) // 1小时前
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // 1秒前过期
                .signWith(secretKey)
                .compact();
        
        // 验证令牌
        boolean isValid = jwtUtils.validateToken(expiredToken);
        
        // 应该是无效的
        assertFalse(isValid);
    }
} 