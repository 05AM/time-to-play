package org.example.batch.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchJdbcConfig {

    @Bean
    @BatchDataSource
    @FlywayDataSource
    @ConfigurationProperties("spring.datasource.batch")
    public DataSourceProperties batchDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "dataSource")
    @BatchDataSource
    @FlywayDataSource
    public DataSource batchDataSource(
        @Qualifier("batchDataSourceProperties") DataSourceProperties props
    ) {
        return props.initializeDataSourceBuilder().build();
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager batchTxManager(
        @Qualifier("dataSource") DataSource ds
    ) {
        return new DataSourceTransactionManager(ds);
    }

    @Bean
    public JdbcTemplate batchJdbcTemplate(
        @Qualifier("dataSource") DataSource ds
    ) {
        return new JdbcTemplate(ds);
    }
}

