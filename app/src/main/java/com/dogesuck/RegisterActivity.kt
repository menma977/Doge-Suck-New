package com.dogesuck

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dogesuck.controller.RegisterController
import com.dogesuck.model.Loading
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

class RegisterActivity : AppCompatActivity() {

    private lateinit var sponsor: EditText
    private lateinit var term: CheckBox
    private lateinit var email: EditText
    private lateinit var name: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var register: Button
    private lateinit var loading: Loading
    private lateinit var data: HashMap<String, String>

    override fun onBackPressed() {
        super.onBackPressed()
        finishAndRemoveTask()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sponsor = findViewById(R.id.sponsorEditText)
        term = findViewById(R.id.termCheckBox)
        email = findViewById(R.id.emailEditText)
        name = findViewById(R.id.nameEditText)
        username = findViewById(R.id.usernameEditText)
        password = findViewById(R.id.passwordEditText)
        register = findViewById(R.id.registerButton)
        loading = Loading(this)
        loading.openDialog()

        Timer().schedule(1000) {
            loading.closeDialog()
        }

        register.setOnClickListener {
            if (term.isChecked) {
                data = HashMap()
                data["sponsor"] = sponsor.text.toString()
                data["term"] = "1"
                data["email"] = email.text.toString()
                data["name"] = name.text.toString()
                data["username"] = username.text.toString()
                data["password"] = password.text.toString()
                if (data["sponsor"].isNullOrEmpty()) {
                    Toast.makeText(this, "sponsor ${getString(R.string.empty)}", Toast.LENGTH_SHORT)
                        .show()
                } else if (data["term"].isNullOrEmpty()) {
                    Toast.makeText(this, "term ${getString(R.string.empty)}", Toast.LENGTH_SHORT)
                        .show()
                } else if (data["email"].isNullOrEmpty()) {
                    Toast.makeText(this, "email ${getString(R.string.empty)}", Toast.LENGTH_SHORT)
                        .show()
                } else if (!data["email"]!!.contains("@")) {
                    Toast.makeText(
                        this,
                        "Email input must have email characteristics",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (data["name"].isNullOrEmpty()) {
                    Toast.makeText(this, "name ${getString(R.string.empty)}", Toast.LENGTH_SHORT)
                        .show()
                } else if (data["username"].isNullOrEmpty()) {
                    Toast.makeText(
                        this,
                        "username ${getString(R.string.empty)}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (data["password"].isNullOrEmpty()) {
                    Toast.makeText(
                        this,
                        "password ${getString(R.string.empty)}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    loading.openDialog()
                    Timer().schedule(1000) {
                        runOnUiThread {
                            onRegister(data)
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You must check to agree with the rules", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun onRegister(dataBody: HashMap<String, String>) {
        val response = RegisterController.Post(dataBody).execute().get()
        println()
        if (response["Status"].toString() == (0).toString()) {
            Toast.makeText(
                this,
                "wait 1 to 2 minutes to make a connection to the provider, to register your account",
                Toast.LENGTH_LONG
            ).show()
            Timer().schedule(10000) {
                runOnUiThread {
                    loading.closeDialog()
                    finishAndRemoveTask()
                }
            }
        } else {
            try {
                Toast.makeText(
                    this,
                    getString(response["Pesan"].toString().toInt()),
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    response["Pesan"].toString().replace("<br>", "\n"),
                    Toast.LENGTH_SHORT
                ).show()
            }
            loading.closeDialog()
        }
    }
}
