package com.taskmanager.service.impl;

import com.taskmanager.mapper.TaskMapper;
import com.taskmanager.model.dto.TaskDTO;
import com.taskmanager.model.entity.Task;
import com.taskmanager.model.query.TaskQuery;
import com.taskmanager.model.vo.TaskVO;
import com.taskmanager.service.TaskService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskVO getTaskById(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null) return null;
        TaskVO vo = new TaskVO();
        BeanUtils.copyProperties(task, vo);
        return vo;
    }

    @Override
    public void addTask(TaskDTO dto) {
        Task task = new Task();
        BeanUtils.copyProperties(dto, task);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.insertTask(task);
    }

    @Override
    public void updateTask(Long id, TaskDTO dto) {
        Task task = new Task();
        BeanUtils.copyProperties(dto, task);
        task.setId(id);
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.updateTask(task);
    }

    @Override
    public void deleteTaskById(Long id) {
        taskMapper.deleteTaskById(id);
    }

    @Override
    public PageInfo<TaskVO> getTaskPage(TaskQuery query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        List<Task> list = taskMapper.selectTaskPage(query);
        List<TaskVO> voList = list.stream().map(task -> {
            TaskVO vo = new TaskVO();
            BeanUtils.copyProperties(task, vo);
            return vo;
        }).collect(Collectors.toList());
        return new PageInfo<>(voList);
    }

    @Override
    public List<TaskVO> getAllTasks() {
        return taskMapper.getAllTasks().stream().map(task -> {
            TaskVO vo = new TaskVO();
            BeanUtils.copyProperties(task, vo);
            return vo;
        }).collect(Collectors.toList());
    }
    @Override
    public List<TaskVO> getTaskList(TaskQuery query) {
        List<Task> list = taskMapper.selectTaskPage(query); // 可复用已有 SQL
        return list.stream().map(task -> {
            TaskVO vo = new TaskVO();
            BeanUtils.copyProperties(task, vo);
            return vo;
        }).collect(Collectors.toList());
    }
    @Override
    public boolean updateTaskStatus(Long id, String status) {
        Task task = new Task();
        task.setId(id);
        task.setStatus(status);
        task.setUpdateTime(LocalDateTime.now());
        return taskMapper.updateTaskStatus(task) > 0;
    }
    @Override
    public boolean deleteTasks(List<Long> ids) {
        // 如果为空，直接返回 false
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        // 遍历 ID 列表并逐个删除任务
        for (Long id : ids) {
            taskMapper.deleteTaskById(id);
        }
        return true;
    }

}
