package com.systemManager.config;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest {

    @Test
    @Disabled("需要ServletContext环境，集成测试时再启用")
    void testCorsFilter() {
        // 创建配置实例
        CorsConfig corsConfig = new CorsConfig();
        
        // 获取CORS过滤器
        CorsFilter corsFilter = corsConfig.corsFilter();
        
        // 验证不为空
        assertNotNull(corsFilter);
    }
} 