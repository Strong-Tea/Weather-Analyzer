package com.analyzer.weather.configure;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


/**
 * Configuration class for creating a RestTemplate bean.
 * RestTemplate is a Spring framework class that simplifies communication with RESTful web services.
 * This configuration provides a RestTemplate bean to be used throughout the application.
 *
 * The RestTemplate bean is created using the RestTemplateBuilder, which allows customization
 * and configuration of the RestTemplate instance.
 *
 * Usage:
 * - Autowire RestTemplate in other components or services where HTTP communication is required.
 *
 * Example:
 * ```
 * @Autowired
 * private RestTemplate restTemplate;
 * ```
 */
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

}
