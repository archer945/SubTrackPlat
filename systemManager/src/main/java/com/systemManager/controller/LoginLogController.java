package com.systemManager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.domain.dto.PageDTO;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.LoginLogVO;
import com.systemManager.entity.LoginLog;
import com.systemManager.service.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 登录日志表 前端控制器
 * </p>
 *
 * @author yuyu
 * @since 2025-06-30
 */
@RestController
@RequestMapping("/systemManager/user")
@Tag(name = "用户登录日志")
public class LoginLogController {

    @Autowired
    private LoginLogService loginLogService;

    /**
     * 查询登录日志列表
     */
    @Operation(summary = "获取登录日志列表")
    @GetMapping("/loginLog")
    public JsonVO<PageDTO<LoginLogVO>> getLoginLogs(@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                 @RequestParam(value = "status", required = false) Integer status,
                                                 @RequestParam(value = "startTime", required = false) String startTime,
                                                 @RequestParam(value = "endTime", required = false) String endTime) {
        Page<LoginLog> page = new Page<>(pageIndex, pageSize);
        IPage<LoginLog> pageData = loginLogService.getLoginLogPage(page, null, status, startTime, endTime);
        
        List<LoginLogVO> records = convertToVOList(pageData.getRecords());
        
        PageDTO<LoginLogVO> pageDTO = new PageDTO<>();
        pageDTO.setRows(records);
        pageDTO.setTotal(pageData.getTotal());
        pageDTO.setPages(pageData.getPages());
        pageDTO.setPageSize(pageSize.longValue());
        pageDTO.setPageIndex(pageIndex.longValue());
        
        return JsonVO.success(pageDTO);
    }

    /**
     * 查询指定用户的登录日志
     */
    @Operation(summary = "获取指定用户的登录日志")
    @GetMapping("/{userId}/loginLog")
    public JsonVO<PageDTO<LoginLogVO>> getUserLoginLogs(@PathVariable("userId") Long userId,
                                                     @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                     @RequestParam(value = "status", required = false) Integer status,
                                                     @RequestParam(value = "startTime", required = false) String startTime,
                                                     @RequestParam(value = "endTime", required = false) String endTime) {
        Page<LoginLog> page = new Page<>(pageIndex, pageSize);
        IPage<LoginLog> pageData = loginLogService.getLoginLogPage(page, userId, status, startTime, endTime);
        
        List<LoginLogVO> records = convertToVOList(pageData.getRecords());
        
        PageDTO<LoginLogVO> pageDTO = new PageDTO<>();
        pageDTO.setRows(records);
        pageDTO.setTotal(pageData.getTotal());
        pageDTO.setPages(pageData.getPages());
        pageDTO.setPageSize(pageSize.longValue());
        pageDTO.setPageIndex(pageIndex.longValue());
        
        return JsonVO.success(pageDTO);
    }
    
    /**
     * 将实体列表转换为VO列表
     */
    private List<LoginLogVO> convertToVOList(List<LoginLog> entityList) {
        return entityList.stream().map(entity -> {
            LoginLogVO vo = new LoginLogVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
    }
} 