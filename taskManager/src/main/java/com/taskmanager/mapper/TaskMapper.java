package com.taskmanager.mapper;

import com.taskmanager.model.entity.Task;
import com.taskmanager.model.entity.User;
import com.taskmanager.model.query.TaskQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskMapper {

    // 插入任务
    int insertTask(Task task);

    // 更新任务
    int updateTask(Task task);

    // 删除任务（通过ID）
    int deleteTaskById(@Param("id") Long id);

    // 根据任务ID查询任务
    Task selectById(@Param("id") Long id);

    // 分页查询任务
    List<Task> selectTaskPage(TaskQuery query);

    // 查询所有任务
    List<Task> getAllTasks();

    // 更新任务状态
    int updateTaskStatus(Task task);

    List<User> selectUsersByName(@Param("name") String name);
}
