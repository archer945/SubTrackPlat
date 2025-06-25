package com.example.login.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.login.entity.User;

public interface UserService extends IService<User> {
    User findByUsername(String username);
}