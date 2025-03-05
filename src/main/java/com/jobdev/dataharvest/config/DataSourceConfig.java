package com.jobdev.dataharvest.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class DataSourceConfig {

    // DataSource principal (APP_MAIN)
    @Primary
    @Bean
    @ConfigurationProperties("app.datasource.oracle")
    public DataSourceProperties mainDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    @ConfigurationProperties("app.datasource.oracle.main")
    public DataSource mainDataSource() {
        return mainDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    // DataSource batch (APP_BATCH)
    @Bean
    @ConfigurationProperties("app.datasource.oracle")
    public DataSourceProperties batchDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("batchDataSource")
    @BatchDataSource
    @ConfigurationProperties("app.datasource.oracle.batch")
    public DataSource batchDataSource() {
        return batchDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public DataSourceInitializer batchDataSourceInitializer(@Qualifier("batchDataSource") DataSource batchDataSource) {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(new ClassPathResource("org/springframework/batch/core/schema-oracle.sql"));
        databasePopulator.setContinueOnError(true);

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(batchDataSource);
        initializer.setDatabasePopulator(databasePopulator);
        return initializer;
    }
}