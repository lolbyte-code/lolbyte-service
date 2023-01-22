package com.badger.lolbyte.client

import com.badger.lolbyte.rank.RankResponse

interface TFTApiClient {
    fun getTFTRanks(name: String): List<RankResponse>
}
