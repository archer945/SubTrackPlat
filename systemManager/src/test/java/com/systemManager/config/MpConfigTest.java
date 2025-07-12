package com.systemManager.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MpConfigTest {

    @Test
    void testMetaObjectHandler() {
        // 创建配置实例
        MpConfig mpConfig = new MpConfig();
        
        // 获取元对象处理器
        MetaObjectHandler handler = mpConfig.metaObjectHandler();
        
        // 验证不为空
        assertNotNull(handler);
    }
} 