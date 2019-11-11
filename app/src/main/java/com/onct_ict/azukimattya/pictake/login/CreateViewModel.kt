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

data class Register(
    var address: String,
    var password: String,
    var username: String
)

data class Duplicate(
    var res: String
)

class CreateViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    private val _createForm = MutableLiveData<CreateFormState>()
    val createFormState: LiveData<CreateFormState> = _createForm

    private val _createResult = MutableLiveData<CreateResult>()
    val createResult: LiveData<CreateResult> = _createResult

    suspend fun create(address: String, password: String, username: String) {

        val regAdapter: JsonAdapter<Register> = Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(Types.newParameterizedType(Register::class.java))
        val dupAdapter: JsonAdapter<Duplicate> = Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(Types.newParameterizedType(Duplicate::class.java))

        /*
        val registerJson = regAdapter.toJson(Register(address, password, username))
        val (_, _, re) = Fuel.post("/login/create").body(registerJson).awaitStringResponseResult()
        val (RE, _) = re
        val reData = regAdapter.fromJson(RE!!)
        */

        val duplicateJson = dupAdapter.toJson(Duplicate(address))
        val (_, _, df) = Fuel.post("/login/duplicate").body(duplicateJson).awaitStringResponseResult()
        val (DF, _) = df
        val duData = dupAdapter.fromJson(DF!!)
        Log.d(LoginActivity::class.java.simpleName, DF)


        val result = loginRepository.create(duData!!)
        if (result is Result.Success) {
            val registerJson = regAdapter.toJson(Register(address, password, username))
            val (_, _, _) = Fuel.post("/login/create").body(registerJson).awaitStringResponseResult()
            _createResult.value =
                CreateResult(success = LoggedInUserView(displayName = username))
        } else {
            _createResult.value = CreateResult(error = R.string.login_failed)
        }
    }

    fun createDataChanged(address: String, password: String, username: String) {
        if (!isAddressValid(address)) {
            _createForm.value = CreateFormState(addressError = R.string.invalid_address)
        } else if (!isPasswordValid(password)) {
            _createForm.value = CreateFormState(passwordError = R.string.invalid_password)
        } else if (!isUsernameValid(username)) {
            _createForm.value = CreateFormState(usernameError = R.string.invalid_address)
        } else {
            _createForm.value = CreateFormState(isDataValid = true)
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
        return username.length > 3
    }
}
