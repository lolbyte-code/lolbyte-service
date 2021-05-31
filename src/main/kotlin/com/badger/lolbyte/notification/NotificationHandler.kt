package com.badger.lolbyte.notification

data class NotificationResponse(val alert: String)

object NotificationHandler {
    fun getNotification(alert: String): NotificationResponse {
        return NotificationResponse(alert)
    }
}
