package com.badger.lolbyte.utils

enum class LaneRole(val lane: String, val role: String, val order: Int) {
    TOP("TOP", "", 1),
    JUNGLE("JUNGLE", "", 2),
    MIDDLE("MIDDLE", "", 3),
    MARKSMAN("BOTTOM", "DUO_CARRY", 4),
    SUPPORT("BOTTOM", "DUO_SUPPORT", 5);

    companion object {
        fun getOrder(lane: String, role: String): Int {
            for (l in values()) {
                if (l.lane == lane && lane != MARKSMAN.lane) {
                    return l.order
                } else {
                    if (l.lane == lane && l.role == role) {
                        return l.order
                    }
                }
            }
            return TOP.order
        }
    }
}
