package com.badger.lolbyte.utils

enum class LaneRole(val lane: String, val role: String, val order: Int) {
    TOP("TOP", "", 1),
    JUNGLE("JUNGLE", "", 2),
    MIDDLE("MIDDLE", "", 3),
    MARKSMAN("BOTTOM", "DUO_CARRY", 4),
    SUPPORT("BOTTOM", "DUO_SUPPORT", 5);

    companion object {
        fun getOrder(lane: String, role: String): Int {
            return values().find {
                (it.lane == lane && lane != MARKSMAN.lane) || (it.lane == lane && it.role == role)
            }?.order ?: TOP.order
        }
    }
}
