package com.taskmanager.model.vo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskExportVOTest {

    @Test
    void testGetterAndSetter() {
        // 创建测试实例
        TaskExportVO exportVO = new TaskExportVO();
        
        // 测试所有属性的getter和setter
        String name = "导出任务";
        exportVO.setName(name);
        assertEquals(name, exportVO.getName());
        
        String type = "巡检";
        exportVO.setType(type);
        assertEquals(type, exportVO.getType());
        
        String priority = "高";
        exportVO.setPriority(priority);
        assertEquals(priority, exportVO.getPriority());
        
        Long executorId = 1L;
        exportVO.setExecutorId(executorId);
        assertEquals(executorId, exportVO.getExecutorId());
        
        String plannedStart = "2023-01-01 08:00:00";
        exportVO.setPlannedStart(plannedStart);
        assertEquals(plannedStart, exportVO.getPlannedStart());
        
        String plannedEnd = "2023-01-02 08:00:00";
        exportVO.setPlannedEnd(plannedEnd);
        assertEquals(plannedEnd, exportVO.getPlannedEnd());
        
        String status = "进行中";
        exportVO.setStatus(status);
        assertEquals(status, exportVO.getStatus());
    }
    
    @Test
    void testEqualsAndHashCode() {
        // 创建完全相同的两个对象
        TaskExportVO vo1 = createTaskExportVO("导出任务", "巡检", 1L);
        TaskExportVO vo2 = createTaskExportVO("导出任务", "巡检", 1L);
        
        // 创建不同名称的对象
        TaskExportVO vo3 = createTaskExportVO("不同任务", "巡检", 1L);
        
        // 创建不同类型的对象
        TaskExportVO vo4 = createTaskExportVO("导出任务", "维修", 1L);
        
        // 创建不同执行人ID的对象
        TaskExportVO vo5 = createTaskExportVO("导出任务", "巡检", 2L);
        
        // 创建executorId为null的对象
        TaskExportVO vo6 = createTaskExportVO("导出任务", "巡检", null);
        TaskExportVO vo7 = createTaskExportVO("导出任务", "巡检", null);
        
        // 自反性
        assertEquals(vo1, vo1);
        
        // 对称性
        assertEquals(vo1, vo2);
        assertEquals(vo2, vo1);
        
        // 传递性
        TaskExportVO vo8 = createTaskExportVO("导出任务", "巡检", 1L);
        assertEquals(vo1, vo2);
        assertEquals(vo2, vo8);
        assertEquals(vo1, vo8);
        
        // 一致性（多次调用结果相同）
        assertEquals(vo1.equals(vo2), vo1.equals(vo2));
        
        // 与null比较
        assertFalse(vo1.equals(null));
        
        // 与不同类型比较
        assertFalse(vo1.equals(new Object()));
        
        // 不同属性的对象不相等
        assertNotEquals(vo1, vo3);
        assertNotEquals(vo1, vo4);
        assertNotEquals(vo1, vo5);
        
        // executorId为null的对象相等性比较
        assertEquals(vo6, vo7);
        assertNotEquals(vo1, vo6);
        
        // hashCode测试
        assertEquals(vo1.hashCode(), vo2.hashCode());
        assertNotEquals(vo1.hashCode(), vo3.hashCode());
        assertNotEquals(vo1.hashCode(), vo4.hashCode());
        assertNotEquals(vo1.hashCode(), vo5.hashCode());
        assertEquals(vo6.hashCode(), vo7.hashCode());
    }
    
    @Test
    void testToString() {
        TaskExportVO vo = new TaskExportVO();
        vo.setName("导出任务");
        vo.setType("巡检");
        
        String toString = vo.toString();
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
        assertTrue(toString.contains("TaskExportVO") || toString.contains("name") || toString.contains("type"));
    }
    
    /**
     * 创建TaskExportVO对象的辅助方法
     */
    private TaskExportVO createTaskExportVO(String name, String type, Long executorId) {
        TaskExportVO vo = new TaskExportVO();
        vo.setName(name);
        vo.setType(type);
        vo.setPriority("高");
        vo.setExecutorId(executorId);
        vo.setPlannedStart("2023-01-01 08:00:00");
        vo.setPlannedEnd("2023-01-02 08:00:00");
        vo.setStatus("进行中");
        return vo;
    }
} 