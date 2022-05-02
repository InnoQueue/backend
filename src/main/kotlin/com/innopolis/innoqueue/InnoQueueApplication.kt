package com.innopolis.innoqueue

import com.innopolis.innoqueue.controller.DatabaseController
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import kotlin.concurrent.thread


@SpringBootApplication
class InnoQueueApplication

fun main(args: Array<String>) {
    val startupDelay: Long = 1 * 60 * 1000
    val clearTimeMinutes: Long = 30
    val clearTimeMillis: Long = clearTimeMinutes * 60 * 1000
    val context = runApplication<InnoQueueApplication>(*args)
    thread(start = true) {
        Thread.sleep(startupDelay)
        while (true) {
            val db = context.getBean("databaseController") as DatabaseController
            try {
                db.clearCodes()
            } catch (_: Exception) {
            }
            println("Expired invite codes were deleted")
            Thread.sleep(clearTimeMillis)
        }
    }
}
