package com.example.login.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class LoginLog {

    private Long logId;
    private Long userId;
    private String username;
    private Integer status;
    private String msg;
    private LocalDateTime loginTime;

}