package com.storage.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MasterDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "storage.datasource.master")
    public HikariConfig masterHikariConfig() {
        return new HikariConfig();
    }

    @Bean
    public HikariDataSource mainDataSource(@Qualifier("masterHikariConfig") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }
}
