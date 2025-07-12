package com.dashboard.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InspectTaskTest {

    @Test
    void testInspectTaskGettersAndSetters() {
        // 创建测试对象
        InspectTask task = new InspectTask();
        
        // 设置所有字段值
        Long id = 1L;
        String name = "隧道巡检任务";
        String type = "日常巡检";
        String priority = "高";
        String description = "隧道日常巡检任务";
        Long executorId = 101L;
        Long assistantId = 102L;
        LocalDateTime plannedStart = LocalDateTime.now();
        LocalDateTime plannedEnd = LocalDateTime.now().plusHours(4);
        LocalDateTime actualStart = LocalDateTime.now().plusMinutes(30);
        LocalDateTime actualEnd = LocalDateTime.now().plusHours(3);
        String lineName = "1号线";
        String startPoint = "起始站";
        String endPoint = "终点站";
        String rangeDescription = "1号线全线";
        Double distance = 15.5;
        String status = "进行中";
        Integer completionRate = 75;
        String result = "正常";
        Integer problemsFound = 2;
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime updateTime = LocalDateTime.now();
        
        task.setId(id);
        task.setName(name);
        task.setType(type);
        task.setPriority(priority);
        task.setDescription(description);
        task.setExecutorId(executorId);
        task.setAssistantId(assistantId);
        task.setPlannedStart(plannedStart);
        task.setPlannedEnd(plannedEnd);
        task.setActualStart(actualStart);
        task.setActualEnd(actualEnd);
        task.setLineName(lineName);
        task.setStartPoint(startPoint);
        task.setEndPoint(endPoint);
        task.setRangeDescription(rangeDescription);
        task.setDistance(distance);
        task.setStatus(status);
        task.setCompletionRate(completionRate);
        task.setResult(result);
        task.setProblemsFound(problemsFound);
        task.setCreateTime(createTime);
        task.setUpdateTime(updateTime);
        
        // 验证getter方法
        assertEquals(id, task.getId());
        assertEquals(name, task.getName());
        assertEquals(type, task.getType());
        assertEquals(priority, task.getPriority());
        assertEquals(description, task.getDescription());
        assertEquals(executorId, task.getExecutorId());
        assertEquals(assistantId, task.getAssistantId());
        assertEquals(plannedStart, task.getPlannedStart());
        assertEquals(plannedEnd, task.getPlannedEnd());
        assertEquals(actualStart, task.getActualStart());
        assertEquals(actualEnd, task.getActualEnd());
        assertEquals(lineName, task.getLineName());
        assertEquals(startPoint, task.getStartPoint());
        assertEquals(endPoint, task.getEndPoint());
        assertEquals(rangeDescription, task.getRangeDescription());
        assertEquals(distance, task.getDistance());
        assertEquals(status, task.getStatus());
        assertEquals(completionRate, task.getCompletionRate());
        assertEquals(result, task.getResult());
        assertEquals(problemsFound, task.getProblemsFound());
        assertEquals(createTime, task.getCreateTime());
        assertEquals(updateTime, task.getUpdateTime());
    }
    
    @Test
    void testEqualsAndHashCode() {
        // 测试equals和hashCode方法
        InspectTask task1 = new InspectTask();
        task1.setId(1L);
        task1.setName("巡检任务1");
        task1.setType("日常巡检");
        task1.setStatus("进行中");
        task1.setExecutorId(101L);
        task1.setPlannedStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        
        InspectTask task2 = new InspectTask();
        task2.setId(1L);
        task2.setName("巡检任务1");
        task2.setType("日常巡检");
        task2.setStatus("进行中");
        task2.setExecutorId(101L);
        task2.setPlannedStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        
        InspectTask task3 = new InspectTask();
        task3.setId(2L);
        task3.setName("巡检任务2");
        task3.setType("专项巡检");
        task3.setStatus("已完成");
        task3.setExecutorId(102L);
        task3.setPlannedStart(LocalDateTime.of(2025, 1, 2, 10, 0));
        
        // 测试equals方法
        assertEquals(task1, task2);
        assertNotEquals(task1, task3);
        assertNotEquals(task1, null);
        assertNotEquals(task1, new Object());
        
        // 测试自反性
        assertEquals(task1, task1);
        
        // 测试对称性
        assertEquals(task1.equals(task2), task2.equals(task1));
        
        // 测试传递性
        InspectTask task4 = new InspectTask();
        task4.setId(1L);
        task4.setName("巡检任务1");
        task4.setType("日常巡检");
        task4.setStatus("进行中");
        task4.setExecutorId(101L);
        task4.setPlannedStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        
        assertEquals(task1, task4);
        assertEquals(task2, task4);
        
        // 测试hashCode方法
        assertEquals(task1.hashCode(), task2.hashCode());
        assertNotEquals(task1.hashCode(), task3.hashCode());
        
        // 测试一致性
        assertEquals(task1.hashCode(), task1.hashCode());
    }
    
    @Test
    void testToString() {
        // 测试toString方法
        InspectTask task = new InspectTask();
        task.setId(1L);
        task.setName("巡检任务1");
        task.setStatus("进行中");
        task.setExecutorId(101L);
        task.setDistance(15.5);
        task.setCompletionRate(75);
        task.setProblemsFound(2);
        task.setType("日常巡检");
        task.setPriority("高");
        task.setLineName("1号线");
        
        String toStringResult = task.toString();
        
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("name=巡检任务1"));
        assertTrue(toStringResult.contains("status=进行中"));
        assertTrue(toStringResult.contains("executorId=101"));
        assertTrue(toStringResult.contains("distance=15.5"));
        assertTrue(toStringResult.contains("completionRate=75"));
        assertTrue(toStringResult.contains("problemsFound=2"));
        assertTrue(toStringResult.contains("type=日常巡检"));
        assertTrue(toStringResult.contains("priority=高"));
        assertTrue(toStringResult.contains("lineName=1号线"));
    }
    
    @Test
    void testNullValues() {
        // 测试null值处理
        InspectTask task = new InspectTask();
        
        task.setName(null);
        assertNull(task.getName());
        
        task.setDescription(null);
        assertNull(task.getDescription());
        
        task.setType(null);
        assertNull(task.getType());
        
        task.setStatus(null);
        assertNull(task.getStatus());
        
        task.setPlannedStart(null);
        assertNull(task.getPlannedStart());
        
        task.setResult(null);
        assertNull(task.getResult());
    }
    
    @Test
    void testEdgeCases() {
        // 测试边缘情况
        InspectTask task = new InspectTask();
        
        // 测试极大值
        task.setId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, task.getId());
        
        task.setExecutorId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, task.getExecutorId());
        
        // 测试极大值和极小值
        task.setCompletionRate(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, task.getCompletionRate());
        
        task.setCompletionRate(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, task.getCompletionRate());
        
        // 测试0和负值
        task.setProblemsFound(0);
        assertEquals(0, task.getProblemsFound());
        
        task.setProblemsFound(-1);
        assertEquals(-1, task.getProblemsFound());
        
        // 测试Double边界值
        task.setDistance(0.0);
        assertEquals(0.0, task.getDistance());
        
        task.setDistance(-1.5);
        assertEquals(-1.5, task.getDistance());
        
        task.setDistance(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, task.getDistance());
        
        // 测试空字符串
        task.setName("");
        assertEquals("", task.getName());
        
        task.setDescription("");
        assertEquals("", task.getDescription());
    }
} 