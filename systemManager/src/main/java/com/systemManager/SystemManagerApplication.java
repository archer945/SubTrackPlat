package com.systemManager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.systemManager.mapper")
public class SystemManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemManagerApplication.class, args);
    }

}
