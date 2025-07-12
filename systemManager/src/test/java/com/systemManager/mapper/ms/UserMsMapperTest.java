package com.systemManager.mapper.ms;

import com.common.domain.dto.systemManager.AddUserDTO;
import com.common.domain.dto.systemManager.UpdateUserDTO;
import com.systemManager.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class UserMsMapperTest {

    private UserMsMapper userMsMapper = Mappers.getMapper(UserMsMapper.class);

    @Test
    void testAddDtoToDo() {
        // 创建AddUserDTO对象
        AddUserDTO dto = new AddUserDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setRealName("测试用户");
        dto.setEmail("test@example.com");
        dto.setTel("13800138000");
        dto.setDeptId(1L);
        dto.setRemark("测试备注");
        
        // 转换为实体对象
        User user = userMsMapper.addDtoToDo(dto);
        
        // 验证转换结果
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("测试用户", user.getRealName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("13800138000", user.getTel());
        assertEquals(1L, user.getDeptId());
        assertEquals("测试备注", user.getRemark());
    }

    @Test
    void testDtoToDo() {
        // 创建UpdateUserDTO对象
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setRealName("更新用户");
        dto.setEmail("update@example.com");
        dto.setTel("13900139000");
        dto.setDeptId(2L);
        dto.setStatus("1");
        
        // 转换为实体对象
        User user = userMsMapper.dtoToDo(dto);
        
        // 验证转换结果
        assertNotNull(user);
        assertEquals("更新用户", user.getRealName());
        assertEquals("update@example.com", user.getEmail());
        assertEquals("13900139000", user.getTel());
        assertEquals(2L, user.getDeptId());
        assertEquals(1, user.getStatus());
    }
} 