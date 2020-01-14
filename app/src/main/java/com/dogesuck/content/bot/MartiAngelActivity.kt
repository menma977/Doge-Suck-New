package com.dogesuck.content.bot

import android.annotation.SuppressLint
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

class MartiAngelActivity : AppCompatActivity() {

    private lateinit var anyChartView: AnyChartView
    private lateinit var starterLinearLayout: LinearLayout
    private lateinit var username: TextView
    private lateinit var balanceTextView: TextView
    private lateinit var targetBalance: TextView
    private lateinit var multiPlay: EditText
    private lateinit var size: EditText
    private lateinit var probability: EditText
    private lateinit var stopLose: EditText
    private lateinit var target: EditText
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
    private lateinit var basePayIn: BigDecimal

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
        setContentView(R.layout.activity_marti_angel)

        username = findViewById(R.id.usernameTextView)
        balanceTextView = findViewById(R.id.balanceTextView)
        targetBalance = findViewById(R.id.targetTextView)
        probability = findViewById(R.id.probabilityEditText)
        probability.setText("50")
        multiPlay = findViewById(R.id.multiPlayEditText)
        multiPlay.setText("2")
        stopLose = findViewById(R.id.stopLoseEditText)
        stopLose.setText("1000")
        target = findViewById(R.id.targetEditText)
        target.setText("1")
        size = findViewById(R.id.sizeEditText)
        size.setText("0.1")
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
                        balanceTextView.text = floatBalance
                        targetBalance.text = targetBalanceValue.toString()
                        val jsonObject = JSONObject()
                        jsonObject.put(
                            "value",
                            formatDouble.format(balanceResponse.toDouble() * 0.00000001)
                        )
                        set.append(jsonObject.toString())
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
                    e.printStackTrace()
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
                Toast.makeText(this, "probability can not be empty", Toast.LENGTH_SHORT).show()
            } else if (!probability.text.isDigitsOnly()) {
                Toast.makeText(this, "probability must number only", Toast.LENGTH_SHORT).show()
            } else if (multiPlay.text.isEmpty()) {
                Toast.makeText(this, "multiPlay can not be empty", Toast.LENGTH_SHORT).show()
            } else if (!multiPlay.text.isDigitsOnly()) {
                Toast.makeText(this, "multiPlay must number only", Toast.LENGTH_SHORT).show()
            } else if (stopLose.text.isEmpty()) {
                Toast.makeText(this, "stopLose can not be empty", Toast.LENGTH_SHORT).show()
            } else if (!stopLose.text.isDigitsOnly()) {
                Toast.makeText(this, "stopLose must number only", Toast.LENGTH_SHORT).show()
            } else if (target.text.isEmpty()) {
                Toast.makeText(this, "target can not be empty", Toast.LENGTH_SHORT).show()
            } else if (!target.text.isDigitsOnly()) {
                Toast.makeText(this, "target must number only", Toast.LENGTH_SHORT).show()
            } else {
                basePayIn = size.text.toString().toBigDecimal()
                targetBalanceValue =
                    balanceTextView.text.toString().toDouble() + ((balanceTextView.text.toString().toDouble() * target.text.toString().toInt()) / 100)
                targetBalance.text = targetBalanceValue.toString()
                bot()
            }
        }

        stopButton.setOnClickListener {
            onStop = true
            onStopBotMode()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bot() {
        onBotMode()
        val loop = 30
        val session = user.sessionDoge
        val seed = format.format((0..99999).random())
        var lose = false
        var multiPlayValue = BigDecimal(multiPlay.text.toString())
        val height = BigDecimal(probability.text.toString()) * BigDecimal(10000)
        Timer().schedule(1000, 1000) {
            runOnUiThread {
                if (onStop) {
                    this.cancel()
                }
                if ((BigDecimal(balanceTextView.text.toString()) * BigDecimal(0.00000001))
                    < ((BigDecimal(balanceTextView.text.toString()) * BigDecimal(0.00000001))
                            - BigDecimal(stopLose.text.toString()))
                ) {
                    targetBalance.text = "Stop Lose"
                    onStopBotMode()
                } else {
                    if (lose) {
                        multiPlayValue *= BigDecimal(multiPlay.text.toString())
                        lose = false
                    } else {
                        multiPlayValue = BigDecimal(1)
                    }
                    val sendBasePayIn = (basePayIn * multiPlayValue) * BigDecimal(100000000)
                    response = BotController.ManualBot(
                        session,
                        height.toString(),
                        format.format(sendBasePayIn).toString(),
                        seed,
                        "0"
                    ).execute().get()
                    println(response)
                    try {
                        val payInResponse = (-basePayIn.toDouble())
                        val payOutResponse = response["PayOut"].toString().toDouble()
                        val profitResponse = payOutResponse + payInResponse
                        val balance = response["StartingBalance"].toString().toDouble()
                        val jsonObject = JSONObject()
                        jsonObject.put("value", (balance + profitResponse) * 0.00000001)
                        set.append(jsonObject.toString())
                        balanceTextView.text = formatLot.format((balance + profitResponse) * 0.00000001)
                        lose = profitResponse < 0
                        if (balanceTextView.text.toString().toBigDecimal() > targetBalanceValue.toBigDecimal()) {
                            this.cancel()
                            onStopBotMode()
                        }
                        if (row >= loop) {
                            set.remove(0)
                        }
                        row++
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(applicationContext, "Invalid request", Toast.LENGTH_SHORT).show()
                        this.cancel()
                        onStopBotMode()
                    }
                }
            }
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

    private fun onBotMode() {
        runOnUiThread {
            starterLinearLayout.visibility = View.INVISIBLE
            startButton.visibility = View.INVISIBLE
            probability.isEnabled = false
            multiPlay.isEnabled = false
            stopLose.isEnabled = false
            target.isEnabled = false
        }
    }

    private fun onStopBotMode() {
        runOnUiThread {
            starterLinearLayout.visibility = View.VISIBLE
            startButton.visibility = View.VISIBLE
            probability.isEnabled = true
            multiPlay.isEnabled = true
            stopLose.isEnabled = true
            target.isEnabled = true
        }
    }
}
