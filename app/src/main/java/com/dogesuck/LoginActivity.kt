package com.dogesuck

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.dogesuck.content.HomeActivity
import com.dogesuck.controller.LoginController
import com.dogesuck.model.Loading
import com.dogesuck.model.User
import com.google.gson.Gson
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule

class LoginActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var versions: TextView
    private lateinit var login: Button
    private lateinit var register: Button
    private lateinit var loading: Loading
    private lateinit var response: JSONObject
    private lateinit var goTo: Intent
    private lateinit var userModel: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        username = findViewById(R.id.username) //PORWANTO
        password = findViewById(R.id.password) //1234
        versions = findViewById(R.id.version)
        login = findViewById(R.id.login)
        register = findViewById(R.id.register)
        userModel = User(this)
        loading = Loading(this)
        loading.openDialog()

        val versionConverter = getString(R.string.version) + " " + BuildConfig.VERSION_NAME

        versions.text = versionConverter

        Timer().schedule(1000) {
            loading.closeDialog()
            println(userModel.sessionDoge)

            if (!userModel.sessionDoge.isNullOrEmpty()) {
                goTo = Intent(applicationContext, HomeActivity::class.java)
                finish()
                startActivity(goTo)
            }
        }

        login.setOnClickListener {
            loading.openDialog()
            Timer().schedule(1000) {
                runOnUiThread {
                    loginDogeSuck()
                }
            }
        }

        register.setOnClickListener {
            goTo = Intent(this, RegisterActivity::class.java)
            startActivity(goTo)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun loginDogeSuck() {
        userModel.username = username.text.toString().toUpperCase()
        userModel.password = password.text.toString()
        when {
            userModel.username.isNullOrEmpty() -> {
                Toast.makeText(
                    applicationContext,
                    "Username" + getString(R.string.empty),
                    Toast.LENGTH_SHORT
                )
                    .show()
                loading.closeDialog()
            }
            userModel.password.isNullOrEmpty() -> {
                Toast.makeText(
                    applicationContext,
                    "Password" + getString(R.string.empty),
                    Toast.LENGTH_SHORT
                )
                    .show()
                loading.closeDialog()
            }
            else -> {
                response =
                    LoginController.Login(userModel.username, userModel.password).execute().get()
                Timer().schedule(1000) {
                    runOnUiThread {
                        if (response["Status"] == (0).toString()) {
                            userModel.usernameDoge = response["userdoge"].toString()
                            userModel.passwordDoge = response["passdoge"].toString()
                            login999Doge()
                        } else {
                            try {
                                Toast.makeText(
                                    applicationContext,
                                    getString(response["Pesan"].toString().toInt()),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    applicationContext,
                                    response["Pesan"].toString().replace("<br>", "\n"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            loading.closeDialog()
                        }
                    }
                }
            }
        }
    }

    private fun login999Doge() {
        response =
            LoginController.LoginDoge(userModel.usernameDoge, userModel.passwordDoge).execute()
                .get()
        Timer().schedule(1000) {
            runOnUiThread {
                try {
                    userModel.sessionDoge = response["SessionCookie"].toString()
                    val googleJson = Gson().toJson(userModel)
                    userModel.save(googleJson)
                    Timer().schedule(1000) {
                        goTo = Intent(applicationContext, HomeActivity::class.java)
                        loading.closeDialog()
                        finish()
                        startActivity(goTo)
                    }
                } catch (e: Exception) {
                    try {
                        Toast.makeText(
                            applicationContext,
                            getString(response["Pesan"].toString().toInt()),
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            applicationContext,
                            response["Pesan"].toString().replace("<br>", "\n"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    loading.closeDialog()
                }
            }
        }
    }
}
