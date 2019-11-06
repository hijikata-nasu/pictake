package com.onct_ict.azukimattya.pictake

class RankingItems {
    var rankValue: Int? = null
    var userName: String? = null
    var scoreValue: Int? = null

    constructor(rankValue: Int, userName: String, scoreValue: Int) {
        this.rankValue = rankValue
        this.userName = userName
        this.scoreValue = scoreValue
    }
}