package com.taskmanager.service.impl;

import com.taskmanager.mapper.TaskMapper;
import com.taskmanager.mapper.UserMapper;
import com.taskmanager.model.dto.TaskDTO;
import com.taskmanager.model.entity.Task;
import com.taskmanager.model.entity.User;
import com.taskmanager.model.query.TaskQuery;
import com.taskmanager.model.vo.TaskVO;
import com.taskmanager.service.TaskService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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
    public void addTask(TaskDTO dto) {
        Task task = new Task();
        BeanUtils.copyProperties(dto, task);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());

        // 查询执行人、协助人姓名
        if (dto.getExecutorId() != null) {
            // 不需要设置 executorName，查询时会自动填充用户信息
            task.setExecutorId(dto.getExecutorId());
        }
        if (dto.getAssistantId() != null) {
            // 不需要设置 assistantName，查询时会自动填充用户信息
            task.setAssistantId(dto.getAssistantId());
        }
        if (dto.getCreatorId() != null) {
            task.setCreatorId(dto.getCreatorId());
        }

        taskMapper.insertTask(task);
    }

    @Override
    public void updateTask(Long id, TaskDTO dto) {
        Task task = new Task();
        BeanUtils.copyProperties(dto, task);
        task.setId(id);
        task.setUpdateTime(LocalDateTime.now());

        // 更新执行人 / 协助人 ID（如有变动）
        if (dto.getExecutorId() != null) {
            task.setExecutorId(dto.getExecutorId());
        }
        if (dto.getAssistantId() != null) {
            task.setAssistantId(dto.getAssistantId());
        }

        taskMapper.updateTask(task);
    }

    @Override
    public TaskVO getTaskById(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null) return null;

        TaskVO vo = new TaskVO();
        BeanUtils.copyProperties(task, vo);

        // 查询执行人姓名
        if (task.getExecutorId() != null) {
            String executorName = userMapper.getName(task.getExecutorId());
            vo.setExecutorName(executorName);  // 设置执行人姓名
        }

        // 查询协助人姓名
        if (task.getAssistantId() != null) {
            String assistantName = userMapper.getName(task.getAssistantId());
            vo.setAssistantName(assistantName);  // 设置协助人姓名
        }
        // 查询创建人姓名
        if (task.getCreatorId() != null) {
            String creatorName = userMapper.getName(task.getCreatorId());
            vo.setCreatorName(creatorName);  // 设置创建人姓名
        }

        return vo;
    }


    @Override
    public void deleteTaskById(Long id) {
        taskMapper.deleteTaskById(id);
    }

    @Override
    public PageInfo<TaskVO> getTaskPage(TaskQuery query) {
        long t0 = System.currentTimeMillis();
        PageHelper.startPage(query.getPage(), query.getSize());
        long t1 = System.currentTimeMillis();
        List<Task> list = taskMapper.selectTaskPage(query);
        long t2 = System.currentTimeMillis();
        System.out.println("[耗时统计] PageHelper.startPage: " + (t1 - t0) + "ms, selectTaskPage SQL: " + (t2 - t1) + "ms");

        // 1. 收集所有相关用户ID
        Set<Long> userIds = new HashSet<>();
        for (Task task : list) {
            if (task.getExecutorId() != null) userIds.add(task.getExecutorId());
            if (task.getAssistantId() != null) userIds.add(task.getAssistantId());
            if (task.getCreatorId() != null) userIds.add(task.getCreatorId());
        }
        long t3 = System.currentTimeMillis();
        System.out.println("[耗时统计] 收集用户ID: " + (t3 - t2) + "ms");

        // 2. 批量查询用户信息
        Map<Long, String> userNameMap;
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectByIds(new ArrayList<>(userIds));
            userNameMap = users.stream().collect(Collectors.toMap(User::getUserId, User::getRealName));
        } else {
            userNameMap = Collections.emptyMap();
        }
        final Map<Long, String> finalUserNameMap = userNameMap;
        long t4 = System.currentTimeMillis();
        System.out.println("[耗时统计] 批量查用户: " + (t4 - t3) + "ms");

        // 3. 组装VO
        long t5 = System.currentTimeMillis();
        List<TaskVO> voList = list.stream().map(task -> {
            TaskVO vo = new TaskVO();
            BeanUtils.copyProperties(task, vo);
            if (task.getExecutorId() != null) {
                vo.setExecutorName(finalUserNameMap.get(task.getExecutorId()));
            }
            if (task.getAssistantId() != null) {
                vo.setAssistantName(finalUserNameMap.get(task.getAssistantId()));
            }
            if (task.getCreatorId() != null) {
                vo.setCreatorName(finalUserNameMap.get(task.getCreatorId()));
            }
            return vo;
        }).collect(Collectors.toList());
        long t6 = System.currentTimeMillis();
        System.out.println("[耗时统计] 组装VO: " + (t6 - t5) + "ms");

        // 保持分页结构
        PageInfo<Task> pageInfo = new PageInfo<>(list);
        PageInfo<TaskVO> voPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, voPageInfo);
        voPageInfo.setList(voList);
        long t7 = System.currentTimeMillis();
        System.out.println("[耗时统计] PageInfo组装: " + (t7 - t6) + "ms, getTaskPage总耗时: " + (t7 - t0) + "ms");
        return voPageInfo;
    }

    @Override
    public List<TaskVO> getTaskList(TaskQuery query) {
        List<Task> list = taskMapper.selectTaskPage(query);

        // 1. 收集所有相关用户ID
        Set<Long> userIds = new HashSet<>();
        for (Task task : list) {
            if (task.getExecutorId() != null) userIds.add(task.getExecutorId());
            if (task.getAssistantId() != null) userIds.add(task.getAssistantId());
        }

        // 2. 批量查询用户信息
        Map<Long, String> userNameMap;
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectByIds(new ArrayList<>(userIds));
            userNameMap = users.stream().collect(Collectors.toMap(User::getUserId, User::getRealName));
        } else {
            userNameMap = Collections.emptyMap();
        }
        final Map<Long, String> finalUserNameMap = userNameMap;

        // 3. 组装VO
        return list.stream().map(task -> {
            TaskVO vo = new TaskVO();
            BeanUtils.copyProperties(task, vo);
            if (task.getExecutorId() != null) {
                vo.setExecutorName(finalUserNameMap.get(task.getExecutorId()));
            }
            if (task.getAssistantId() != null) {
                vo.setAssistantName(finalUserNameMap.get(task.getAssistantId()));
            }
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

    @Override
    public List<User> findUsersByName(String name) {
        return taskMapper.selectUsersByName(name);
    }


}
