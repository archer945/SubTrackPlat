package com.systemManager.controller;


import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.DeptDTO;
import com.common.domain.query.systemManager.DeptQuery;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.DeptTreeVO;
import com.systemManager.mapper.DeptMapper;
import com.systemManager.service.IDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@Tag(name = "部门管理")
@Validated
public class DeptController {
    @Resource
    private IDeptService deptService;

    @Operation(summary = "获取部门列表（条件+分页）")
    @GetMapping
    public JsonVO<PageDTO<DeptTreeVO>> queryDeptList(@ParameterObject @Validated DeptQuery deptQuery) {
        return JsonVO.success(deptService.listDept(deptQuery));
    }

    @GetMapping("/tree")
    @Operation(summary = "部门树形结构")
    public JsonVO<List<DeptTreeVO>> tree() {
        return JsonVO.success(deptService.getDeptTree());
    }

    @Operation(summary = "添加部门")
    @PostMapping
    public JsonVO<String> addDept(@Validated @RequestBody DeptDTO dto) {
        return JsonVO.success(deptService.saveDept(dto));
    }

    @Operation(summary = "更新部门")
    @PutMapping("/{id}")
    public JsonVO<String> updateDept(@PathVariable Long id, @Validated @RequestBody DeptDTO dto) {
        return JsonVO.success(deptService.updateDept(id, dto));
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    public JsonVO<String> removeDept(@PathVariable Long id) {
        return JsonVO.success(deptService.removeDept(id));
    }
}

