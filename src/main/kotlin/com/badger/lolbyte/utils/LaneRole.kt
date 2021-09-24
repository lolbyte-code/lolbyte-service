package com.badger.lolbyte.utils

enum class LaneRole(val lane: String, val role: String, val order: Int) {
    TOP("TOP", "SOLO", 1),
    JUNGLE("JUNGLE", "NONE", 2),
    MIDDLE("MID", "SOLO", 3),
    MARKSMAN("BOT", "CARRY", 4),
    SUPPORT("BOT", "SUPPORT", 5);

    companion object {
        fun getOrder(lane: String, role: String): Int {
            return values().find {
                (it.lane == lane && lane != MARKSMAN.lane) || (it.lane == lane && it.role == role)
            }?.order ?: TOP.order
        }
    }
}
