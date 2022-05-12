package com.innopolis.innoqueue

import com.innopolis.innoqueue.controller.DatabaseController
import com.innopolis.innoqueue.controller.NotificationsController
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import kotlin.concurrent.thread


@SpringBootApplication
class InnoQueueApplication

fun main(args: Array<String>) {
    val startupDelay: Long = 1 * 60 * 1000
    val resetDbTimeMinutes: Long = 60
    val resetDbTimeMillis: Long = resetDbTimeMinutes * 60 * 1000
    val clearInviteCodesTimeMinutes: Long = 30
    val clearInviteCodesTimeMillis: Long = clearInviteCodesTimeMinutes * 60 * 1000
    val clearNotificationsTimeHours: Long = 24
    val clearNotificationsTimeMillis: Long = clearNotificationsTimeHours * 60 * 60 * 1000
    val context = runApplication<InnoQueueApplication>(*args)
    thread(start = true) {
        Thread.sleep(startupDelay)
        while (true) {
            val db = context.getBean("databaseController") as DatabaseController
            try {
                db.resetDB()
            } catch (_: Exception) {
            }
            println("Database was reset")
            Thread.sleep(resetDbTimeMillis)
        }
    }
    thread(start = true) {
        Thread.sleep(startupDelay)
        while (true) {
            val db = context.getBean("databaseController") as DatabaseController
            try {
                db.clearCodes()
            } catch (_: Exception) {
            }
            println("Expired invite codes were deleted")
            Thread.sleep(clearInviteCodesTimeMillis)
        }
    }
    thread(start = true) {
        Thread.sleep(startupDelay)
        while (true) {
            val notificationsController = context.getBean("notificationsController") as NotificationsController
            try {
                notificationsController.clearOldNotifications()
            } catch (_: Exception) {
            }
            println("Old notifications were deleted")
            Thread.sleep(clearNotificationsTimeMillis)
        }
    }
}
