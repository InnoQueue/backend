package com.innopolis.innoqueue.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter
import javax.servlet.http.HttpServletRequest

@Configuration
class RequestLoggingFilterConfig {
    @Bean
    fun logFilter(@Value("\${logging.maxPayloadLength}") maxPayloadLength: Int): CommonsRequestLoggingFilter =
        object : CommonsRequestLoggingFilter() {
            public override fun beforeRequest(request: HttpServletRequest, message: String) {
                // no-op
            }
        }.apply {
            setIncludeQueryString(true)
            setIncludePayload(true)
            setIncludeHeaders(false)
            setAfterMessagePrefix("Request: ")
            setAfterMessageSuffix("")
            setMaxPayloadLength(maxPayloadLength)
            setHeaderPredicate { it == "user-token" }
            setIncludeHeaders(true)
        }
}
