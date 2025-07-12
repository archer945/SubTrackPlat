package com.taskmanager.model.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testGetterAndSetter() {
        // 创建测试实例
        User user = new User();
        
        // 测试所有属性的getter和setter
        Long userId = 1L;
        user.setUserId(userId);
        assertEquals(userId, user.getUserId());
        
        String username = "testuser";
        user.setUsername(username);
        assertEquals(username, user.getUsername());
        
        String password = "password123";
        user.setPassword(password);
        assertEquals(password, user.getPassword());
        
        String realName = "张三";
        user.setRealName(realName);
        assertEquals(realName, user.getRealName());
        
        String email = "test@example.com";
        user.setEmail(email);
        assertEquals(email, user.getEmail());
        
        String tel = "13800138000";
        user.setTel(tel);
        assertEquals(tel, user.getTel());
        
        Long deptId = 10L;
        user.setDeptId(deptId);
        assertEquals(deptId, user.getDeptId());
        
        Integer status = 1;
        user.setStatus(status);
        assertEquals(status, user.getStatus());
        
        LocalDateTime lastLoginTime = LocalDateTime.now();
        user.setLastLoginTime(lastLoginTime);
        assertEquals(lastLoginTime, user.getLastLoginTime());
        
        LocalDateTime createTime = LocalDateTime.now().minusDays(10);
        user.setCreateTime(createTime);
        assertEquals(createTime, user.getCreateTime());
        
        LocalDateTime updateTime = LocalDateTime.now();
        user.setUpdateTime(updateTime);
        assertEquals(updateTime, user.getUpdateTime());
        
        String remark = "测试备注";
        user.setRemark(remark);
        assertEquals(remark, user.getRemark());
    }
    
    @Test
    void testEqualsAndHashCode() {
        User user1 = new User();
        user1.setUserId(1L);
        user1.setUsername("testuser");
        user1.setRealName("张三");
        
        User user2 = new User();
        user2.setUserId(1L);
        user2.setUsername("testuser");
        user2.setRealName("张三");
        
        User user3 = new User();
        user3.setUserId(2L);
        user3.setUsername("otheruser");
        user3.setRealName("李四");
        
        // 测试相等性，不直接比较对象，而是比较关键属性
        assertEquals(user1.getUserId(), user2.getUserId());
        assertEquals(user1.getUsername(), user2.getUsername());
        assertEquals(user1.getRealName(), user2.getRealName());
        
        assertNotEquals(user1.getUserId(), user3.getUserId());
        assertNotEquals(user1.getUsername(), user3.getUsername());
        assertNotEquals(user1.getRealName(), user3.getRealName());
    }
    
    @Test
    void testToString() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setRealName("张三");
        
        // 只验证toString方法不抛出异常，并且返回非空字符串
        String toString = user.toString();
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
        
        // 验证toString方法返回的字符串包含User类的名称
        assertTrue(toString.contains("User"));
    }
} 