package com.onct_ict.azukimattya.pictake

import android.text.GetChars

class IndexItems{
    var name: String? = null
    var image: Int? = null
    var getFlag: Boolean? = null

    constructor(name: String, image: Int, getFlag: Boolean) {
        this.name = name
        this.image = image
        this.getFlag = getFlag
    }
}