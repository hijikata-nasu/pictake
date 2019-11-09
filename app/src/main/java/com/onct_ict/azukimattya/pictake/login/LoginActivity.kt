package com.onct_ict.azukimattya.pictake.login

import android.app.Activity
import androidx.lifecycle.*
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.*
import android.view.inputmethod.EditorInfo
import android.widget.*

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.coroutines.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.View
import com.onct_ict.azukimattya.pictake.LoginViewModelFactory
import com.onct_ict.azukimattya.pictake.R
import com.onct_ict.azukimattya.pictake.MainActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var createViewModel: CreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        createViewModel = ViewModelProviders.of(this, CreateViewModelFactory())
            .get(CreateViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.addressError != null) {
                address.error = getString(loginState.addressError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        createViewModel.createFormState.observe(this@LoginActivity, Observer {
            val createState = it ?: return@Observer

            // disable login button unless both username / password is valid
            create.isEnabled = createState.isDataValid

            if (createState.addressError != null) {
                caddress.error = getString(createState.addressError)
            }
            if (createState.passwordError != null) {
                cpassword.error = getString(createState.passwordError)
            }
            if (createState.usernameError != null) {
                cusername.error = getString(createState.usernameError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            //finish()
        })

        createViewModel.createResult.observe(this@LoginActivity, Observer {
            val createResult = it ?: return@Observer

            cloading.visibility = View.GONE
            if (createResult.error != null) {
                showLoginFailed(createResult.error)
            }
            if (createResult.success != null) {
                updateUiWithUser_create(createResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            //finish()
        })

        address.afterTextChanged {
            loginViewModel.loginDataChanged(
                address.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    address.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                            loginViewModel.login(address.text.toString(), password.text.toString())
                        }
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                    loginViewModel.login(address.text.toString(), password.text.toString())
                }
            }
        }
        swap_button.setOnClickListener {
            setContentView(R.layout.activity_create)
            caddress.afterTextChanged {
                createViewModel.createDataChanged(
                    caddress.text.toString(),
                    cpassword.text.toString(),
                    cusername.text.toString()
                )
            }
            cusername.afterTextChanged {
                createViewModel.createDataChanged(
                    caddress.text.toString(),
                    cpassword.text.toString(),
                    cusername.text.toString()
                )
            }
            cpassword.apply {
                afterTextChanged {
                    createViewModel.createDataChanged(
                        caddress.text.toString(),
                        cpassword.text.toString(),
                        cusername.text.toString()
                    )
                }

                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE ->
                            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                                createViewModel.create(caddress.text.toString(), cpassword.text.toString(), cusername.text.toString())
                            }
                    }
                    false
                }

                create.setOnClickListener {
                    cloading.visibility = View.VISIBLE
                    GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                        createViewModel.create(caddress.text.toString(), cpassword.text.toString(), cusername.text.toString())
                    }
                }
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        var intent = Intent(this, MainActivity::class.java)
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
        startActivity(intent)
    }

    private fun updateUiWithUser_create(model: LoggedInUserView) {
        val create = getString(R.string.create)
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            create,
            Toast.LENGTH_LONG
        ).show()
        //再起動
        val intent = intent
        val appStarter =
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), appStarter)
        finish()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
