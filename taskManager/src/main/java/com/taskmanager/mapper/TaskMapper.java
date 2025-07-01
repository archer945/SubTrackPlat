package com.taskmanager.mapper;

import com.taskmanager.model.entity.Task;
import com.taskmanager.model.query.TaskQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskMapper {

    int insertTask(Task task);

    int updateTask(Task task);

    int deleteTaskById(@Param("id") Long id);

    Task selectById(@Param("id") Long id);

    List<Task> selectTaskPage(TaskQuery query);

    List<Task> getAllTasks();
    int updateTaskStatus(Task task);


}
