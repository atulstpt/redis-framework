package com.redis.redisinterface.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Redis Framework API")
                        .description("Your API Description")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Atul Satpute")
                                .email("atulstpt@hotmail.com")));
    }

}
