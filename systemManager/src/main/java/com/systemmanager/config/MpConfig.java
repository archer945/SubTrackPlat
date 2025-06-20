package com.systemmanager.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * <p>
 * 描述：注入MyBaits Plus插件与配置
 * </p>
 * @author yuyu
 * @version 1.0.0
 */
@Configuration
@ComponentScan("com.common.config")
public class MpConfig {
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                // 插入时填充
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                // 更新时填充
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

            }
        };
    }
}
