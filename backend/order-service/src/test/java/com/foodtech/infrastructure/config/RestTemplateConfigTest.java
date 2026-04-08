package com.foodtech.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class RestTemplateConfigTest {

    private final RestTemplateConfig restTemplateConfig = new RestTemplateConfig();

    @Test
    void restTemplate_noEsNulo() {
        RestTemplate restTemplate = restTemplateConfig.restTemplate();

        assertThat(restTemplate).isNotNull();
    }

    @Test
    void restTemplate_retornaInstanciaDeRestTemplate() {
        RestTemplate restTemplate = restTemplateConfig.restTemplate();

        assertThat(restTemplate).isInstanceOf(RestTemplate.class);
    }
}
