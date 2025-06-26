package com.example.login.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("巡线平台接口文档")
            .version("1.0.0")
            .description("用于前后端联调的后端接口文档"));
    }
}