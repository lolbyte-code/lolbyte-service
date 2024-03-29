package com.badger.lolbyte.utils

enum class Queue(val tag: String, val id: Int) {
    UNKNOWN("Unknown", -1),
    CUSTOM("Custom", 0),
    RANKED_PREMADE_5x5("Ranked Premade 5v5", 6),
    RANKED_TEAM_3x3("Ranked Team 3v3", 41),
    RANKED_TEAM_5x5("Ranked Team 5v5", 42),
    ODIN_5x5_BLIND("Dominion 5v5 Blind", 16),
    ODIN_5x5_DRAFT("Dominion 5v5 Draft", 17),
    BOT_5x5("Coop vs AI", 7),
    BOT_ODIN_5x5("Dominion Coop vs AI", 25),
    GROUP_FINDER_5x5("Team Manager", 61),
    ONEFORALL_5x5("One for All", 70),
    FIRSTBLOOD_1x1("Snowdown Showdown 1v1", 72),
    FIRSTBLOOD_2x2("Snowdown Showdown 2v2", 73),
    SR_6x6("Summoner's Rift 6v6 Hexakill", 75),
    URF_5x5("Ultra Rapid Fire", 76),
    ONEFORALL_MIRRORMODE_5x5("One for All", 78),
    BOT_URF_5x5("Ultra Rapid Fire vs AI", 83),
    NIGHTMARE_BOT_5x5_RANK1("Doom Bots Rank 1", 91),
    NIGHTMARE_BOT_5x5_RANK2("Doom Bots Rank 2", 92),
    NIGHTMARE_BOT_5x5_RANK5("Doom Bots Rank 5", 93),
    ASCENSION_5x5("Ascension", 96),
    HEXAKILL("Twisted Treeline 6v6 Hexakill", 98),
    BILGEWATER_ARAM_5x5("Butcher's Bridge", 100),
    KING_PORO_5x5("King Poro", 300),
    COUNTER_PICK("Nemesis", 310),
    BILGEWATER_5x5("Black Market Brawlers", 313),
    DEFINITELY_NOT_DOMINION_5x5("Definitely Not Dominion", 317),
    ARURF_5X5("AR Ultra Rapid Fire", 318),
    ARSR_5x5("ARSR", 325),
    TEAM_BUILDER_DRAFT_UNRANKED_5x5("Unranked Dynamic Queue", 400),
    TEAM_BUILDER_DRAFT_RANKED_5x5("Ranked Dynamic Queue", 410),
    TEAM_BUILDER_RANKED_SOLO("Ranked Solo/Duo Queue", 420),
    RANKED_SOLO_5x5("Ranked Solo/Duo Queue", 420),
    NORMAL_5x5_BLIND("Normal 5v5 Blind", 430),
    RANKED_FLEX_SR("Ranked Flex Queue 5v5", 440),
    ARAM_5x5("ARAM", 450),
    NORMAL_3x3("Normal 3v3", 460),
    RANKED_PREMADE_3x3("Ranked Flex Queue 3v3", 470),
    RANKED_FLEX_TT("Ranked Flex Queue 3v3", 470),
    ASSASSINATE_5x5("Blood Moon", 600),
    DARKSTAR_3x3("Dark Star 3v3", 610),
    CLASH("Clash", 700),
    BOT_TT_3x3("Coop vs AI Twisted Treeline", 800),
    BOT_5x5_INTRO("Coop vs AI Intro", 830),
    BOT_5x5_BEGINNER("Coop vs AI Beginner", 840),
    BOT_5x5_INTERMEDIATE("Coop vs AI Intermediate", 850),
    URF("AR Ultra Rapid Fire", 900),
    SIEGE("Nexus Siege", 940),
    INVASION_NORMAL("Invasion: Normal", 980),
    INVASION("Invasion: Onslaught", 990),
    OVERCHARGE("Overcharge", 1000),
    SNOWURF("Snow Battle ARURF", 1010),
    ONE_FOR_ALL("One For All", 1020),
    ODYSSEY_INTRO("Odyssey: Intro", 1030),
    ODYSSEY_CADET("Odyssey: Cadet", 1040),
    ODYSSEY_CREWMEMBER("Odyssey: Crewmember", 1050),
    ODYSSEY_CAPTAIN("Odyssey: Captain", 1060),
    ODYSSEY_ONSLAUGHT("Odyssey: Onslaught", 1070),
    RANKED_TFT("Ranked TFT", 1100),
    RANKED_TFT_DOUBLE_UP("Ranked TFT Double Up", 1160),
    NEXUS_BLITZ_BETA("Nexus Blitz (Beta)", 1200),
    NEXUS_BLITZ("Nexus Blitz", 1300),
    ULTBOOK("Ultimate Spellbook", 1400);

    companion object {
        fun getTag(id: Int?): String {
            return values().firstOrNull { it.id == id }?.tag ?: UNKNOWN.tag
        }

        fun getQueue(name: String): Queue {
            return values().firstOrNull { it.name == name } ?: UNKNOWN
        }
    }
}
