package com.systemManager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.systemManager.entity.UserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface UserRoleMapper extends BaseMapper<UserRole> {
    
    /**
     * 根据用户ID批量删除用户角色关联
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);
    
    /**
     * 批量插入用户角色关联
     * @param userRoles 用户角色关联列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<UserRole> userRoles);
}
