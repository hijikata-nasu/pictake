package com.onct_ict.azukimattya.pictake.login

data class CreateResult (
    val success: LoggedInUserView? = null,
    val error: Int? = null
)