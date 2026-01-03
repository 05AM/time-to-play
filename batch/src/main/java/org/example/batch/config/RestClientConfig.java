package org.example.batch.config;

import org.example.batch.infra.client.psn.PsnProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @Qualifier("psnRestClient")
    public RestClient psnRestClient(RestClient.Builder builder, PsnProperties props) {
        return builder
            .baseUrl(props.baseUrl())
            .build();
    }
}