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

class FibonacciActivity : AppCompatActivity() {

    private lateinit var anyChartView: AnyChartView
    private lateinit var arrayText: TextView
    private lateinit var username: TextView
    private lateinit var balance: TextView
    private lateinit var targetBalance: TextView
    private lateinit var starterLinearLayout: LinearLayout
    private lateinit var percent: EditText
    private lateinit var seekBar: SeekBar
    private lateinit var descriptionSeekBarTextView: TextView
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
    private lateinit var formatLevelOfCourage: String
    private var levelOfCourageValue = 1
    private lateinit var basePayInValue: BigDecimal
    private var targetBalanceValue = 0.0
    private lateinit var maxPayInValue: BigDecimal
    private var fibonacciArray = ArrayList<Int>()

    override fun onBackPressed() {
        super.onBackPressed()
        stopBot()
        row = 0
        finishAndRemoveTask()
    }

    override fun onStop() {
        super.onStop()
        stopBot()
        row = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fibonacci)

        loading = Loading(this)
        user = User(this)
        anyChartView = findViewById(R.id.chart)
        arrayText = findViewById(R.id.arrayTextView)
        username = findViewById(R.id.usernameTextView)
        balance = findViewById(R.id.balanceTextView)
        targetBalance = findViewById(R.id.targetTextView)
        starterLinearLayout = findViewById(R.id.starterLinearLayout)
        percent = findViewById(R.id.percentEditText)
        seekBar = findViewById(R.id.seekBar)
        descriptionSeekBarTextView = findViewById(R.id.DescriptionSeekBarTextView)
        suck = findViewById(R.id.StarterButton)
        stopButton = findViewById(R.id.StopButton)

        setValueFibonacci()

        formatLevelOfCourage =
            "${getString(R.string.level_of_courage)} : ${(seekBar.progress.toString().toInt() + 1)}%"
        descriptionSeekBarTextView.text = formatLevelOfCourage
        percent.setText("1")
        cartesian = AnyChart.line()
        cartesian.background().fill("#000")
        configChart()
        anyChartView.setChart(cartesian)

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

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                formatLevelOfCourage =
                    "${getString(R.string.level_of_courage)} : ${(progress + 1)}%"
                descriptionSeekBarTextView.text = formatLevelOfCourage
                levelOfCourageValue = progress + 1
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        suck.setOnClickListener {
            onStop = false
            starterLinearLayout.visibility = View.INVISIBLE
            suck.visibility = View.INVISIBLE
            if (percent.text.isEmpty()) {
                Toast.makeText(this, "Percent can not be empty", Toast.LENGTH_SHORT).show()
            } else if (!percent.text.isDigitsOnly()) {
                Toast.makeText(this, "Percent must number only", Toast.LENGTH_SHORT).show()
            } else {
                basePayInValue =
                    (Config().getLabor(balance.text.toString().toDouble() * 0.00000001) * 10).toBigDecimal()
                targetBalanceValue =
                    balance.text.toString().toDouble() + ((balance.text.toString().toDouble() * percent.text.toString().toInt()) / 100)
                maxPayInValue = (basePayInValue * (1000).toBigDecimal())
                targetBalance.text = formatLot.format(targetBalanceValue)
                when (levelOfCourageValue) {
                    2 -> {
                        basePayInValue *= (5).toBigDecimal()
                    }
                    3 -> {
                        basePayInValue *= (10).toBigDecimal()
                    }
                    else -> {
                        basePayInValue
                    }
                }

                bot()
            }
        }

        stopButton.setOnClickListener {
            stopBot()
            seekBar.isEnabled = true
            starterLinearLayout.visibility = View.VISIBLE
            suck.visibility = View.VISIBLE
        }
    }

    private fun setValueFibonacci() {
        fibonacciArray.add(1)
        fibonacciArray.add(1)
        fibonacciArray.add(2)
        fibonacciArray.add(3)
        fibonacciArray.add(5)
        fibonacciArray.add(8)
        fibonacciArray.add(13)
        fibonacciArray.add(21)
        fibonacciArray.add(34)
        fibonacciArray.add(55)
        fibonacciArray.add(89)
        fibonacciArray.add(144)
        fibonacciArray.add(233)
        fibonacciArray.add(377)
        fibonacciArray.add(610)
        fibonacciArray.add(987)
        fibonacciArray.add(1597)
        fibonacciArray.add(2584)
        fibonacciArray.add(4181)
        fibonacciArray.add(6765)
        fibonacciArray.add(10946)
        fibonacciArray.add(17711)
        fibonacciArray.add(28657)
        fibonacciArray.add(46368)
        fibonacciArray.add(75025)
        fibonacciArray.add(121393)
        fibonacciArray.add(196418)
        fibonacciArray.add(317811)
        fibonacciArray.add(514229)
    }

    private fun bot() {
        val loop = 30
        val seed = format.format((0..99999).random())
        val session = user.sessionDoge
        val jumper = 0
<<<<<<< HEAD
        //todo: Add Bot System from old Bot
=======
>>>>>>> master
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

    private fun stopBot() {
        onStop = true
    }
}
