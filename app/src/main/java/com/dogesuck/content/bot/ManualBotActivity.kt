package com.dogesuck.content.bot

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
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
import java.text.DecimalFormat
import java.util.*
import kotlin.concurrent.schedule


class ManualBotActivity : AppCompatActivity() {

    private lateinit var anyChartView: AnyChartView
    private lateinit var cartesian: Cartesian
    private lateinit var bait: Button
    private lateinit var double: Button
    private lateinit var half: Button
    private lateinit var reset: Button
    private lateinit var set: Set
    private lateinit var usernameText: TextView
    private lateinit var balanceText: TextView
    private lateinit var lotEditText: EditText
    private lateinit var probabilityEditText: EditText
    private lateinit var loading: Loading
    private lateinit var user: User
    private lateinit var response: JSONObject
    private var format = DecimalFormat("#")
    private var formatLot = DecimalFormat("#.#########")
    private var row = 0
    private var probability = 50.0
    private var lot = 0.1

    override fun onBackPressed() {
        super.onBackPressed()
        finishAndRemoveTask()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_bot)

        loading = Loading(this)
        user = User(this)
        usernameText = findViewById(R.id.usernameTextView)
        balanceText = findViewById(R.id.balanceTextView)
        double = findViewById(R.id.doubleButton)
        half = findViewById(R.id.halfButton)
        reset = findViewById(R.id.resetButton)
        lotEditText = findViewById(R.id.lotEditText)
        probabilityEditText = findViewById(R.id.probabilityEditText)
        anyChartView = findViewById(R.id.chart)
        cartesian = AnyChart.line()
        cartesian.background().fill("#000")
        configChart()
        anyChartView.setChart(cartesian)
        bait = findViewById(R.id.baitButton)
        loading.openDialog()

        lotEditText.setText(lot.toString())
        probabilityEditText.setText(probability.toString())

        Timer().schedule(2000) {
            response =
                UserController.GetBalance(user.usernameDoge, user.passwordDoge).execute().get()
            if (response["Doge"].toString().length > 1) {
                runOnUiThread {
                    val balance = response.getJSONObject("Doge")["Balance"].toString()
                    if (balance.toFloat() > 0) {
                        val floatBalance = formatLot.format(balance.toFloat() * 0.00000001)
                        usernameText.text = user.username
                        balanceText.text = floatBalance.toString()
                        set.row(0, formatLot.format(balance.toFloat() * 0.00000001))
                    } else {
                        usernameText.text = user.username
                        balanceText.text = formatLot.format(0)
                        set.row(0, formatLot.format(0))
                        Toast.makeText(
                            applicationContext,
                            "your balance is not enough to do the bot",
                            Toast.LENGTH_LONG
                        ).show()
                        finishAndRemoveTask()
                    }
                    loading.closeDialog()
                }
            } else {
                runOnUiThread {
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

        double.setOnClickListener {
            lot *= 2
            lotEditText.setText(formatLot.format(lot))
        }

        half.setOnClickListener {
            lot /= 2
            lotEditText.setText(formatLot.format(lot))
        }

        reset.setOnClickListener {
            lot = 0.1
            lotEditText.setText(formatLot.format(lot))
        }

        bait.setOnClickListener {
            lotEditText.clearFocus()
            probabilityEditText.clearFocus()
            loading.openDialog()
            onBot()
        }

        lotEditText.doOnTextChanged { text, _, _, _ ->
            lot = try {
                text.toString().toDouble()
            } catch (e: Exception) {
                0.0
            }
        }

        probabilityEditText.doOnTextChanged { text, _, _, _ ->
            probability = try {
                text.toString().toDouble()
            } catch (e: Exception) {
                0.0
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

    private fun onBot() {
        val loop = 30
        val seed = format.format((0..99999).random())
        val basePlayIn = format.format((lot / 0.00000001))
        val high = format.format(probability * 10000)
        Timer().schedule(100) {
            response = BotController.ManualBot(
                user.sessionDoge,
                high.toString(),
                basePlayIn,
                seed.toString()
            ).execute().get()
            runOnUiThread {
                if (!response.toString().contains("error")) {
                    val payInResponse = (-basePlayIn.toDouble())
                    val payOutResponse = response["PayOut"].toString().toDouble()
                    val profitResponse = payOutResponse + payInResponse
                    val balance = response["StartingBalance"].toString().toDouble()
                    val jsonObject = JSONObject()
                    jsonObject.put("value", (balance + profitResponse) * 0.00000001)
                    set.append(jsonObject.toString())
                    balanceText.text = formatLot.format((balance + profitResponse) * 0.00000001)
                    if (row >= loop) {
                        set.remove(0)
                    }
                    row++
                    loading.closeDialog()
                } else {
                    Toast.makeText(applicationContext, "Invalid request", Toast.LENGTH_SHORT).show()
                    loading.closeDialog()
                }
            }
        }
    }
}
