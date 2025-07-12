package com.defectmanager.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MybatisPlusConfigTest {

    @Test
    void testMybatisPlusInterceptor() {
        // 创建配置实例
        MybatisPlusConfig config = new MybatisPlusConfig();
        
        // 获取拦截器
        MybatisPlusInterceptor interceptor = config.mybatisPlusInterceptor();
        
        // 验证拦截器不为空
        assertNotNull(interceptor);
        
        // 验证拦截器内部组件
        assertTrue(interceptor.getInterceptors().stream()
                .anyMatch(inner -> inner instanceof PaginationInnerInterceptor));
        
        assertTrue(interceptor.getInterceptors().stream()
                .anyMatch(inner -> inner instanceof OptimisticLockerInnerInterceptor));
        
        assertTrue(interceptor.getInterceptors().stream()
                .anyMatch(inner -> inner instanceof BlockAttackInnerInterceptor));
    }
} 