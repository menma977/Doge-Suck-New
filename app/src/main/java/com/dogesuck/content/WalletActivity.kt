package com.dogesuck.content

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.dogesuck.R
import com.dogesuck.controller.UserController
import com.dogesuck.model.Loading
import com.dogesuck.model.User
import org.json.JSONObject
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*
import kotlin.concurrent.schedule

class WalletActivity : AppCompatActivity() {

    private lateinit var username: TextView
    private lateinit var balance: TextView
    private lateinit var wallet: TextView
    private lateinit var copy: Button
    private lateinit var response: JSONObject
    private lateinit var user: User
    private lateinit var loading: Loading
    private var formatLot = DecimalFormat("#.#########")
    private var statusDeposit = false

    override fun onBackPressed() {
        super.onBackPressed()
        finishAndRemoveTask()
    }

    override fun onStop() {
        super.onStop()
        finishAndRemoveTask()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        username = findViewById(R.id.usernameTextView)
        balance = findViewById(R.id.balanceTextView)
        wallet = findViewById(R.id.walletTextView)
        copy = findViewById(R.id.copyButton)

        user = User(this)
        loading = Loading(this)

        loading.openDialog()

        Timer().schedule(100) {
            runOnUiThread {
                response = UserController.GetBalance(user.usernameDoge, user.passwordDoge).execute().get()
                val responseBalance =
                    formatLot.format(response.getJSONObject("Doge")["Balance"].toString().toDouble() * 0.00000001)
                balance.text = responseBalance
                response = UserController.GetWallet(user.username).execute().get()
                if (response["Status"].toString() == (0).toString()) {
                    username.text = user.username
                    statusDeposit = response["Status"].toString().toInt() == 0
                    wallet.text = response["WalletDeposit"].toString()
                    loading.closeDialog()
                } else {
                    Toast.makeText(applicationContext, "Response data not valid try again later", Toast.LENGTH_SHORT)
                        .show()
                    finishAndRemoveTask()
                    loading.closeDialog()
                }
            }
        }

        copy.setOnClickListener {
            val clipboard =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Wallet", wallet.text.toString())
            clipboard.primaryClip = clip
            Toast.makeText(this, "Your wallet has been copied", Toast.LENGTH_SHORT).show()
        }
    }
}
