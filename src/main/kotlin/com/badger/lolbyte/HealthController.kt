package com.badger.lolbyte

import com.badger.lolbyte.health.HealthHandler
import com.badger.lolbyte.health.HealthResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {
    @GetMapping("/health")
    fun getHealth(): HealthResponse {
        return HealthHandler.getHealth()
    }
}
