package com.example.login.service;

public interface LoginLogService {
    void record(Long userId, String username, boolean success, String msg);
}