package com.taskmanager.service.impl;

import com.taskmanager.mapper.TaskMapper;
import com.taskmanager.mapper.UserMapper;
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
    private final UserMapper userMapper;
    public TaskServiceImpl(TaskMapper taskMapper, UserMapper userMapper) {
        this.taskMapper = taskMapper;
        this.userMapper = userMapper;
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

        // 查询执行人、协助人、创建人姓名
        if (dto.getExecutorId() != null) {
            task.setExecutorName(userMapper.selectRealNameById(dto.getExecutorId()));
        }
        if (dto.getAssistantId() != null) {
            task.setAssistantName(userMapper.selectRealNameById(dto.getAssistantId()));
        }
        if (dto.getCreatorId() != null) {
            task.setCreatorId(dto.getCreatorId());
            task.setCreatorName(userMapper.selectRealNameById(dto.getCreatorId()));
        }

        taskMapper.insertTask(task);
    }

    @Override
    public void updateTask(Long id, TaskDTO dto) {
        Task task = new Task();
        BeanUtils.copyProperties(dto, task);
        task.setId(id);
        task.setUpdateTime(LocalDateTime.now());

        // 更新执行人 / 协助人姓名（如有变动）
        if (dto.getExecutorId() != null) {
            task.setExecutorName(userMapper.selectRealNameById(dto.getExecutorId()));
        }
        if (dto.getAssistantId() != null) {
            task.setAssistantName(userMapper.selectRealNameById(dto.getAssistantId()));
        }

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

        // 必须先构造 PageInfo 原始分页对象（包含 total）
        PageInfo<Task> pageInfo = new PageInfo<>(list);

        // 再将 list 映射成 voList
        List<TaskVO> voList = list.stream().map(task -> {
            TaskVO vo = new TaskVO();
            BeanUtils.copyProperties(task, vo);
            return vo;
        }).collect(Collectors.toList());

        // 手动构造 PageInfo<TaskVO>，保持分页结构
        PageInfo<TaskVO> voPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, voPageInfo);
        voPageInfo.setList(voList);
        return voPageInfo;
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
