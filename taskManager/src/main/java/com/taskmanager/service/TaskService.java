package com.taskmanager.service;

import com.taskmanager.model.dto.TaskDTO;
import com.taskmanager.model.entity.Task;
import com.taskmanager.model.query.TaskQuery;
import com.taskmanager.model.vo.TaskVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface TaskService {

    TaskVO getTaskById(Long id);

    void addTask(TaskDTO dto);

    void updateTask(Long id, TaskDTO dto);

    void deleteTaskById(Long id);

    PageInfo<TaskVO> getTaskPage(TaskQuery query);

    List<TaskVO> getAllTasks();
    List<TaskVO> getTaskList(TaskQuery query);
    boolean updateTaskStatus(Long id, String status);
    /**
     * 批量删除任务
     * @param ids 要删除的任务 ID 列表
     * @return 是否删除成功
     */
    boolean deleteTasks(List<Long> ids);


}
