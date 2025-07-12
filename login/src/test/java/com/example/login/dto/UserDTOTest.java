package com.example.login.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {
    @Test
    void testGettersAndSetters() {
        UserDTO dto = new UserDTO();
        dto.setUsername("admin");
        dto.setPassword("123456");
        assertEquals("admin", dto.getUsername());
        assertEquals("123456", dto.getPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        UserDTO dto1 = new UserDTO();
        dto1.setUsername("admin");
        dto1.setPassword("123456");
        UserDTO dto2 = new UserDTO();
        dto2.setUsername("admin");
        dto2.setPassword("123456");
        UserDTO dto3 = new UserDTO();
        dto3.setUsername("user");
        dto3.setPassword("654321");
        // 自反性
        assertEquals(dto1, dto1);
        // 对称性
        assertEquals(dto1, dto2);
        assertEquals(dto2, dto1);
        // 传递性
        UserDTO dto4 = new UserDTO();
        dto4.setUsername("admin");
        dto4.setPassword("123456");
        assertEquals(dto1, dto4);
        assertEquals(dto2, dto4);
        // null和不同类型
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, new Object());
        // 不等
        assertNotEquals(dto1, dto3);
        // hashCode一致性
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        // 所有字段都为null
        UserDTO dtoNull1 = new UserDTO();
        UserDTO dtoNull2 = new UserDTO();
        assertEquals(dtoNull1, dtoNull2);
        assertEquals(dtoNull1.hashCode(), dtoNull2.hashCode());
    }

    @Test
    void testToString() {
        UserDTO dto = new UserDTO();
        dto.setUsername("admin");
        dto.setPassword("123456");
        String str = dto.toString();
        assertTrue(str.contains("username=admin"));
        assertTrue(str.contains("password=123456"));
    }

    @Test
    void testNullAndEdgeCases() {
        UserDTO dto = new UserDTO();
        dto.setUsername(null);
        assertNull(dto.getUsername());
        dto.setPassword("");
        assertEquals("", dto.getPassword());
        dto.setPassword(null);
        assertNull(dto.getPassword());
    }
} 