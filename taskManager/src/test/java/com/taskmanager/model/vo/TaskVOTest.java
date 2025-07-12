package com.taskmanager.model.vo;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TaskVOTest {

    @Test
    void testGetterAndSetter() {
        // 创建测试实例
        TaskVO taskVO = new TaskVO();
        
        // 测试所有属性的getter和setter
        Long id = 1L;
        taskVO.setId(id);
        assertEquals(id, taskVO.getId());
        
        String name = "测试任务";
        taskVO.setName(name);
        assertEquals(name, taskVO.getName());
        
        String type = "巡检";
        taskVO.setType(type);
        assertEquals(type, taskVO.getType());
        
        String priority = "高";
        taskVO.setPriority(priority);
        assertEquals(priority, taskVO.getPriority());
        
        String description = "测试描述";
        taskVO.setDescription(description);
        assertEquals(description, taskVO.getDescription());
        
        Long executorId = 2L;
        taskVO.setExecutorId(executorId);
        assertEquals(executorId, taskVO.getExecutorId());
        
        Long assistantId = 3L;
        taskVO.setAssistantId(assistantId);
        assertEquals(assistantId, taskVO.getAssistantId());
        
        Long creatorId = 4L;
        taskVO.setCreatorId(creatorId);
        assertEquals(creatorId, taskVO.getCreatorId());
        
        LocalDateTime plannedStart = LocalDateTime.now();
        taskVO.setPlannedStart(plannedStart);
        assertEquals(plannedStart, taskVO.getPlannedStart());
        
        LocalDateTime plannedEnd = plannedStart.plusDays(1);
        taskVO.setPlannedEnd(plannedEnd);
        assertEquals(plannedEnd, taskVO.getPlannedEnd());
        
        LocalDateTime actualStart = plannedStart.plusHours(1);
        taskVO.setActualStart(actualStart);
        assertEquals(actualStart, taskVO.getActualStart());
        
        LocalDateTime actualEnd = plannedEnd.minusHours(1);
        taskVO.setActualEnd(actualEnd);
        assertEquals(actualEnd, taskVO.getActualEnd());
        
        String lineName = "测试线路";
        taskVO.setLineName(lineName);
        assertEquals(lineName, taskVO.getLineName());
        
        String startPoint = "起点";
        taskVO.setStartPoint(startPoint);
        assertEquals(startPoint, taskVO.getStartPoint());
        
        String endPoint = "终点";
        taskVO.setEndPoint(endPoint);
        assertEquals(endPoint, taskVO.getEndPoint());
        
        String rangeDescription = "范围描述";
        taskVO.setRangeDescription(rangeDescription);
        assertEquals(rangeDescription, taskVO.getRangeDescription());
        
        String status = "进行中";
        taskVO.setStatus(status);
        assertEquals(status, taskVO.getStatus());
        
        Integer completionRate = 50;
        taskVO.setCompletionRate(completionRate);
        assertEquals(completionRate, taskVO.getCompletionRate());
        
        String result = "测试结果";
        taskVO.setResult(result);
        assertEquals(result, taskVO.getResult());
        
        Integer problemsFound = 2;
        taskVO.setProblemsFound(problemsFound);
        assertEquals(problemsFound, taskVO.getProblemsFound());
        
        LocalDateTime createTime = LocalDateTime.now().minusDays(1);
        taskVO.setCreateTime(createTime);
        assertEquals(createTime, taskVO.getCreateTime());
        
        LocalDateTime updateTime = LocalDateTime.now();
        taskVO.setUpdateTime(updateTime);
        assertEquals(updateTime, taskVO.getUpdateTime());
        
        String creatorName = "创建者";
        taskVO.setCreatorName(creatorName);
        assertEquals(creatorName, taskVO.getCreatorName());
        
        String executorName = "执行者";
        taskVO.setExecutorName(executorName);
        assertEquals(executorName, taskVO.getExecutorName());
        
        String assistantName = "协助者";
        taskVO.setAssistantName(assistantName);
        assertEquals(assistantName, taskVO.getAssistantName());
    }
    
    @Test
    void testEqualsAndHashCode() {
        // 创建完全相同的两个对象
        TaskVO vo1 = createTaskVO(1L, "测试任务");
        TaskVO vo2 = createTaskVO(1L, "测试任务");
        
        // 创建不同ID的对象
        TaskVO vo3 = createTaskVO(2L, "测试任务");
        
        // 创建不同名称的对象
        TaskVO vo4 = createTaskVO(1L, "不同任务");
        
        // 创建ID为null的对象
        TaskVO vo5 = createTaskVO(null, "测试任务");
        TaskVO vo6 = createTaskVO(null, "测试任务");
        
        // 自反性
        assertEquals(vo1, vo1);
        
        // 对称性
        assertEquals(vo1, vo2);
        assertEquals(vo2, vo1);
        
        // 传递性
        TaskVO vo7 = createTaskVO(1L, "测试任务");
        assertEquals(vo1, vo2);
        assertEquals(vo2, vo7);
        assertEquals(vo1, vo7);
        
        // 一致性（多次调用结果相同）
        assertEquals(vo1.equals(vo2), vo1.equals(vo2));
        
        // 与null比较
        assertFalse(vo1.equals(null));
        
        // 与不同类型比较
        assertFalse(vo1.equals(new Object()));
        
        // 不同ID的对象不相等
        assertNotEquals(vo1, vo3);
        
        // 不同名称的对象不相等
        assertNotEquals(vo1, vo4);
        
        // ID为null的对象相等性比较
        assertEquals(vo5, vo6);
        assertNotEquals(vo1, vo5);
        
        // hashCode测试
        assertEquals(vo1.hashCode(), vo2.hashCode());
        assertNotEquals(vo1.hashCode(), vo3.hashCode());
        assertEquals(vo5.hashCode(), vo6.hashCode());
    }
    
    @Test
    void testToString() {
        TaskVO vo = new TaskVO();
        vo.setId(1L);
        vo.setName("测试任务");
        
        String toString = vo.toString();
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
        assertTrue(toString.contains("TaskVO"));
    }
    
    /**
     * 创建TaskVO对象的辅助方法
     */
    private TaskVO createTaskVO(Long id, String name) {
        TaskVO vo = new TaskVO();
        vo.setId(id);
        vo.setName(name);
        vo.setType("巡检");
        vo.setPriority("高");
        vo.setDescription("测试描述");
        vo.setExecutorId(2L);
        vo.setAssistantId(3L);
        vo.setCreatorId(4L);
        vo.setStatus("进行中");
        return vo;
    }
} 