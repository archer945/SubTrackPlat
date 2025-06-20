package com.systemmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.systemmanager.entity.User;
import com.systemmanager.mapper.UserMapper;
import com.systemmanager.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
