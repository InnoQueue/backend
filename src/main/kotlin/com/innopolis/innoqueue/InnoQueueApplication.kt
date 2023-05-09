package com.innopolis.innoqueue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

/**
 * Spring Application entrypoint
 */
@SpringBootApplication
@EnableCaching
class InnoQueueApplication

/**
 * main entrypoint method
 */
fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<InnoQueueApplication>(*args)
}
