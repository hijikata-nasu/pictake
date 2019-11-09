package com.onct_ict.azukimattya.pictake.login

data class CreateFormState(
    val addressError: Int? = null,
    val passwordError: Int? = null,
    val usernameError: Int? = null,
    val isDataValid: Boolean = false
)