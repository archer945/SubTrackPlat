package com.taskmanager.controller;

import com.taskmanager.model.dto.TaskDTO;
import com.taskmanager.model.entity.User;
import com.taskmanager.model.query.TaskQuery;
import com.taskmanager.model.vo.TaskVO;
import com.taskmanager.service.TaskService;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @Mock
    private TaskService taskService;
    
    @InjectMocks
    private TaskController taskController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testGetPage() {
        // 准备测试数据
        TaskQuery query = new TaskQuery();
        query.setPage(1);
        query.setSize(10);
        
        List<TaskVO> taskList = new ArrayList<>();
        TaskVO task1 = new TaskVO();
        task1.setId(1L);
        task1.setName("任务1");
        taskList.add(task1);
        
        TaskVO task2 = new TaskVO();
        task2.setId(2L);
        task2.setName("任务2");
        taskList.add(task2);
        
        PageInfo<TaskVO> pageInfo = new PageInfo<>();
        pageInfo.setList(taskList);
        pageInfo.setTotal(2);
        pageInfo.setPageNum(1);
        pageInfo.setPageSize(10);
        
        // 模拟依赖方法的行为
        when(taskService.getTaskPage(query)).thenReturn(pageInfo);
        
        // 执行测试
        PageInfo<TaskVO> result = taskController.getPage(query);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getList().size());
        assertEquals(1L, result.getList().get(0).getId());
        assertEquals("任务1", result.getList().get(0).getName());
        assertEquals(2L, result.getList().get(1).getId());
        assertEquals("任务2", result.getList().get(1).getName());
        
        verify(taskService).getTaskPage(query);
    }
    
    @Test
    void testGetById() {
        // 准备测试数据
        Long taskId = 1L;
        TaskVO taskVO = new TaskVO();
        taskVO.setId(taskId);
        taskVO.setName("测试任务");
        taskVO.setDescription("任务描述");
        
        // 模拟依赖方法的行为
        when(taskService.getTaskById(taskId)).thenReturn(taskVO);
        
        // 执行测试
        TaskVO result = taskController.getById(taskId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("测试任务", result.getName());
        assertEquals("任务描述", result.getDescription());
        
        verify(taskService).getTaskById(taskId);
    }
    
    @Test
    void testAdd() {
        // 准备测试数据
        TaskDTO dto = new TaskDTO();
        dto.setName("新任务");
        dto.setDescription("任务描述");
        
        // 模拟依赖方法的行为
        doNothing().when(taskService).addTask(dto);
        
        // 执行测试
        assertDoesNotThrow(() -> taskController.add(dto));
        
        // 验证结果
        verify(taskService).addTask(dto);
    }
    
    @Test
    void testUpdate() {
        // 准备测试数据
        Long taskId = 1L;
        TaskDTO dto = new TaskDTO();
        dto.setName("更新的任务");
        dto.setDescription("更新的描述");
        
        // 模拟依赖方法的行为
        doNothing().when(taskService).updateTask(taskId, dto);
        
        // 执行测试
        assertDoesNotThrow(() -> taskController.update(taskId, dto));
        
        // 验证结果
        verify(taskService).updateTask(taskId, dto);
    }
    
    @Test
    void testDelete() {
        // 准备测试数据
        Long taskId = 1L;
        
        // 模拟依赖方法的行为
        doNothing().when(taskService).deleteTaskById(taskId);
        
        // 执行测试
        assertDoesNotThrow(() -> taskController.delete(taskId));
        
        // 验证结果
        verify(taskService).deleteTaskById(taskId);
    }
    
    @Test
    void testExportTasks() throws IOException {
        // 准备测试数据
        TaskQuery query = new TaskQuery();
        
        List<TaskVO> taskList = new ArrayList<>();
        TaskVO task1 = new TaskVO();
        task1.setId(1L);
        task1.setName("任务1");
        task1.setType("巡检");
        task1.setPriority("高");
        task1.setExecutorId(10L);
        task1.setPlannedStart(LocalDateTime.now());
        task1.setPlannedEnd(LocalDateTime.now().plusDays(1));
        task1.setStatus("进行中");
        taskList.add(task1);
        
        TaskVO task2 = new TaskVO();
        task2.setId(2L);
        task2.setName("任务2");
        task2.setType("维修");
        task2.setPriority("中");
        task2.setExecutorId(20L);
        task2.setPlannedStart(LocalDateTime.now().plusDays(2));
        task2.setPlannedEnd(LocalDateTime.now().plusDays(3));
        task2.setStatus("未开始");
        taskList.add(task2);
        
        // 模拟依赖方法的行为
        when(taskService.getTaskList(query)).thenReturn(taskList);
        
        // 创建模拟的HttpServletResponse
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // 执行测试
        assertDoesNotThrow(() -> taskController.exportTasks(query, response));
        
        // 验证结果
        verify(taskService).getTaskList(query);
        // 修正内容类型断言，使用startsWith而不是完全匹配
        assertTrue(response.getContentType().startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        assertEquals("utf-8", response.getCharacterEncoding());
        assertEquals("attachment; filename=tasks.xlsx", response.getHeader("Content-Disposition"));
    }
    
    @Test
    void testUpdateTaskStatus() {
        // 准备测试数据
        Long taskId = 1L;
        String status = "已完成";
        
        // 模拟依赖方法的行为
        when(taskService.updateTaskStatus(taskId, status)).thenReturn(true);
        
        // 执行测试
        boolean result = taskController.updateTaskStatus(taskId, status);
        
        // 验证结果
        assertTrue(result);
        verify(taskService).updateTaskStatus(taskId, status);
    }
    
    @Test
    void testDeleteTasks() {
        // 准备测试数据
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        
        // 模拟依赖方法的行为
        when(taskService.deleteTasks(ids)).thenReturn(true);
        
        // 执行测试
        boolean result = taskController.deleteTasks(ids);
        
        // 验证结果
        assertTrue(result);
        verify(taskService).deleteTasks(ids);
    }
    
    @Test
    void testGetUsersByName() {
        // 准备测试数据
        String name = "张";
        
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setUserId(1L);
        user1.setRealName("张三");
        userList.add(user1);
        
        User user2 = new User();
        user2.setUserId(2L);
        user2.setRealName("张四");
        userList.add(user2);
        
        // 模拟依赖方法的行为
        when(taskService.findUsersByName(name)).thenReturn(userList);
        
        // 执行测试
        Map<String, Object> result = taskController.getUsersByName(name);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("list"));
        
        @SuppressWarnings("unchecked")
        List<User> resultList = (List<User>) result.get("list");
        assertEquals(2, resultList.size());
        assertEquals(1L, resultList.get(0).getUserId());
        assertEquals("张三", resultList.get(0).getRealName());
        assertEquals(2L, resultList.get(1).getUserId());
        assertEquals("张四", resultList.get(1).getRealName());
        
        verify(taskService).findUsersByName(name);
    }
    
    @Test
    void testGetUsersByNameWithNullParam() {
        // 准备测试数据
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setUserId(1L);
        user1.setRealName("张三");
        userList.add(user1);
        
        // 模拟依赖方法的行为
        when(taskService.findUsersByName(null)).thenReturn(userList);
        
        // 执行测试
        Map<String, Object> result = taskController.getUsersByName(null);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("list"));
        
        @SuppressWarnings("unchecked")
        List<User> resultList = (List<User>) result.get("list");
        assertEquals(1, resultList.size());
        
        verify(taskService).findUsersByName(null);
    }
} 