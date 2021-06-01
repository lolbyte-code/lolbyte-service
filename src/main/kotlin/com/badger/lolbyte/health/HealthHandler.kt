package com.badger.lolbyte.health

data class HealthResponse(val healthy: Boolean)

object HealthHandler {
    fun getHealth(): HealthResponse {
        return HealthResponse(true)
    }
}
