package com.dogesuck.content.bot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.text.isDigitsOnly
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.TooltipPositionMode
import com.dogesuck.R
import com.dogesuck.controller.UserController
import com.dogesuck.controller.bot.BotController
import com.dogesuck.model.Loading
import com.dogesuck.model.User
import org.json.JSONObject
import java.lang.Exception
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*
import kotlin.concurrent.schedule

class CustomActivity : AppCompatActivity() {

    private lateinit var username: TextView
    private lateinit var balance: TextView
    private lateinit var targetBalance: TextView
    private lateinit var probability: EditText
    private lateinit var dfi: EditText
    private lateinit var size: EditText
    private lateinit var capacity: EditText
    private lateinit var stopLose: EditText
    private lateinit var target: EditText
    private lateinit var anyChartView: AnyChartView
    private lateinit var starterLinearLayout: LinearLayout
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var cartesian: Cartesian
    private lateinit var set: Set
    private lateinit var loading: Loading
    private lateinit var user: User
    private lateinit var response: JSONObject
    private var format = DecimalFormat("#")
    private var formatDouble = DecimalFormat("#.##")
    private var formatLot = DecimalFormat("#.#########")
    private var onStop = false
    private var row = 0
    private var targetBalanceValue = 0.0

    override fun onBackPressed() {
        super.onBackPressed()
        onStop = true
        row = 0
        finishAndRemoveTask()
    }

    override fun onStop() {
        super.onStop()
        onStop = true
        row = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom)

        username = findViewById(R.id.usernameTextView)
        balance = findViewById(R.id.balanceTextView)
        targetBalance = findViewById(R.id.targetTextView)
        probability = findViewById(R.id.probabilityEditText)
        probability.setText("50")
        dfi = findViewById(R.id.dfiEditText)
        dfi.setText("100")
        size = findViewById(R.id.sizeEditText)
        size.setText("0.1")
        capacity = findViewById(R.id.capacityEditText)
        capacity.setText("200")
        stopLose = findViewById(R.id.stopLoseEditText)
        stopLose.setText("100")
        target = findViewById(R.id.targetEditText)
        target.setText("1")
        startButton = findViewById(R.id.StarterButton)
        stopButton = findViewById(R.id.StopButton)
        starterLinearLayout = findViewById(R.id.starterLinearLayout)
        anyChartView = findViewById(R.id.chart)

        loading = Loading(this)
        user = User(this)

        cartesian = AnyChart.line()
        cartesian.background().fill("#000")
        configChart()
        anyChartView.setChart(cartesian)

        loading.openDialog()

        Timer().schedule(1000) {
            response = UserController.GetBalance(user.usernameDoge, user.passwordDoge).execute().get()
            runOnUiThread {
                try {
                    val balanceResponse = response.getJSONObject("Doge")["Balance"].toString()
                    if (balanceResponse.toDouble() > 0) {
                        val floatBalance = formatLot.format(balanceResponse.toDouble() * 0.00000001)
                        username.text = user.username
                        balance.text = floatBalance
                        targetBalance.text = targetBalanceValue.toString()
                        loading.closeDialog()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "your balance is not enough to do the bot",
                            Toast.LENGTH_LONG
                        ).show()
                        loading.closeDialog()
                        finishAndRemoveTask()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        applicationContext,
                        "Your connection has been lost. Please come back when the connection is stable",
                        Toast.LENGTH_LONG
                    ).show()
                    loading.closeDialog()
                    finishAndRemoveTask()
                }
            }
        }

        startButton.setOnClickListener {
            if (probability.text.isEmpty()) {
                Toast.makeText(this, "Probability can not be empty", Toast.LENGTH_SHORT).show()
            } else if (!probability.text.isDigitsOnly()) {
                Toast.makeText(this, "Probability must number only", Toast.LENGTH_SHORT).show()
            } else if (dfi.text.isEmpty()) {
                Toast.makeText(this, "Damnification Increase can not be empty", Toast.LENGTH_SHORT).show()
            } else if (!dfi.text.isDigitsOnly()) {
                Toast.makeText(this, "Damnification Increase must number only", Toast.LENGTH_SHORT).show()
            } else if (size.text.isEmpty()) {
                Toast.makeText(this, "Size can not be empty", Toast.LENGTH_SHORT).show()
            } else if (capacity.text.isEmpty()) {
                Toast.makeText(this, "Capacity can not be empty", Toast.LENGTH_SHORT).show()
            } else if (!capacity.text.isDigitsOnly()) {
                Toast.makeText(this, "Capacity must number only", Toast.LENGTH_SHORT).show()
            } else if (stopLose.text.isEmpty()) {
                Toast.makeText(this, "StopLose can not be empty", Toast.LENGTH_SHORT).show()
            } else if (!stopLose.text.isDigitsOnly()) {
                Toast.makeText(this, "StopLose must number only", Toast.LENGTH_SHORT).show()
            } else if (target.text.isEmpty()) {
                Toast.makeText(this, "Target can not be empty", Toast.LENGTH_SHORT).show()
            } else if (!target.text.isDigitsOnly()) {
                Toast.makeText(this, "Target must number only", Toast.LENGTH_SHORT).show()
            } else {
                targetBalanceValue =
                    balance.text.toString().toDouble() + ((balance.text.toString().toDouble() * target.text.toString().toDouble()) / 100)
                targetBalance.text = targetBalanceValue.toString()
                onBot()
            }
        }

        stopButton.setOnClickListener {
            anyChartView.requestFocus()
            username.requestFocus()
            onStop = true
            onStopBotMode()
        }
    }

    private fun configChart() {
        cartesian.crosshair().enabled(true)
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

        set = Set.instantiate()
        val series1Mapping: Mapping = set.mapAs("{ value: 'value' }")

        val series1: Line = cartesian.line(series1Mapping)
        series1.name("Balance")
        series1.hovered().markers().enabled(true)
        series1.stroke("#DD0A0A")

        cartesian.legend().enabled(true)
        cartesian.legend().fontSize(13.0)
        cartesian.legend().padding(0.0)
    }

    private fun onBot() {
        onBotMode()
        val loop = 30
        val seed = format.format((0..99999).random())
        val session = user.sessionDoge
        var basePayIn = format.format(BigDecimal(size.text.toString()) * BigDecimal(100000000)).toBigDecimal()
        val basePayInMirror = basePayIn
        val increaseOnLosePercent = BigDecimal(dfi.text.toString()) * BigDecimal(0.01)
        val maxBet = BigDecimal(capacity.text.toString())
        val probability = BigDecimal(probability.text.toString()) * BigDecimal(10000)
        var maxPayIn = BigDecimal(stopLose.text.toString() + "00000000")
        val maxPayInMirror = maxPayIn
        var lose = 0

        Timer().schedule(1000, 1000) {
            if (onStop) {
                this.cancel()
                onStopBotMode()
            }
            runOnUiThread {
                try {
                    if (lose > 0) {
                        basePayIn *= BigDecimal(10)
                        maxPayIn *= BigDecimal(10)
                        lose--
                    } else {
                        basePayIn = basePayInMirror
                        maxPayIn = maxPayInMirror
                    }
                    if (balance.text.toString().toBigDecimal() > targetBalanceValue.toBigDecimal()) {
                        this.cancel()
                        row = 0
                        onStopBotMode()
                    }
                    response = BotController.CustomBot(
                        session,
                        format.format(basePayIn).toString(),
                        maxPayIn.toString(),
                        seed,
                        maxBet.toString(),
                        "0",
                        probability.toString(),
                        formatDouble.format(increaseOnLosePercent).toString()
                    ).execute().get()
                    println(response)
                    val responsePayIn = response["PayIn"].toString().toBigDecimal()
                    val responsePayOut = response["PayOut"].toString().toBigDecimal()
                    val responseProfit = (responsePayOut + responsePayIn)
                    val responseBalance =
                        balance.text.toString().toBigDecimal() + (responseProfit * (0.00000001).toBigDecimal())
                    val jsonObject = JSONObject()
                    jsonObject.put(
                        "value",
                        formatDouble.format(responseBalance)
                    )
                    set.append(jsonObject.toString())
                    balance.text = formatLot.format(responseBalance)
                    when {
                        responseProfit < (0).toBigDecimal() -> {
                            lose = 2
                        }
                    }
                    when {
                        row > loop -> {
                            set.remove(0)
                        }
                    }
                    row++
                } catch (e: Exception) {
                    e.printStackTrace()
                    this.cancel()
                    onStopBotMode()
                }
            }
        }
    }

    private fun onBotMode() {
        runOnUiThread {
            starterLinearLayout.visibility = View.INVISIBLE
            startButton.visibility = View.INVISIBLE
            probability.isEnabled = false
            dfi.isEnabled = false
            size.isEnabled = false
            capacity.isEnabled = false
            stopLose.isEnabled = false
            target.isEnabled = false
        }
    }

    private fun onStopBotMode() {
        runOnUiThread {
            starterLinearLayout.visibility = View.VISIBLE
            startButton.visibility = View.VISIBLE
            probability.isEnabled = true
            dfi.isEnabled = true
            size.isEnabled = true
            capacity.isEnabled = true
            stopLose.isEnabled = true
            target.isEnabled = true
        }
    }
}