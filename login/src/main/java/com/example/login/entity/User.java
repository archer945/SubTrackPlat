package com.example.login.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@TableName(value = "user", schema = "sub_track_plat")
public class User {
    private Long userId;
    private String username;
    private String password;
    private String realName;
    private String email;
    private String tel;
    private Long deptId;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;


}