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
                .title("InnoQueue API2")
                .description(
                    "Backend API documentation for the InnoQueue backend service.\n\n" +
                            "Check out [our github](https://github.com/InnoQueue) for more information."
                )
                .version("v1.0.0")
        )
}
