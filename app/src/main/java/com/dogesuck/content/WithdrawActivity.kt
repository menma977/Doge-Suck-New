package com.dogesuck.content

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dogesuck.R
import com.dogesuck.controller.UserController
import com.dogesuck.model.Loading
import com.dogesuck.model.User
import org.json.JSONObject
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

class WithdrawActivity : AppCompatActivity() {

    private lateinit var username: TextView
    private lateinit var balance: TextView
    private lateinit var wallet: TextView
    private lateinit var paste: Button
    private lateinit var nominal: EditText
    private lateinit var uniqueEditText: EditText
    private lateinit var uniqueCode: Button
    private lateinit var withdraw: Button
    private lateinit var response: JSONObject
    private lateinit var user: User
    private lateinit var loading: Loading
    private var formatLot = DecimalFormat("#.#########")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw)

        username = findViewById(R.id.usernameTextView)
        balance = findViewById(R.id.balanceTextView)
        wallet = findViewById(R.id.walletTextView)
        paste = findViewById(R.id.pasteButton)
        nominal = findViewById(R.id.nominalEditText)
        uniqueEditText = findViewById(R.id.uniqueCodeEditText)
        uniqueCode = findViewById(R.id.codeButton)
        withdraw = findViewById(R.id.withdrawButton)

        user = User(this)
        loading = Loading(this)

        Timer().schedule(100) {
            runOnUiThread {
                response = UserController.GetBalance(user.usernameDoge, user.passwordDoge).execute().get()
                val responseBalance =
                    formatLot.format(response.getJSONObject("Doge")["Balance"].toString().toDouble() * 0.00000001)
                balance.text = responseBalance
                username.text = user.username
                loading.closeDialog()
            }
        }

        paste.setOnClickListener {
            try {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                wallet.text = clipboard.primaryClip.getItemAt(0).text.toString()
            } catch (e: Exception) {
                Toast.makeText(this, "Don't have data to paste", Toast.LENGTH_SHORT).show()
            }
        }

        uniqueCode.setOnClickListener {
            withdrawal()
        }

        withdraw.setOnClickListener {
            finalWithdraw()
        }
    }

    private fun withdrawal() {
        when {
            username.text.isEmpty() -> {
                Toast.makeText(this, "Username is empty", Toast.LENGTH_SHORT).show()
            }
            nominal.text.isEmpty() -> {
                Toast.makeText(this, "Nominal can not be empty", Toast.LENGTH_SHORT).show()
            }
            nominal.text.toString().toBigDecimal() < BigDecimal(100) -> {
                Toast.makeText(this, "Nominal withdrawal cannot be less than 100", Toast.LENGTH_SHORT).show()
            }
            wallet.text.isEmpty() -> {
                Toast.makeText(this, "Wallet can not be empty", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Timer().schedule(100) {
                    val body = HashMap<String, String>()
                    body["a"] = "RequestWD"
                    body["username"] = username.text.toString()
                    body["nominal"] = nominal.text.toString()
                    body["wallet"] = wallet.text.toString()
                    response = UserController.RequestWithdraw(body).execute().get()
                    runOnUiThread {
                        if (response["Status"].toString() == (0).toString()) {
                            paste.isEnabled = false
                            nominal.isEnabled = false
                            uniqueCode.isEnabled = false
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Email delivery failed please repeat again",
                                Toast.LENGTH_SHORT
                            ).show()
                            paste.isEnabled = true
                            nominal.isEnabled = true
                            uniqueCode.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun finalWithdraw() {
        when {
            uniqueEditText.text.isEmpty() -> {
                Toast.makeText(this, "Code unique can not be empty", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Timer().schedule(100) {
                    val body = HashMap<String, String>()
                    body["a"] = "WDFinal"
                    body["username"] = username.text.toString()
                    body["nominal"] = nominal.text.toString()
                    body["wallet"] = wallet.text.toString()
                    body["kodeunik"] = uniqueEditText.text.toString()
                    response = UserController.RequestWithdraw(body).execute().get()
                    runOnUiThread {
                        if (response["Status"].toString() == (0).toString()) {
                            paste.isEnabled = true
                            nominal.isEnabled = true
                            uniqueCode.isEnabled = true
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Send data failed please repeat again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}
