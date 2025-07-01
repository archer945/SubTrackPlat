package com.example.login.controller;

import com.example.login.entity.User;
import com.example.login.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Resource
    private UserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        System.out.println(user.getUsername());
        if (userService.findByUsername(user.getUsername()) != null) {
            result.put("code", 400);
            result.put("message", "用户名已存在");
            return result;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        result.put("code", 200);
        result.put("message", "注册成功");
        return result;
    }

    // 这个要改一下
    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.findByUsername(username);
        if (user == null) {
            result.put("code", 404);
            result.put("message", "用户不存在");
            return result;
        }
        user.setPassword(passwordEncoder.encode("123456"));
        userService.updateByUserId(user);
        result.put("code", 200);
        result.put("message", "密码已重置为123456");
        return result;
    }
}