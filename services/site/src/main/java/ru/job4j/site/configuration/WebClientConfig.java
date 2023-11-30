package ru.job4j.site.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import ru.job4j.site.handler.RestTemplateResponseErrorHandler;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final RestTemplateResponseErrorHandler errorHandler;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().errorHandler(errorHandler).build();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }
}
