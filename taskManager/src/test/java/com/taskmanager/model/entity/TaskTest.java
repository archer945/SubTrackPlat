package com.taskmanager.model.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testGetterAndSetter() {
        // 创建测试实例
        Task task = new Task();
        
        // 测试所有属性的getter和setter
        Long id = 1L;
        task.setId(id);
        assertEquals(id, task.getId());
        
        String name = "测试任务";
        task.setName(name);
        assertEquals(name, task.getName());
        
        String type = "巡检";
        task.setType(type);
        assertEquals(type, task.getType());
        
        String priority = "高";
        task.setPriority(priority);
        assertEquals(priority, task.getPriority());
        
        String description = "测试描述";
        task.setDescription(description);
        assertEquals(description, task.getDescription());
        
        Long executorId = 2L;
        task.setExecutorId(executorId);
        assertEquals(executorId, task.getExecutorId());
        
        Long assistantId = 3L;
        task.setAssistantId(assistantId);
        assertEquals(assistantId, task.getAssistantId());
        
        LocalDateTime plannedStart = LocalDateTime.now();
        task.setPlannedStart(plannedStart);
        assertEquals(plannedStart, task.getPlannedStart());
        
        LocalDateTime plannedEnd = plannedStart.plusDays(1);
        task.setPlannedEnd(plannedEnd);
        assertEquals(plannedEnd, task.getPlannedEnd());
        
        LocalDateTime actualStart = plannedStart.plusHours(1);
        task.setActualStart(actualStart);
        assertEquals(actualStart, task.getActualStart());
        
        LocalDateTime actualEnd = plannedEnd.minusHours(1);
        task.setActualEnd(actualEnd);
        assertEquals(actualEnd, task.getActualEnd());
        
        String lineName = "测试线路";
        task.setLineName(lineName);
        assertEquals(lineName, task.getLineName());
        
        String startPoint = "起点";
        task.setStartPoint(startPoint);
        assertEquals(startPoint, task.getStartPoint());
        
        String endPoint = "终点";
        task.setEndPoint(endPoint);
        assertEquals(endPoint, task.getEndPoint());
        
        String rangeDescription = "范围描述";
        task.setRangeDescription(rangeDescription);
        assertEquals(rangeDescription, task.getRangeDescription());
        
        String status = "进行中";
        task.setStatus(status);
        assertEquals(status, task.getStatus());
        
        Integer completionRate = 50;
        task.setCompletionRate(completionRate);
        assertEquals(completionRate, task.getCompletionRate());
        
        String result = "测试结果";
        task.setResult(result);
        assertEquals(result, task.getResult());
        
        Integer problemsFound = 2;
        task.setProblemsFound(problemsFound);
        assertEquals(problemsFound, task.getProblemsFound());
        
        LocalDateTime createTime = LocalDateTime.now().minusDays(1);
        task.setCreateTime(createTime);
        assertEquals(createTime, task.getCreateTime());
        
        LocalDateTime updateTime = LocalDateTime.now();
        task.setUpdateTime(updateTime);
        assertEquals(updateTime, task.getUpdateTime());
        
        Long creatorId = 4L;
        task.setCreatorId(creatorId);
        assertEquals(creatorId, task.getCreatorId());
    }
    
    @Test
    void testEqualsAndHashCode() {
        Task task1 = new Task();
        task1.setId(1L);
        task1.setName("测试任务");
        
        Task task2 = new Task();
        task2.setId(1L);
        task2.setName("测试任务");
        
        Task task3 = new Task();
        task3.setId(2L);
        task3.setName("不同任务");
        
        // 测试equals方法
        assertEquals(task1, task1); // 自反性
        assertEquals(task1, task2); // 对称性
        assertNotEquals(task1, task3);
        assertNotEquals(task1, null);
        assertNotEquals(task1, new Object());
        
        // 测试hashCode方法
        assertEquals(task1.hashCode(), task2.hashCode());
    }
    
    @Test
    void testToString() {
        Task task = new Task();
        task.setId(1L);
        task.setName("测试任务");
        
        String toString = task.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=测试任务"));
    }
} 