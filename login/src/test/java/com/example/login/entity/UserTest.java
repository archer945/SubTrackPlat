package com.example.login.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void testGettersAndSetters() {
        User user = new User();
        Long userId = 1L;
        String username = "admin";
        String password = "123456";
        String realName = "管理员";
        String email = "admin@test.com";
        String tel = "12345678901";
        Long deptId = 2L;
        Integer status = 1;
        LocalDateTime lastLoginTime = LocalDateTime.now();
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime updateTime = LocalDateTime.now();
        String remark = "超级管理员";

        user.setUserId(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setEmail(email);
        user.setTel(tel);
        user.setDeptId(deptId);
        user.setStatus(status);
        user.setLastLoginTime(lastLoginTime);
        user.setCreateTime(createTime);
        user.setUpdateTime(updateTime);
        user.setRemark(remark);

        assertEquals(userId, user.getUserId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(realName, user.getRealName());
        assertEquals(email, user.getEmail());
        assertEquals(tel, user.getTel());
        assertEquals(deptId, user.getDeptId());
        assertEquals(status, user.getStatus());
        assertEquals(lastLoginTime, user.getLastLoginTime());
        assertEquals(createTime, user.getCreateTime());
        assertEquals(updateTime, user.getUpdateTime());
        assertEquals(remark, user.getRemark());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User();
        user1.setUserId(1L);
        user1.setUsername("admin");
        User user2 = new User();
        user2.setUserId(1L);
        user2.setUsername("admin");
        User user3 = new User();
        user3.setUserId(2L);
        user3.setUsername("user");
        // 自反性
        assertEquals(user1, user1);
        // 对称性
        assertEquals(user1, user2);
        assertEquals(user2, user1);
        // 传递性
        User user4 = new User();
        user4.setUserId(1L);
        user4.setUsername("admin");
        assertEquals(user1, user4);
        assertEquals(user2, user4);
        // null和不同类型
        assertNotEquals(user1, null);
        assertNotEquals(user1, new Object());
        // 不等
        assertNotEquals(user1, user3);
        // hashCode一致性
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
        // 所有字段都为null
        User userNull1 = new User();
        User userNull2 = new User();
        assertEquals(userNull1, userNull2);
        assertEquals(userNull1.hashCode(), userNull2.hashCode());
    }

    @Test
    void testToString() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("admin");
        String str = user.toString();
        assertTrue(str.contains("userId=1"));
        assertTrue(str.contains("username=admin"));
    }

    @Test
    void testNullAndEdgeCases() {
        User user = new User();
        user.setUsername(null);
        assertNull(user.getUsername());
        user.setPassword("");
        assertEquals("", user.getPassword());
        user.setDeptId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, user.getDeptId());
        user.setStatus(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, user.getStatus());
        user.setRealName(null);
        user.setEmail(null);
        user.setTel(null);
        user.setLastLoginTime(null);
        user.setCreateTime(null);
        user.setUpdateTime(null);
        user.setRemark(null);
        assertNull(user.getRealName());
        assertNull(user.getEmail());
        assertNull(user.getTel());
        assertNull(user.getLastLoginTime());
        assertNull(user.getCreateTime());
        assertNull(user.getUpdateTime());
        assertNull(user.getRemark());
    }
} 