package com.example.springrestapidemo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig class
 *
 * @author : kjss920
 * @since : 2025-09-09, Tuesday
 **/
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Header Validation API")
                        .version("1.0")
                        .description("API that requires mandatory headers")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .components(new Components()
                        .addParameters("X-Auth-Token", new Parameter()
                                .name("X-Auth-Token")
                                .description("Auth Token")
                                .required(true)
                                .in("header"))
                        .addParameters("X-Request-Id", new Parameter()
                                .name("X-Request-Id")
                                .description("Request Identifier")
                                .required(true)
                                .in("header")));
    }

    /**
     * If you want global headers documented for all endpoints,
     * you can add reusable Parameters here in Components.
     * Do not define 2 OpenAPI beans in the same context. You might see error while starting the app.
     * Parameter 0 of method openAPIBuilder in org.springdoc.core.configuration.SpringDocConfiguration required a single bean, but 2 were found:
     * 	- customOpenAPI: defined by method 'customOpenAPI' in class path resource [com/example/springrestapidemo/config/OpenApiConfig.class]
     * 	- addGlobalHeaders: defined by method 'addGlobalHeaders' in class path resource [com/example/springrestapidemo/config/OpenApiConfig.class]
     *
     * This may be due to missing parameter name information
     *
     * Action:
     *
     * Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans, or using @Qualifier to identify the bean that should be consumed
     */
//    @Bean
//    public OpenAPI addGlobalHeaders() {
//        return new OpenAPI()
//                .components(new Components()
//                        .addParameters("X-Auth-Token", new Parameter()
//                                .name("X-Auth-Token")
//                                .description("Auth Token")
//                                .required(true)
//                                .in("header"))
//                        .addParameters("X-Request-Id", new Parameter()
//                                .name("X-Request-Id")
//                                .description("Request Identifier")
//                                .required(true)
//                                .in("header")));
//    }
}
