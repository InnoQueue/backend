package com.innopolis.innoqueue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class InnoQueueApplication

fun main(args: Array<String>) {
    val context = runApplication<InnoQueueApplication>(*args)
    /*
    thread(start = true) {
        while (true) {
            val db = context.getBean("databaseController") as DatabaseController
            try {
                db.resetDB()
            } catch (e: Exception) {

            }
            println("Database was reset")
            Thread.sleep(300_000)
        }
    }
     */
}
