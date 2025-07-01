package com.taskmanager.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据用户 ID 查询真实姓名
     * @param id 用户ID
     * @return 真实姓名（real_name）
     */
    @Select("SELECT real_name FROM sub_track_plat.user WHERE user_id = #{id}")
    String selectRealNameById(@Param("id") Long id);
}
