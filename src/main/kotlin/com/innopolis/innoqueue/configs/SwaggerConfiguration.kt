package com.innopolis.innoqueue.configs

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SwaggerConfiguration {
    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("InnoQueue API")
                .description("Swagger for the InnoQueue Backend\n\nCheck https://github.com/InnoQueue for more info")
                .version("v1.0.0")
        )
}
