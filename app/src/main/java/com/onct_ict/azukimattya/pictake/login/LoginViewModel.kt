package com.onct_ict.azukimattya.pictake.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.onct_ict.azukimattya.pictake.data.Result

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.onct_ict.azukimattya.pictake.R
import com.onct_ict.azukimattya.pictake.data.LoginRepository
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

data class PostData(
    var address: String,
    var password: String
)

data class Res(
    var res: String
)



class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    suspend fun login(address: String, password: String) {
        FuelManager.instance.apply {
            basePath = "http://192.168.43.230:8080"
        }

        val postAdapter: JsonAdapter<PostData> = Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(Types.newParameterizedType(PostData::class.java))
        val getAdapter: JsonAdapter<Res> = Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(Types.newParameterizedType(Res::class.java))

        val data = postAdapter.toJson(PostData(address, password))
        val (_, _, res) = Fuel.post("/login/check").body(data).awaitStringResponseResult()
        val (_res, _) = res
        val __res = getAdapter.fromJson(_res!!)

        val result = loginRepository.login(__res!!.res)
        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName), address = address)
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(address: String, password: String) {
        if (!isAddressValid(address)) {
            _loginForm.value = LoginFormState(addressError = R.string.invalid_address)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isAddressValid(address: String): Boolean {
        return if (address.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(address).matches()
        } else {
            address.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isUsernameValid(username: String): Boolean {
        return username.length > 4
    }
}
