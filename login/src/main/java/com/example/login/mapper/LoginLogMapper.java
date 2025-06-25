package com.example.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.login.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
}