package com.systemmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.systemmanager.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
