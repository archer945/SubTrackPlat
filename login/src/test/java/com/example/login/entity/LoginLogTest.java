package com.example.login.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoginLogTest {
    @Test
    void testGettersAndSetters() {
        LoginLog log = new LoginLog();
        Long logId = 1L;
        Long userId = 2L;
        String username = "admin";
        Integer status = 1;
        String msg = "登录成功";
        LocalDateTime loginTime = LocalDateTime.now();

        log.setLogId(logId);
        log.setUserId(userId);
        log.setUsername(username);
        log.setStatus(status);
        log.setMsg(msg);
        log.setLoginTime(loginTime);

        assertEquals(logId, log.getLogId());
        assertEquals(userId, log.getUserId());
        assertEquals(username, log.getUsername());
        assertEquals(status, log.getStatus());
        assertEquals(msg, log.getMsg());
        assertEquals(loginTime, log.getLoginTime());
    }

    @Test
    void testEqualsAndHashCode() {
        LoginLog log1 = new LoginLog();
        log1.setLogId(1L);
        log1.setUsername("admin");
        LoginLog log2 = new LoginLog();
        log2.setLogId(1L);
        log2.setUsername("admin");
        LoginLog log3 = new LoginLog();
        log3.setLogId(2L);
        log3.setUsername("user");
        assertEquals(log1, log2);
        assertNotEquals(log1, log3);
        assertNotEquals(log1, null);
        assertNotEquals(log1, new Object());
        assertEquals(log1.hashCode(), log2.hashCode());
        assertNotEquals(log1.hashCode(), log3.hashCode());
    }

    @Test
    void testToString() {
        LoginLog log = new LoginLog();
        log.setLogId(1L);
        log.setUsername("admin");
        String str = log.toString();
        assertTrue(str.contains("logId=1"));
        assertTrue(str.contains("username=admin"));
    }

    @Test
    void testNullAndEdgeCases() {
        LoginLog log = new LoginLog();
        log.setUsername(null);
        assertNull(log.getUsername());
        log.setMsg("");
        assertEquals("", log.getMsg());
        log.setLogId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, log.getLogId());
        log.setStatus(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, log.getStatus());
    }
} 