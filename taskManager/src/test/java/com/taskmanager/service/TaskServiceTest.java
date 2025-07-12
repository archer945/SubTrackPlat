package com.taskmanager.service;

import com.taskmanager.mapper.TaskMapper;
import com.taskmanager.mapper.UserMapper;
import com.taskmanager.model.dto.TaskDTO;
import com.taskmanager.model.entity.Task;
import com.taskmanager.model.entity.User;
import com.taskmanager.model.query.TaskQuery;
import com.taskmanager.model.vo.TaskVO;
import com.taskmanager.service.impl.TaskServiceImpl;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    
    @Captor
    private ArgumentCaptor<Task> taskCaptor;
    
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
    void testGetTaskByIdWithNullExecutorId() {
        // 准备测试数据
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setName("测试任务");
        task.setExecutorId(null);
        task.setAssistantId(20L);
        task.setCreatorId(30L);
        
        // 模拟依赖方法的行为
        when(taskMapper.selectById(taskId)).thenReturn(task);
        when(userMapper.getName(20L)).thenReturn("协助人");
        when(userMapper.getName(30L)).thenReturn("创建人");
        
        // 执行测试
        TaskVO result = taskService.getTaskById(taskId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("测试任务", result.getName());
        assertNull(result.getExecutorName());
        assertEquals("协助人", result.getAssistantName());
        assertEquals("创建人", result.getCreatorName());
        
        verify(taskMapper).selectById(taskId);
        verify(userMapper, never()).getName(eq(null));
        verify(userMapper).getName(20L);
        verify(userMapper).getName(30L);
    }
    
    @Test
    void testGetTaskByIdWithNullAssistantId() {
        // 准备测试数据
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setName("测试任务");
        task.setExecutorId(10L);
        task.setAssistantId(null);
        task.setCreatorId(30L);
        
        // 模拟依赖方法的行为
        when(taskMapper.selectById(taskId)).thenReturn(task);
        when(userMapper.getName(10L)).thenReturn("执行人");
        when(userMapper.getName(30L)).thenReturn("创建人");
        
        // 执行测试
        TaskVO result = taskService.getTaskById(taskId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("测试任务", result.getName());
        assertEquals("执行人", result.getExecutorName());
        assertNull(result.getAssistantName());
        assertEquals("创建人", result.getCreatorName());
        
        verify(taskMapper).selectById(taskId);
        verify(userMapper).getName(10L);
        verify(userMapper, never()).getName(eq(null));
        verify(userMapper).getName(30L);
    }
    
    @Test
    void testGetTaskByIdWithNullCreatorId() {
        // 准备测试数据
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setName("测试任务");
        task.setExecutorId(10L);
        task.setAssistantId(20L);
        task.setCreatorId(null);
        
        // 模拟依赖方法的行为
        when(taskMapper.selectById(taskId)).thenReturn(task);
        when(userMapper.getName(10L)).thenReturn("执行人");
        when(userMapper.getName(20L)).thenReturn("协助人");
        
        // 执行测试
        TaskVO result = taskService.getTaskById(taskId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("测试任务", result.getName());
        assertEquals("执行人", result.getExecutorName());
        assertEquals("协助人", result.getAssistantName());
        assertNull(result.getCreatorName());
        
        verify(taskMapper).selectById(taskId);
        verify(userMapper).getName(10L);
        verify(userMapper).getName(20L);
        verify(userMapper, never()).getName(eq(null));
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
        verify(taskMapper).insertTask(taskCaptor.capture());
        
        Task capturedTask = taskCaptor.getValue();
        assertEquals("新任务", capturedTask.getName());
        assertEquals("任务描述", capturedTask.getDescription());
        assertEquals(10L, capturedTask.getExecutorId());
        assertEquals(20L, capturedTask.getAssistantId());
        assertEquals(30L, capturedTask.getCreatorId());
        assertNotNull(capturedTask.getCreateTime());
        assertNotNull(capturedTask.getUpdateTime());
    }
    
    @Test
    void testAddTaskWithNullIds() {
        // 准备测试数据
        TaskDTO dto = new TaskDTO();
        dto.setName("新任务");
        dto.setDescription("任务描述");
        dto.setExecutorId(null);
        dto.setAssistantId(null);
        dto.setCreatorId(null);
        
        // 模拟依赖方法的行为
        when(taskMapper.insertTask(any(Task.class))).thenReturn(1);
        
        // 执行测试
        assertDoesNotThrow(() -> taskService.addTask(dto));
        
        // 验证结果
        verify(taskMapper).insertTask(taskCaptor.capture());
        
        Task capturedTask = taskCaptor.getValue();
        assertEquals("新任务", capturedTask.getName());
        assertEquals("任务描述", capturedTask.getDescription());
        assertNull(capturedTask.getExecutorId());
        assertNull(capturedTask.getAssistantId());
        assertNull(capturedTask.getCreatorId());
        assertNotNull(capturedTask.getCreateTime());
        assertNotNull(capturedTask.getUpdateTime());
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
        verify(taskMapper).updateTask(taskCaptor.capture());
        
        Task capturedTask = taskCaptor.getValue();
        assertEquals(taskId, capturedTask.getId());
        assertEquals("更新的任务", capturedTask.getName());
        assertEquals("更新的描述", capturedTask.getDescription());
        assertEquals(15L, capturedTask.getExecutorId());
        assertEquals(25L, capturedTask.getAssistantId());
        assertNotNull(capturedTask.getUpdateTime());
    }
    
    @Test
    void testUpdateTaskWithNullIds() {
        // 准备测试数据
        Long taskId = 1L;
        TaskDTO dto = new TaskDTO();
        dto.setName("更新的任务");
        dto.setDescription("更新的描述");
        dto.setExecutorId(null);
        dto.setAssistantId(null);
        
        // 模拟依赖方法的行为
        when(taskMapper.updateTask(any(Task.class))).thenReturn(1);
        
        // 执行测试
        assertDoesNotThrow(() -> taskService.updateTask(taskId, dto));
        
        // 验证结果
        verify(taskMapper).updateTask(taskCaptor.capture());
        
        Task capturedTask = taskCaptor.getValue();
        assertEquals(taskId, capturedTask.getId());
        assertEquals("更新的任务", capturedTask.getName());
        assertEquals("更新的描述", capturedTask.getDescription());
        assertNull(capturedTask.getExecutorId());
        assertNull(capturedTask.getAssistantId());
        assertNotNull(capturedTask.getUpdateTime());
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
        verify(taskMapper).updateTaskStatus(taskCaptor.capture());
        
        Task capturedTask = taskCaptor.getValue();
        assertEquals(taskId, capturedTask.getId());
        assertEquals(status, capturedTask.getStatus());
        assertNotNull(capturedTask.getUpdateTime());
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
        
        // 模拟依赖方法的行为
        when(taskMapper.deleteTaskById(anyLong())).thenReturn(1);
        
        // 执行测试
        boolean result = taskService.deleteTasks(ids);
        
        // 验证结果
        assertTrue(result);
        verify(taskMapper, times(3)).deleteTaskById(anyLong());
        verify(taskMapper).deleteTaskById(1L);
        verify(taskMapper).deleteTaskById(2L);
        verify(taskMapper).deleteTaskById(3L);
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
    void testDeleteTasksNull() {
        // 执行测试
        boolean result = taskService.deleteTasks(null);
        
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
    
    @Test
    void testGetTaskPage() {
        // 准备测试数据
        TaskQuery query = new TaskQuery();
        query.setPage(1);
        query.setSize(10);
        
        List<Task> taskList = new ArrayList<>();
        Task task1 = new Task();
        task1.setId(1L);
        task1.setName("任务1");
        task1.setExecutorId(10L);
        task1.setAssistantId(20L);
        task1.setCreatorId(30L);
        taskList.add(task1);
        
        Task task2 = new Task();
        task2.setId(2L);
        task2.setName("任务2");
        task2.setExecutorId(11L);
        task2.setAssistantId(21L);
        task2.setCreatorId(31L);
        taskList.add(task2);
        
        List<User> users = Arrays.asList(
            createUser(10L, "执行人1"),
            createUser(11L, "执行人2"),
            createUser(20L, "协助人1"),
            createUser(21L, "协助人2"),
            createUser(30L, "创建人1"),
            createUser(31L, "创建人2")
        );
        
        // 模拟依赖方法的行为
        when(taskMapper.selectTaskPage(query)).thenReturn(taskList);
        when(userMapper.selectByIds(anyList())).thenReturn(users);
        
        // 执行测试
        PageInfo<TaskVO> result = taskService.getTaskPage(query);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getList().size());
        
        TaskVO vo1 = result.getList().get(0);
        assertEquals(1L, vo1.getId());
        assertEquals("任务1", vo1.getName());
        assertEquals("执行人1", vo1.getExecutorName());
        assertEquals("协助人1", vo1.getAssistantName());
        assertEquals("创建人1", vo1.getCreatorName());
        
        TaskVO vo2 = result.getList().get(1);
        assertEquals(2L, vo2.getId());
        assertEquals("任务2", vo2.getName());
        assertEquals("执行人2", vo2.getExecutorName());
        assertEquals("协助人2", vo2.getAssistantName());
        assertEquals("创建人2", vo2.getCreatorName());
        
        verify(taskMapper).selectTaskPage(query);
        verify(userMapper).selectByIds(anyList());
    }
    
    @Test
    void testGetTaskPageWithEmptyList() {
        // 准备测试数据
        TaskQuery query = new TaskQuery();
        query.setPage(1);
        query.setSize(10);
        
        // 模拟依赖方法的行为
        when(taskMapper.selectTaskPage(query)).thenReturn(Collections.emptyList());
        
        // 执行测试
        PageInfo<TaskVO> result = taskService.getTaskPage(query);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
        
        verify(taskMapper).selectTaskPage(query);
        verify(userMapper, never()).selectByIds(anyList());
    }
    
    @Test
    void testGetTaskList() {
        // 准备测试数据
        TaskQuery query = new TaskQuery();
        
        List<Task> taskList = new ArrayList<>();
        Task task1 = new Task();
        task1.setId(1L);
        task1.setName("任务1");
        task1.setExecutorId(10L);
        task1.setAssistantId(20L);
        taskList.add(task1);
        
        Task task2 = new Task();
        task2.setId(2L);
        task2.setName("任务2");
        task2.setExecutorId(11L);
        task2.setAssistantId(21L);
        taskList.add(task2);
        
        List<User> users = Arrays.asList(
            createUser(10L, "执行人1"),
            createUser(11L, "执行人2"),
            createUser(20L, "协助人1"),
            createUser(21L, "协助人2")
        );
        
        // 模拟依赖方法的行为
        when(taskMapper.selectTaskPage(query)).thenReturn(taskList);
        when(userMapper.selectByIds(anyList())).thenReturn(users);
        
        // 执行测试
        List<TaskVO> result = taskService.getTaskList(query);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        
        TaskVO vo1 = result.get(0);
        assertEquals(1L, vo1.getId());
        assertEquals("任务1", vo1.getName());
        assertEquals("执行人1", vo1.getExecutorName());
        assertEquals("协助人1", vo1.getAssistantName());
        
        TaskVO vo2 = result.get(1);
        assertEquals(2L, vo2.getId());
        assertEquals("任务2", vo2.getName());
        assertEquals("执行人2", vo2.getExecutorName());
        assertEquals("协助人2", vo2.getAssistantName());
        
        verify(taskMapper).selectTaskPage(query);
        verify(userMapper).selectByIds(anyList());
    }
    
    @Test
    void testGetTaskListWithEmptyList() {
        // 准备测试数据
        TaskQuery query = new TaskQuery();
        
        // 模拟依赖方法的行为
        when(taskMapper.selectTaskPage(query)).thenReturn(Collections.emptyList());
        
        // 执行测试
        List<TaskVO> result = taskService.getTaskList(query);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(taskMapper).selectTaskPage(query);
        verify(userMapper, never()).selectByIds(anyList());
    }
    
    @Test
    void testGetTaskListWithNullIds() {
        // 准备测试数据
        TaskQuery query = new TaskQuery();
        
        List<Task> taskList = new ArrayList<>();
        Task task1 = new Task();
        task1.setId(1L);
        task1.setName("任务1");
        task1.setExecutorId(null);
        task1.setAssistantId(null);
        taskList.add(task1);
        
        // 模拟依赖方法的行为
        when(taskMapper.selectTaskPage(query)).thenReturn(taskList);
        
        // 执行测试
        List<TaskVO> result = taskService.getTaskList(query);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        
        TaskVO vo1 = result.get(0);
        assertEquals(1L, vo1.getId());
        assertEquals("任务1", vo1.getName());
        assertNull(vo1.getExecutorName());
        assertNull(vo1.getAssistantName());
        
        verify(taskMapper).selectTaskPage(query);
        verify(userMapper, never()).selectByIds(anyList());
    }
    
    // 辅助方法，创建用户对象
    private User createUser(Long id, String name) {
        User user = new User();
        user.setUserId(id);
        user.setRealName(name);
        return user;
    }
} 