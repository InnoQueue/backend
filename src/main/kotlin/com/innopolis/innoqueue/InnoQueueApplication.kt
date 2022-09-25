package com.innopolis.innoqueue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class InnoQueueApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<InnoQueueApplication>(*args)
}
