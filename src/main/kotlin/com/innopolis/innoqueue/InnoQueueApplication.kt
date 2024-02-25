package com.innopolis.innoqueue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Spring Application entrypoint
 */
@SpringBootApplication
class InnoQueueApplication

/**
 * main entrypoint method test
 */
fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<InnoQueueApplication>(*args)
}
