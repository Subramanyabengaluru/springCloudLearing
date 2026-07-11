package com.example.commentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // @LoadBalanced makes this builder Eureka-aware: a URL like http://post-service
    // is resolved to a real instance address by Spring Cloud LoadBalancer.
    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient postServiceRestClient(RestClient.Builder loadBalancedRestClientBuilder,
                                            @Value("${post-service.url}") String postServiceUrl) {
        return loadBalancedRestClientBuilder
                .baseUrl(postServiceUrl)
                .build();
    }
}
