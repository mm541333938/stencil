package com.kilig.module.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * mybatis配置类
 * @author L.Willian
 * @date 2020/1/20
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.kilig.module.mapper","com.kilig.module.dao"})
public class MyBatisConfig {
}
