package com.systemManager.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
    }

    @Test
    void testGenerateAndValidateToken() {
        // 生成token
        String username = "testuser";
        String token = jwtUtils.generateToken(username);

        // 验证token
        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token));
        assertEquals(username, jwtUtils.getUsernameFromToken(token));
    }

    @Test
    void testGetUserIdFromToken_WhenUserIdIsInteger() throws Exception {
        // 准备测试数据
        String username = "testuser";
        Integer userId = 123;

        // 使用反射调用私有方法生成带有userId的token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        
        Method doGenerateTokenMethod = JwtUtils.class.getDeclaredMethod("doGenerateToken", Map.class, String.class);
        doGenerateTokenMethod.setAccessible(true);
        String token = (String) doGenerateTokenMethod.invoke(jwtUtils, claims, username);

        // 验证结果
        Long retrievedUserId = jwtUtils.getUserIdFromToken(token);
        assertNotNull(retrievedUserId);
        assertEquals(userId.longValue(), retrievedUserId);
    }

    @Test
    void testGetUserIdFromToken_WhenUserIdIsLong() throws Exception {
        // 准备测试数据
        String username = "testuser";
        Long userId = 123L;

        // 使用反射调用私有方法生成带有userId的token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        
        Method doGenerateTokenMethod = JwtUtils.class.getDeclaredMethod("doGenerateToken", Map.class, String.class);
        doGenerateTokenMethod.setAccessible(true);
        String token = (String) doGenerateTokenMethod.invoke(jwtUtils, claims, username);

        // 验证结果
        Long retrievedUserId = jwtUtils.getUserIdFromToken(token);
        assertNotNull(retrievedUserId);
        assertEquals(userId, retrievedUserId);
    }

    @Test
    void testGetUserIdFromToken_WhenUserIdIsNull() throws Exception {
        // 准备测试数据
        String username = "testuser";

        // 使用反射调用私有方法生成不带userId的token
        Map<String, Object> claims = new HashMap<>();
        
        Method doGenerateTokenMethod = JwtUtils.class.getDeclaredMethod("doGenerateToken", Map.class, String.class);
        doGenerateTokenMethod.setAccessible(true);
        String token = (String) doGenerateTokenMethod.invoke(jwtUtils, claims, username);

        // 验证结果
        Long retrievedUserId = jwtUtils.getUserIdFromToken(token);
        assertNull(retrievedUserId);
    }

    @Test
    void testGetExpirationDateFromToken() {
        // 生成token
        String username = "testuser";
        String token = jwtUtils.generateToken(username);

        // 获取过期时间
        Date expirationDate = jwtUtils.getExpirationDateFromToken(token);
        
        // 验证过期时间在将来
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void testGetClaimFromToken() {
        // 生成token
        String username = "testuser";
        String token = jwtUtils.generateToken(username);

        // 获取声明
        String subject = jwtUtils.getClaimFromToken(token, Claims::getSubject);
        
        // 验证结果
        assertEquals(username, subject);
    }

    @Test
    void testValidateToken_WithExpiredToken() throws Exception {
        // 准备测试数据
        String username = "testuser";
        
        // 使用反射设置过期时间为负数，使token立即过期
        Field expirationTimeField = JwtUtils.class.getDeclaredField("EXPIRATION_TIME");
        expirationTimeField.setAccessible(true);
        long originalValue = expirationTimeField.getLong(null);
        expirationTimeField.set(null, -1000); // 设置为负数，使token立即过期
        
        try {
            // 生成已过期的token
            String token = jwtUtils.generateToken(username);
            
            // 验证token
            assertFalse(jwtUtils.validateToken(token));
        } finally {
            // 恢复原始值
            expirationTimeField.set(null, originalValue);
        }
    }

    @Test
    void testValidateToken_WithInvalidToken() {
        // 准备无效的token
        String invalidToken = "invalid.token.string";
        
        // 验证token
        assertFalse(jwtUtils.validateToken(invalidToken));
    }

    @Test
    void testIsTokenExpired() throws Exception {
        // 使用反射调用私有方法
        Method isTokenExpiredMethod = JwtUtils.class.getDeclaredMethod("isTokenExpired", String.class);
        isTokenExpiredMethod.setAccessible(true);
        
        // 准备测试数据
        String username = "testuser";
        String token = jwtUtils.generateToken(username);
        
        // 验证未过期的token
        Boolean isExpired = (Boolean) isTokenExpiredMethod.invoke(jwtUtils, token);
        assertFalse(isExpired);
    }
} 