package com.systemManager.mapper.ms;

import com.common.domain.dto.systemManager.MenuDTO;
import com.systemManager.entity.Menu;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class MenuMsMapperTest {

    private MenuMsMapper menuMsMapper = Mappers.getMapper(MenuMsMapper.class);

    @Test
    void testDtoToDo() {
        // 创建DTO对象
        MenuDTO dto = new MenuDTO();
        dto.setMenuName("测试菜单");
        dto.setParentId(0L);
        dto.setOrderNum(1);
        dto.setPath("/test");
        dto.setComponent("Test");
        dto.setMenuType("M");
        dto.setVisible(1);
        dto.setPerms("test:menu:list");
        dto.setIcon("test-icon");
        
        // 转换为实体对象
        Menu menu = menuMsMapper.dtoToDo(dto);
        
        // 验证转换结果
        assertNotNull(menu);
        assertEquals("测试菜单", menu.getMenuName());
        assertEquals(0L, menu.getParentId());
        assertEquals(1, menu.getOrderNum());
        assertEquals("/test", menu.getPath());
        assertEquals("Test", menu.getComponent());
        assertEquals("M", menu.getMenuType());
        assertEquals(1, menu.getVisible());
        assertEquals("test:menu:list", menu.getPerms());
        assertEquals("test-icon", menu.getIcon());
    }
} 