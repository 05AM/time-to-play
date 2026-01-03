package org.example.batch.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ApiJdbcConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.api")
    public DataSourceProperties apiDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource apiDataSource(DataSourceProperties apiDataSourceProperties) {
        return apiDataSourceProperties
            .initializeDataSourceBuilder()
            .build();
    }

    @Primary
    @Bean
    public JdbcTemplate apiJdbcTemplate(
        @Qualifier("apiDataSource") DataSource ds
    ) {
        return new JdbcTemplate(ds);
    }

    @Primary
    @Bean
    public PlatformTransactionManager apiTxManager(
        @Qualifier("apiDataSource") DataSource ds
    ) {
        return new DataSourceTransactionManager(ds);
    }
}