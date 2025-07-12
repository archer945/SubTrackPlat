package com.systemManager.mapper.ms;

import com.common.domain.dto.systemManager.RoleDTO;
import com.systemManager.entity.Role;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class RoleMsMapperTest {

    private RoleMsMapper roleMsMapper = Mappers.getMapper(RoleMsMapper.class);

    @Test
    void testDtoToDo() {
        // 创建DTO对象
        RoleDTO dto = new RoleDTO();
        dto.setRoleName("测试角色");
        dto.setRoleCode("test_role");
        dto.setStatus("1");
        dto.setDescription("测试角色描述");
        dto.setDataScope("1");
        
        // 转换为实体对象
        Role role = roleMsMapper.dtoToDo(dto);
        
        // 验证转换结果
        assertNotNull(role);
        assertEquals("测试角色", role.getRoleName());
        assertEquals("test_role", role.getRoleCode());
        assertEquals(Integer.valueOf(1), role.getStatus());
        assertEquals("测试角色描述", role.getDescription());
        assertEquals("1", role.getDataScope());
    }
} 