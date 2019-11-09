package com.onct_ict.azukimattya.pictake.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onct_ict.azukimattya.pictake.data.LoginDataSource
import com.onct_ict.azukimattya.pictake.data.LoginRepository

class CreateViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateViewModel::class.java)) {
            return CreateViewModel(
                loginRepository = LoginRepository(
                    dataSource = LoginDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}