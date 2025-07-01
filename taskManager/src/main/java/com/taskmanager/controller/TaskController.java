package com.taskmanager.controller;

import com.alibaba.excel.EasyExcel;
import com.taskmanager.model.dto.TaskDTO;
import com.taskmanager.model.query.TaskQuery;
import com.taskmanager.model.vo.TaskExportVO;
import com.taskmanager.model.vo.TaskVO;
import com.taskmanager.service.TaskService;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // 分页查询任务列表
    @GetMapping
    public PageInfo<TaskVO> getPage(TaskQuery query) {
        return taskService.getTaskPage(query);
    }

    // 查询所有任务（不分页）
    @GetMapping("/all")
    public List<TaskVO> getAll() {
        return taskService.getAllTasks();
    }

    // 查询单个任务详情
    @GetMapping("/{id}")
    public TaskVO getById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    // 新增任务
    @PostMapping
    public void add(@RequestBody TaskDTO dto) {
        taskService.addTask(dto);
    }

    // 修改任务
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody TaskDTO dto) {
        taskService.updateTask(id, dto);
    }

    // 删除任务
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        taskService.deleteTaskById(id);
    }
    @GetMapping("/export")
    public void exportTasks(TaskQuery query, HttpServletResponse response) throws IOException {
        List<TaskVO> taskList = taskService.getTaskList(query);

        List<TaskExportVO> exportList = taskList.stream().map(task -> {
            TaskExportVO vo = new TaskExportVO();
            BeanUtils.copyProperties(task, vo);
            if (task.getPlannedStart() != null)
                vo.setPlannedStart(task.getPlannedStart().toString());
            if (task.getPlannedEnd() != null)
                vo.setPlannedEnd(task.getPlannedEnd().toString());
            return vo;
        }).collect(Collectors.toList());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=tasks.xlsx");

        EasyExcel.write(response.getOutputStream(), TaskExportVO.class)
                .sheet("任务数据")
                .doWrite(exportList);
    }
    @PutMapping("/{id}/status")
    public boolean updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        return taskService.updateTaskStatus(id, status);
    }
    @DeleteMapping
    public boolean deleteTasks(@RequestBody List<Long> ids) {
        // 调用服务层批量删除方法，传入任务 ID 列表
        return taskService.deleteTasks(ids);
    }

}
