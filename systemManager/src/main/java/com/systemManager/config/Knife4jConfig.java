package com.systemManager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {
    
    @Bean
    public OpenAPI systemManagerApi() {
        return new OpenAPI().info(new Info()
                .title("系统管理模块接口文档")
                .description("用于系统管理模块前后端交互，提供一套API说明")
                .version("1.0.0")
        );
    }
}