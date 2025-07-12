package com.systemManager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Knife4jConfigTest {

    @Test
    void testSystemManagerApi() {
        // 创建配置实例
        Knife4jConfig knife4jConfig = new Knife4jConfig();
        
        // 获取OpenAPI对象
        OpenAPI openAPI = knife4jConfig.systemManagerApi();
        
        // 验证不为空
        assertNotNull(openAPI);
        
        // 验证信息对象
        Info info = openAPI.getInfo();
        assertNotNull(info);
        
        // 验证标题、描述和版本
        assertEquals("系统管理模块接口文档", info.getTitle());
        assertEquals("用于系统管理模块前后端交互，提供一套API说明", info.getDescription());
        assertEquals("1.0.0", info.getVersion());
    }
} 