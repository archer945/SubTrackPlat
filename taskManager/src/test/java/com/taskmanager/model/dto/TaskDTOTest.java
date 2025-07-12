package com.taskmanager.model.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TaskDTOTest {

    @Test
    void testGetterAndSetter() {
        // 创建测试实例
        TaskDTO dto = new TaskDTO();
        
        // 测试所有属性的getter和setter
        String name = "测试任务";
        dto.setName(name);
        assertEquals(name, dto.getName());
        
        String type = "巡检";
        dto.setType(type);
        assertEquals(type, dto.getType());
        
        String priority = "高";
        dto.setPriority(priority);
        assertEquals(priority, dto.getPriority());
        
        String description = "测试描述";
        dto.setDescription(description);
        assertEquals(description, dto.getDescription());
        
        Long executorId = 2L;
        dto.setExecutorId(executorId);
        assertEquals(executorId, dto.getExecutorId());
        
        Long assistantId = 3L;
        dto.setAssistantId(assistantId);
        assertEquals(assistantId, dto.getAssistantId());
        
        LocalDateTime plannedStart = LocalDateTime.now();
        dto.setPlannedStart(plannedStart);
        assertEquals(plannedStart, dto.getPlannedStart());
        
        LocalDateTime plannedEnd = plannedStart.plusDays(1);
        dto.setPlannedEnd(plannedEnd);
        assertEquals(plannedEnd, dto.getPlannedEnd());
        
        LocalDateTime actualStart = plannedStart.plusHours(1);
        dto.setActualStart(actualStart);
        assertEquals(actualStart, dto.getActualStart());
        
        LocalDateTime actualEnd = plannedEnd.minusHours(1);
        dto.setActualEnd(actualEnd);
        assertEquals(actualEnd, dto.getActualEnd());
        
        String lineName = "测试线路";
        dto.setLineName(lineName);
        assertEquals(lineName, dto.getLineName());
        
        String startPoint = "起点";
        dto.setStartPoint(startPoint);
        assertEquals(startPoint, dto.getStartPoint());
        
        String endPoint = "终点";
        dto.setEndPoint(endPoint);
        assertEquals(endPoint, dto.getEndPoint());
        
        String rangeDescription = "范围描述";
        dto.setRangeDescription(rangeDescription);
        assertEquals(rangeDescription, dto.getRangeDescription());
        
        String status = "进行中";
        dto.setStatus(status);
        assertEquals(status, dto.getStatus());
        
        Integer completionRate = 50;
        dto.setCompletionRate(completionRate);
        assertEquals(completionRate, dto.getCompletionRate());
        
        String result = "测试结果";
        dto.setResult(result);
        assertEquals(result, dto.getResult());
        
        Integer problemsFound = 2;
        dto.setProblemsFound(problemsFound);
        assertEquals(problemsFound, dto.getProblemsFound());
        
        Long creatorId = 4L;
        dto.setCreatorId(creatorId);
        assertEquals(creatorId, dto.getCreatorId());
    }
    
    @Test
    void testEqualsAndHashCode() {
        TaskDTO dto1 = new TaskDTO();
        dto1.setName("测试任务");
        dto1.setType("巡检");
        
        TaskDTO dto2 = new TaskDTO();
        dto2.setName("测试任务");
        dto2.setType("巡检");
        
        TaskDTO dto3 = new TaskDTO();
        dto3.setName("不同任务");
        dto3.setType("维修");
        
        // 测试equals方法
        assertEquals(dto1, dto1); // 自反性
        assertEquals(dto1, dto2); // 对称性
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, new Object());
        
        // 测试hashCode方法
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
    
    @Test
    void testToString() {
        TaskDTO dto = new TaskDTO();
        dto.setName("测试任务");
        dto.setType("巡检");
        
        String toString = dto.toString();
        assertTrue(toString.contains("name=测试任务"));
        assertTrue(toString.contains("type=巡检"));
    }
} 