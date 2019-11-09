package com.onct_ict.azukimattya.pictake.data

import android.util.Log
import com.onct_ict.azukimattya.pictake.data.model.LoggedInUser
import com.onct_ict.azukimattya.pictake.login.Duplicate
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */

class LoginDataSource {
    fun login(res: String): Result<LoggedInUser> {
        when (res == "user not found") {
            true -> return Result.Error(IOException(res))
            false -> return Result.Success(LoggedInUser(res))
        }
    }

    fun create(duData: Duplicate): Result<LoggedInUser> {
        when (duData.res == "Duplicate") {
            true -> return Result.Error(IOException(duData.res))
            false -> return Result.Success(LoggedInUser(duData.res))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}

