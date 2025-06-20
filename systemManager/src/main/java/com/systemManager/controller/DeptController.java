package com.systemManager.controller;


import com.systemManager.mapper.DeptMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 部门表 前端控制器
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@RestController
@RequestMapping("/systemManager/dept")
public class DeptController {
    @Resource
    private DeptMapper deptMapper;

}

