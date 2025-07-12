package com.defectmanager.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusUpdateDTOTest {

    @Test
    void testGetterAndSetter() {
        StatusUpdateDTO dto = new StatusUpdateDTO();
        
        String status = "已确认";
        dto.setStatus(status);
        assertEquals(status, dto.getStatus());
        
        Long operatorId = 100L;
        dto.setOperatorId(operatorId);
        assertEquals(operatorId, dto.getOperatorId());
    }
    
    @Test
    void testEqualsAndHashCode() {
        StatusUpdateDTO dto1 = new StatusUpdateDTO();
        dto1.setStatus("已确认");
        dto1.setOperatorId(100L);
        
        StatusUpdateDTO dto2 = new StatusUpdateDTO();
        dto2.setStatus("已确认");
        dto2.setOperatorId(100L);
        
        StatusUpdateDTO dto3 = new StatusUpdateDTO();
        dto3.setStatus("处理中");
        dto3.setOperatorId(200L);
        
        // 测试equals
        assertEquals(dto1, dto1); // 自反性
        assertEquals(dto1, dto2); // 对称性
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, new Object());
        
        // 测试hashCode
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
    
    @Test
    void testToString() {
        StatusUpdateDTO dto = new StatusUpdateDTO();
        dto.setStatus("已确认");
        dto.setOperatorId(100L);
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("status=已确认"));
        assertTrue(toString.contains("operatorId=100"));
    }
} 