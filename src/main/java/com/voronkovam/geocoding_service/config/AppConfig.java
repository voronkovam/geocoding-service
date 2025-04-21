package com.voronkovam.geocoding_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
/**
 * Конфигурационный класс Spring для определения бинов.
 */
@Configuration
public class AppConfig {
    /**
     * Создает и возвращает бин RestTemplate.
     *
     * @return новый экземпляр RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
