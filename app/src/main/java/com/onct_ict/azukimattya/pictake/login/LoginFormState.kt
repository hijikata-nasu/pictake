package com.onct_ict.azukimattya.pictake.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val addressError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)
