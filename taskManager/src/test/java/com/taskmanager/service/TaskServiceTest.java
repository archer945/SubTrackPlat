package com.taskmanager.service;

import com.taskmanager.mapper.TaskMapper;
import com.taskmanager.mapper.UserMapper;
import com.taskmanager.model.dto.TaskDTO;
import com.taskmanager.model.entity.Task;
import com.taskmanager.model.entity.User;
import com.taskmanager.model.vo.TaskVO;
import com.taskmanager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskMapper taskMapper;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private TaskServiceImpl taskService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testGetTaskByIdSuccess() {
        // 准备测试数据
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setName("测试任务");
        task.setExecutorId(10L);
        task.setAssistantId(20L);
        task.setCreatorId(30L);
        
        // 模拟依赖方法的行为
        when(taskMapper.selectById(taskId)).thenReturn(task);
        when(userMapper.getName(10L)).thenReturn("执行人");
        when(userMapper.getName(20L)).thenReturn("协助人");
        when(userMapper.getName(30L)).thenReturn("创建人");
        
        // 执行测试
        TaskVO result = taskService.getTaskById(taskId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("测试任务", result.getName());
        assertEquals("执行人", result.getExecutorName());
        assertEquals("协助人", result.getAssistantName());
        assertEquals("创建人", result.getCreatorName());
        
        verify(taskMapper).selectById(taskId);
        verify(userMapper).getName(10L);
        verify(userMapper).getName(20L);
        verify(userMapper).getName(30L);
    }
    
    @Test
    void testGetTaskByIdNotFound() {
        // 准备测试数据
        Long taskId = 999L;
        
        // 模拟依赖方法的行为
        when(taskMapper.selectById(taskId)).thenReturn(null);
        
        // 执行测试
        TaskVO result = taskService.getTaskById(taskId);
        
        // 验证结果
        assertNull(result);
        verify(taskMapper).selectById(taskId);
        verify(userMapper, never()).getName(anyLong());
    }
    
    @Test
    void testAddTaskSuccess() {
        // 准备测试数据
        TaskDTO dto = new TaskDTO();
        dto.setName("新任务");
        dto.setDescription("任务描述");
        dto.setExecutorId(10L);
        dto.setAssistantId(20L);
        dto.setCreatorId(30L);
        
        // 模拟依赖方法的行为
        when(taskMapper.insertTask(any(Task.class))).thenReturn(1);
        
        // 执行测试
        assertDoesNotThrow(() -> taskService.addTask(dto));
        
        // 验证结果
        verify(taskMapper).insertTask(any(Task.class));
    }
    
    @Test
    void testUpdateTaskSuccess() {
        // 准备测试数据
        Long taskId = 1L;
        TaskDTO dto = new TaskDTO();
        dto.setName("更新的任务");
        dto.setDescription("更新的描述");
        dto.setExecutorId(15L);
        dto.setAssistantId(25L);
        
        // 模拟依赖方法的行为
        when(taskMapper.updateTask(any(Task.class))).thenReturn(1);
        
        // 执行测试
        assertDoesNotThrow(() -> taskService.updateTask(taskId, dto));
        
        // 验证结果
        verify(taskMapper).updateTask(any(Task.class));
    }
    
    @Test
    void testDeleteTaskByIdSuccess() {
        // 准备测试数据
        Long taskId = 1L;
        
        // 模拟依赖方法的行为
        when(taskMapper.deleteTaskById(taskId)).thenReturn(1);
        
        // 执行测试
        assertDoesNotThrow(() -> taskService.deleteTaskById(taskId));
        
        // 验证结果
        verify(taskMapper).deleteTaskById(taskId);
    }
    
    @Test
    void testUpdateTaskStatusSuccess() {
        // 准备测试数据
        Long taskId = 1L;
        String status = "已完成";
        
        // 模拟依赖方法的行为
        when(taskMapper.updateTaskStatus(any(Task.class))).thenReturn(1);
        
        // 执行测试
        boolean result = taskService.updateTaskStatus(taskId, status);
        
        // 验证结果
        assertTrue(result);
        verify(taskMapper).updateTaskStatus(any(Task.class));
    }
    
    @Test
    void testUpdateTaskStatusFailed() {
        // 准备测试数据
        Long taskId = 999L;
        String status = "已完成";
        
        // 模拟依赖方法的行为
        when(taskMapper.updateTaskStatus(any(Task.class))).thenReturn(0);
        
        // 执行测试
        boolean result = taskService.updateTaskStatus(taskId, status);
        
        // 验证结果
        assertFalse(result);
        verify(taskMapper).updateTaskStatus(any(Task.class));
    }
    
    @Test
    void testDeleteTasksSuccess() {
        // 准备测试数据
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        
        // 执行测试
        boolean result = taskService.deleteTasks(ids);
        
        // 验证结果
        assertTrue(result);
        verify(taskMapper, times(3)).deleteTaskById(anyLong());
    }
    
    @Test
    void testDeleteTasksEmpty() {
        // 准备测试数据
        List<Long> ids = new ArrayList<>();
        
        // 执行测试
        boolean result = taskService.deleteTasks(ids);
        
        // 验证结果
        assertFalse(result);
        verify(taskMapper, never()).deleteTaskById(anyLong());
    }
    
    @Test
    void testFindUsersByName() {
        // 准备测试数据
        String name = "张";
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(createUser(1L, "张三"));
        expectedUsers.add(createUser(2L, "张四"));
        
        // 模拟依赖方法的行为
        when(taskMapper.selectUsersByName(name)).thenReturn(expectedUsers);
        
        // 执行测试
        List<User> result = taskService.findUsersByName(name);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("张三", result.get(0).getRealName());
        assertEquals("张四", result.get(1).getRealName());
        verify(taskMapper).selectUsersByName(name);
    }
    
    @Test
    void testFindUsersByNameEmpty() {
        // 准备测试数据
        String name = "不存在的名字";
        
        // 模拟依赖方法的行为
        when(taskMapper.selectUsersByName(name)).thenReturn(new ArrayList<>());
        
        // 执行测试
        List<User> result = taskService.findUsersByName(name);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskMapper).selectUsersByName(name);
    }
    
    // 辅助方法，创建用户对象
    private User createUser(Long id, String name) {
        User user = new User();
        user.setUserId(id);
        user.setRealName(name);
        return user;
    }
} 