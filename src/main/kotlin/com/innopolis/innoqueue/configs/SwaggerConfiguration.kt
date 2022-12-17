package com.innopolis.innoqueue.configs

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * Bean configuration for the Swagger
 * It uses OpenAPI v3 standard
 */
@Configuration
class SwaggerConfiguration {
    /**
     * Bean provides OpenAPI custom configuration
     */
    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("InnoQueue Backend API")
                .description(
                    "Backend API documentation for the InnoQueue backend service.\n\n" +
                            "Check out the [GitHub repository](https://github.com/InnoQueue/Backend)" +
                            "for more information."
                )
                .version("v1.0.0")
        )
}
