package com.innopolis.innoqueue

import com.innopolis.innoqueue.controller.DatabaseController
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import kotlin.concurrent.thread


@SpringBootApplication
class InnoQueueApplication

fun main(args: Array<String>) {
    val context = runApplication<InnoQueueApplication>(*args)
    thread(start = true) {
        while (true) {
            val db = context.getBean("databaseController") as DatabaseController
            try {
                db.resetDB()
            } catch (e: Exception) {

            }
            Thread.sleep(300_000)
        }
    }
}
