package com.dfpt.canonical.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI canonicalServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Canonical Service API")
                        .description("REST API for processing trades in JSON, XML, and CSV formats into canonical model")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DFTP Team")
                                .email("support@dftp.com")));
    }
}
