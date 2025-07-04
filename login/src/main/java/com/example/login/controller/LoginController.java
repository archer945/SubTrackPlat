package com.example.login.controller;

import com.example.login.config.JwtUtils;
import com.example.login.dto.UserDTO;
import com.example.login.entity.User;
import com.example.login.service.CaptchaService;
import com.example.login.service.LoginLogService;
import com.example.login.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Resource
    private UserService userService;

    @Resource
    private CaptchaService captchaService;

    @Resource
    private LoginLogService loginLogService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody UserDTO userDTO) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.findByUsername(userDTO.getUsername());
        if (user == null) {
            loginLogService.record(null, userDTO.getUsername(), false, "用户不存在");
            result.put("code", 401);
            result.put("message", "用户不存在");
            return result;
        }

//        boolean captchaOk = captchaService.validateCaptcha(user.getUserId(), captcha);
//        if (!captchaOk) {
//            loginLogService.record(user.getUserId(), username, false, "验证码错误");
//            result.put("code", 403);
//            result.put("message", "验证码错误");
//            return result;
//        }

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            loginLogService.record(user.getUserId(), userDTO.getUsername(), false, "密码错误");
            result.put("code", 403);
            result.put("message", "密码错误");
            return result;
        }

        loginLogService.record(user.getUserId(), userDTO.getUsername(), true, "登录成功");
        String token = jwtUtils.generateToken(userDTO.getUsername());
        user.setLastLoginTime(LocalDateTime.now());
        userService.updateByUserId(user);
        result.put("code", 200);
        result.put("message", "登录成功");
        result.put("token", token);
        return result;
    }

//    @GetMapping("/captcha")
//    public String getCaptcha() {
//        Captcha captcha = captchaService.generateCaptcha();
//        return captcha.getCaptchaCode();
//    }
}