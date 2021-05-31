package com.badger.lolbyte.notification

data class NotificationResponse(val alert: String)

object NotificationHandler {
    fun getNotification(): NotificationResponse {
        // TODO: grab from config
        return NotificationResponse("nah")
    }
}
