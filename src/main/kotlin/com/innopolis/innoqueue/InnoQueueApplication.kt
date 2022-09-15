package com.innopolis.innoqueue

import com.innopolis.innoqueue.controller.DatabaseController
import com.innopolis.innoqueue.controller.NotificationsController
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import kotlin.concurrent.thread

private const val STARTUP_DELAY: Long = 1 * 60 * 1000
private const val RESET_DB_TIME_MINUTES: Long = 60
private const val RESET_DB_TIME_MILLIS: Long = RESET_DB_TIME_MINUTES * 60 * 1000
private const val CLEAR_INVITE_CODES_TIME_MINUTES: Long = 30
private const val CLEAR_INVITE_CODES_TIME_MILLIS: Long = CLEAR_INVITE_CODES_TIME_MINUTES * 60 * 1000
private const val CLEAR_NOTIFICATIONS_TIME_HOURS: Long = 24
private const val CLEAR_NOTIFICATIONS_TIME_MILLIS: Long = CLEAR_NOTIFICATIONS_TIME_HOURS * 60 * 60 * 1000

@SpringBootApplication
class InnoQueueApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    val context = runApplication<InnoQueueApplication>(*args)
    thread(start = true) {
        Thread.sleep(STARTUP_DELAY)
        while (true) {
            val db = context.getBean("databaseController") as DatabaseController
            try {
                db.resetDB()
            } catch (_: Exception) {
            }
            println("Database was reset")
            Thread.sleep(RESET_DB_TIME_MILLIS)
        }
    }
    thread(start = true) {
        Thread.sleep(STARTUP_DELAY)
        while (true) {
            val db = context.getBean("databaseController") as DatabaseController
            try {
                db.clearCodes()
            } catch (_: Exception) {
            }
            println("Expired invite codes were deleted")
            Thread.sleep(CLEAR_INVITE_CODES_TIME_MILLIS)
        }
    }
    thread(start = true) {
        Thread.sleep(STARTUP_DELAY)
        while (true) {
            val notificationsController = context.getBean("notificationsController") as NotificationsController
            try {
                notificationsController.clearOldNotifications()
            } catch (_: Exception) {
            }
            println("Old notifications were deleted")
            Thread.sleep(CLEAR_NOTIFICATIONS_TIME_MILLIS)
        }
    }
}
