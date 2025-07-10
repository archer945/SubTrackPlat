package com.systemManager.controller;


import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.DeptDTO;
import com.common.domain.dto.systemManager.DeptUserDTO;
import com.common.domain.query.systemManager.DeptQuery;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.DeptTreeVO;
import com.common.domain.vo.systemManager.UserVO;
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
    
    @Operation(summary = "获取部门树")
    @GetMapping("/tree")
    public JsonVO<List<DeptTreeVO>> getDeptTree() {
        return JsonVO.success(deptService.getDeptTree());
    }
    
    @Operation(summary = "获取部门人员数量")
    @GetMapping("/{deptId}/user/count")
    public JsonVO<Long> getDeptUserCount(@PathVariable Long deptId) {
        return JsonVO.success(deptService.getDeptUserCount(deptId));
    }
    
    @Operation(summary = "获取部门人员列表")
    @GetMapping("/{deptId}/users")
    public JsonVO<PageDTO<UserVO>> getDeptUsers(
            @PathVariable Long deptId,
            @RequestParam(defaultValue = "1") Integer pageIndex,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username) {
        return JsonVO.success(deptService.getDeptUsers(deptId, pageIndex, pageSize, username));
    }
    
    @Operation(summary = "获取部门人员统计信息")
    @GetMapping("/{deptId}/user-info")
    public JsonVO<DeptUserDTO> getDeptUserInfo(@PathVariable Long deptId) {
        return JsonVO.success(deptService.getDeptUserInfo(deptId));
    }
}

