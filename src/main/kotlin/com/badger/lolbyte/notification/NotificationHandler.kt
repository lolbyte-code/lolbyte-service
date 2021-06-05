package com.badger.lolbyte.notification

import com.badger.lolbyte.NotFoundException

data class NotificationResponse(val alert: String)

object NotificationHandler {
    fun getNotification(alert: String): NotificationResponse {
        if (alert == "") throw NotFoundException
        return NotificationResponse(alert)
    }
}
