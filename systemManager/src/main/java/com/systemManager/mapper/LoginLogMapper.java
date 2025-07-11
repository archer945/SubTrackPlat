package com.systemManager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.systemManager.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
    
    /**
     * 分页查询登录日志
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param status 状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页数据
     */
    IPage<LoginLog> selectLoginLogPage(
            Page<LoginLog> page,
            @Param("userId") Long userId,
            @Param("status") Integer status,
            @Param("startTime") String startTime, 
            @Param("endTime") String endTime
    );
}
