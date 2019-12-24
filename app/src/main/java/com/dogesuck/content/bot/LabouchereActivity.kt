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
import com.dogesuck.model.Config
import com.dogesuck.model.Loading
import com.dogesuck.model.User
import org.json.JSONObject
import java.lang.Exception
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class LabouchereActivity : AppCompatActivity() {

    private lateinit var anyChartView: AnyChartView
    private lateinit var arrayText: TextView
    private lateinit var starterLinearLayout: LinearLayout
    private lateinit var username: TextView
    private lateinit var balance: TextView
    private lateinit var targetBalance: TextView
    private lateinit var percent: EditText
    private lateinit var suck: Button
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
    private var win = false
    private lateinit var basePayInValue: BigDecimal
    private var targetBalanceValue = 0.0
    private lateinit var maxPayInValue: BigDecimal
    private var labour: ArrayList<Double> = ArrayList()
    private var payIn: Double = 0.0
    private var payInDefault: Double = 0.0

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
        setContentView(R.layout.activity_labouchere)

        loading = Loading(this)
        user = User(this)
        username = findViewById(R.id.usernameTextView)
        balance = findViewById(R.id.balanceTextView)
        targetBalance = findViewById(R.id.targetTextView)
        starterLinearLayout = findViewById(R.id.starterLinearLayout)
        percent = findViewById(R.id.percentEditText)
        suck = findViewById(R.id.StarterButton)
        stopButton = findViewById(R.id.StopButton)
        anyChartView = findViewById(R.id.chart)
        arrayText = findViewById(R.id.arrayTextView)
        cartesian = AnyChart.line()
        cartesian.background().fill("#000")
        configChart()
        anyChartView.setChart(cartesian)
        percent.setText("1")

        loading.openDialog()

        Timer().schedule(1000) {
            response =
                UserController.GetBalance(user.usernameDoge, user.passwordDoge).execute().get()
            runOnUiThread {
                try {
                    val balanceResponse = response.getJSONObject("Doge")["Balance"].toString()
                    if (balanceResponse.toDouble() > 0) {
                        val floatBalance = formatLot.format(balanceResponse.toDouble() * 0.00000001)
                        username.text = user.username
                        balance.text = floatBalance
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

        suck.setOnClickListener {
            onStop = false
            starterLinearLayout.visibility = View.INVISIBLE
            suck.visibility = View.INVISIBLE
            if (percent.text.isEmpty()) {
                Toast.makeText(this, "Percent can not be empty", Toast.LENGTH_SHORT).show()
            } else if (!percent.text.isDigitsOnly()) {
                Toast.makeText(this, "Percent must number only", Toast.LENGTH_SHORT).show()
            } else {
                payIn = Config().getLabor(balance.text.toString().toDouble() * 0.00000001)
                basePayInValue =
                    Config().getOnPercentValue((balance.text.toString().toDouble() * 0.00000001))
                        .toBigDecimal()
                targetBalanceValue =
                    balance.text.toString().toDouble() + ((balance.text.toString().toDouble() * percent.text.toString().toInt()) / 100)
                maxPayInValue = (basePayInValue * (1000).toBigDecimal())
                targetBalance.text = formatLot.format(targetBalanceValue)
                labour.add(payIn)
                bot()
            }
        }

        stopButton.setOnClickListener {
            onStop = true
            starterLinearLayout.visibility = View.VISIBLE
            suck.visibility = View.VISIBLE
        }
    }

    private fun bot() {
        val loop = 30
        val seed = format.format((0..99999).random())
        val session = user.sessionDoge
        payInDefault = payIn
        Timer().schedule(1000, 1000) {
            try {
                if (onStop) {
                    this.cancel()
                }
                runOnUiThread {
                    if (win) {
                        payIn = try {
                            labour.removeAt(0)
                            labour.removeAt(labour.size - 1)
                            labour[0] + labour[labour.size - 1]
                        } catch (e: Exception) {
                            payIn = payInDefault
                            labour.add(formatDouble.format(payIn).toDouble())
                            labour.add(formatDouble.format(payIn * 10).toDouble())
                            labour.add(formatDouble.format(payIn).toDouble())
                            labour[0] + labour[labour.size - 1]
                        }
                    } else {
                        payIn = labour[0] + labour[labour.size - 1]
                        labour.add(formatDouble.format(payIn).toDouble())
                    }
                    arrayText.text = labour.toString()
                    val pay = payIn * 100000000
                    response = BotController.ClassicBot(
                        session,
                        format.format(pay),
                        maxPayInValue.toString(),
                        seed,
                        "1"
                    ).execute().get()

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
                    win = responseProfit >= (0).toBigDecimal()

                    if (balance.text.toString().toBigDecimal() > targetBalanceValue.toBigDecimal()) {
                        this.cancel()
                        starterLinearLayout.visibility = View.VISIBLE
                        suck.visibility = View.VISIBLE
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
                println(e)
                this.cancel()
                runOnUiThread {
                    starterLinearLayout.visibility = View.VISIBLE
                    suck.visibility = View.VISIBLE
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
        series1.stroke("tomato")

        cartesian.legend().enabled(true)
        cartesian.legend().fontSize(13.0)
        cartesian.legend().padding(0.0)
    }
}
