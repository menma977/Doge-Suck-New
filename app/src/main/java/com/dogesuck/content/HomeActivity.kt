package com.dogesuck.content

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.dogesuck.LoginActivity
import com.dogesuck.R
import com.dogesuck.content.bot.*
import com.dogesuck.controller.UserController
import com.dogesuck.model.Loading
import com.dogesuck.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*
import kotlin.concurrent.schedule

class HomeActivity : AppCompatActivity() {

    /**
     * FAB
     */
    private lateinit var fab: FloatingActionButton

    private lateinit var getWalletTextView: TextView
    private lateinit var getWalletLinearLayout: LinearLayout
    private lateinit var fabGetWallet: FloatingActionButton

    private lateinit var withdrawTextView: TextView
    private lateinit var withdrawLinearLayout: LinearLayout
    private lateinit var fabWithdraw: FloatingActionButton

    /**
     * prepare Input
     */
    private lateinit var goTo: Intent
    private lateinit var response: JSONObject
    private lateinit var user: User
    private lateinit var loading: Loading
    private lateinit var logout: ImageButton
    private lateinit var refresh: Button
    private lateinit var usernameText: TextView
    private lateinit var levelText: TextView
    private lateinit var balanceText: TextView
    private lateinit var manual: Button
    private lateinit var labouchere: Button
    private lateinit var reverseLabouchere: Button
    private lateinit var fibonacci: Button
    private lateinit var flyFibonacci: Button
    private lateinit var classic: Button
    private lateinit var classic2: Button
    private lateinit var martiAngelCustom: Button
    private lateinit var custom: Button
    private lateinit var custom2: Button
    private var formatLot = DecimalFormat("#.#########")
    private var isFABOpen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        /**
         * fab
         */
        fab = findViewById(R.id.fab)

        getWalletLinearLayout = findViewById(R.id.getWalletLinearLayout)
        getWalletTextView = findViewById(R.id.getWalletTextView)
        fabGetWallet = findViewById(R.id.getWalletFAB)

        withdrawLinearLayout = findViewById(R.id.withdrawLinearLayout)
        withdrawTextView = findViewById(R.id.withdrawTextView)
        fabWithdraw = findViewById(R.id.withdrawFAB)

        /**
         * set preparation
         */
        user = User(this)
        loading = Loading(this)
        logout = findViewById(R.id.logoutButton)
        refresh = findViewById(R.id.refreshButton)
        usernameText = findViewById(R.id.usernameTextView)
        levelText = findViewById(R.id.typeTextView)
        balanceText = findViewById(R.id.balanceTextView)
        manual = findViewById(R.id.manualButton)
        labouchere = findViewById(R.id.labouchereButton)
        reverseLabouchere = findViewById(R.id.revreseLabouchereButton)
        fibonacci = findViewById(R.id.fibonacciButton)
        flyFibonacci = findViewById(R.id.flyFibonacciButton)
        classic = findViewById(R.id.classicButton)
        classic2 = findViewById(R.id.classic2Button)
        martiAngelCustom = findViewById(R.id.martiAngelCustomButton)
        custom = findViewById(R.id.customButton)
        custom2 = findViewById(R.id.custom2Button)
        loading.openDialog()

        fab.setOnClickListener {
            if (isFABOpen) {
                showFAB()
            } else {
                closeFAB()
            }
        }

        Timer().schedule(500) {
            runOnUiThread {
                getTypeUser()
            }
        }

        Timer().schedule(1000) {
            loading.closeDialog()
        }

        logout.setOnClickListener {
            goTo = Intent(this, LoginActivity::class.java)
            user.clear()
            finishAndRemoveTask()
            startActivity(goTo)
        }

        refresh.setOnClickListener {
            loading.openDialog()
            Timer().schedule(1000) {
                runOnUiThread {
                    getTypeUser()
                    getBalance()
                }
            }
        }

        manual.setOnClickListener {
            goTo = Intent(this, ManualBotActivity::class.java)
            startActivity(goTo)
        }

        classic.setOnClickListener {
            goTo = Intent(this, ClassicActivity::class.java)
            startActivity(goTo)
        }

        labouchere.setOnClickListener {
            goTo = Intent(this, LabouchereActivity::class.java)
            startActivity(goTo)
        }

        reverseLabouchere.setOnClickListener {
            goTo = Intent(this, ReverseLabouchereActivity::class.java)
            startActivity(goTo)
        }

        fibonacci.setOnClickListener {
            goTo = Intent(this, FibonacciActivity::class.java)
            startActivity(goTo)
        }
<<<<<<< HEAD

        flyFibonacci.setOnClickListener {
            //todo: Add flyFibonacci
        }
=======
>>>>>>> master
    }

    private fun getTypeUser() {
        try {
            response = UserController.GetType(user.username).execute().get()
            val levelUser = response["Status"].toString()
            usernameText.text = user.username
            levelText.text = if (levelUser == "0") "Unlimited" else levelUser

            if (levelUser.toInt() in 1..3) {
                reverseLabouchere.isEnabled = false
                fibonacci.isEnabled = false
                flyFibonacci.isEnabled = false
            }
            loading.closeDialog()
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "data failed to load, please refresh first",
                Toast.LENGTH_SHORT
            ).show()
            manual.isEnabled = false
            labouchere.isEnabled = false
            reverseLabouchere.isEnabled = false
            fibonacci.isEnabled = false
            flyFibonacci.isEnabled = false
            classic.isEnabled = false
            classic2.isEnabled = false
            martiAngelCustom.isEnabled = false
            custom.isEnabled = false
            custom2.isEnabled = false
            loading.closeDialog()
        }
    }

    private fun getBalance(): Boolean {
        return try {
            response =
                UserController.GetBalance(user.usernameDoge, user.passwordDoge).execute().get()
            balanceText.text =
                formatLot.format(response.getJSONObject("Doge")["Balance"].toString().toDouble() * 0.00000001)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun showFAB() {
        isFABOpen = false
        getWalletLinearLayout.animate().translationY(-resources.getDimension(R.dimen.standard_55))
        withdrawLinearLayout.animate().translationY(-resources.getDimension(R.dimen.standard_105))
        Timer().schedule(500) {
            runOnUiThread {
                getWalletTextView.visibility = View.VISIBLE
                withdrawTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun closeFAB() {
        isFABOpen = true
        getWalletLinearLayout.animate().translationY((0).toFloat())
        withdrawLinearLayout.animate().translationY((0).toFloat())

        getWalletTextView.visibility = View.INVISIBLE
        withdrawTextView.visibility = View.INVISIBLE
    }

    override fun onStart() {
        super.onStart()
        loading.openDialog()
        Timer().schedule(100) {
            runOnUiThread {
                if (!getBalance()) {
                    Toast.makeText(
                        applicationContext,
                        "data failed to load, please refresh first",
                        Toast.LENGTH_SHORT
                    ).show()
                    manual.isEnabled = false
                    labouchere.isEnabled = false
                    reverseLabouchere.isEnabled = false
                    fibonacci.isEnabled = false
                    flyFibonacci.isEnabled = false
                    classic.isEnabled = false
                    classic2.isEnabled = false
                    martiAngelCustom.isEnabled = false
                    custom.isEnabled = false
                    custom2.isEnabled = false
                }
                loading.closeDialog()
            }
        }
    }
}
