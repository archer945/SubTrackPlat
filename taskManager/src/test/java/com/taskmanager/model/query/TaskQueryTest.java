package com.taskmanager.model.query;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskQueryTest {

    @Test
    void testGetterAndSetter() {
        // 创建测试实例
        TaskQuery query = new TaskQuery();
        
        // 测试默认值
        assertEquals(1, query.getPage());
        assertEquals(10, query.getSize());
        
        // 测试所有属性的getter和setter
        Integer page = 2;
        query.setPage(page);
        assertEquals(page, query.getPage());
        
        Integer size = 20;
        query.setSize(size);
        assertEquals(size, query.getSize());
        
        String name = "测试任务";
        query.setName(name);
        assertEquals(name, query.getName());
        
        String type = "巡检";
        query.setType(type);
        assertEquals(type, query.getType());
        
        String status = "进行中";
        query.setStatus(status);
        assertEquals(status, query.getStatus());
        
        Long executorId = 1L;
        query.setExecutorId(executorId);
        assertEquals(executorId, query.getExecutorId());
        
        Long creatorId = 2L;
        query.setCreatorId(creatorId);
        assertEquals(creatorId, query.getCreatorId());
        
        String creatorName = "张三";
        query.setCreatorName(creatorName);
        assertEquals(creatorName, query.getCreatorName());
        
        String priority = "高";
        query.setPriority(priority);
        assertEquals(priority, query.getPriority());
        
        String createTimeStart = "2023-01-01 00:00:00";
        query.setCreateTimeStart(createTimeStart);
        assertEquals(createTimeStart, query.getCreateTimeStart());
        
        String createTimeEnd = "2023-01-31 23:59:59";
        query.setCreateTimeEnd(createTimeEnd);
        assertEquals(createTimeEnd, query.getCreateTimeEnd());
        
        String plannedStart = "2023-02-01 00:00:00";
        query.setPlannedStart(plannedStart);
        assertEquals(plannedStart, query.getPlannedStart());
        
        String plannedEnd = "2023-02-28 23:59:59";
        query.setPlannedEnd(plannedEnd);
        assertEquals(plannedEnd, query.getPlannedEnd());
    }
    
    @Test
    void testEqualsAndHashCode() {
        TaskQuery query1 = new TaskQuery();
        query1.setName("测试任务");
        query1.setType("巡检");
        
        TaskQuery query2 = new TaskQuery();
        query2.setName("测试任务");
        query2.setType("巡检");
        
        TaskQuery query3 = new TaskQuery();
        query3.setName("不同任务");
        query3.setType("维修");
        
        // 测试equals方法
        assertEquals(query1, query1); // 自反性
        assertEquals(query1, query2); // 对称性
        assertNotEquals(query1, query3);
        assertNotEquals(query1, null);
        assertNotEquals(query1, new Object());
        
        // 测试hashCode方法
        assertEquals(query1.hashCode(), query2.hashCode());
    }
    
    @Test
    void testToString() {
        TaskQuery query = new TaskQuery();
        query.setName("测试任务");
        query.setType("巡检");
        
        String toString = query.toString();
        assertTrue(toString.contains("name=测试任务"));
        assertTrue(toString.contains("type=巡检"));
    }
} 