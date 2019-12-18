package com.dogesuck.controller.bot

import android.os.AsyncTask
import com.dogesuck.R
import com.dogesuck.model.Url
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class BotController {
    class ManualBot(
        private var session: String,
        private var high: String,
        private var basePayIn: String,
        private var seed: String
    ) :
        AsyncTask<Void, Void, JSONObject>() {
        override fun doInBackground(vararg params: Void?): JSONObject {
            try {
                val userAgent = "Mozilla/5.0"
                val url = URL("${Url.getUrlDoge()}/web.aspx")
                val httpURLConnection = url.openConnection() as HttpURLConnection
                //add request header
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("User-Agent", userAgent)
                httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
                httpURLConnection.setRequestProperty("Accept", "application/json")

                val body = HashMap<String, Any>()
                body["a"] = "PlaceBet"
                body["s"] = session
                body["Low"] = "0"
                body["High"] = high
                body["PayIn"] = basePayIn
                body["ProtocolVersion"] = "2"
                body["ClientSeed"] = seed
                body["Currency"] = "doge"

                // Send post request
                httpURLConnection.doOutput = true
                val write = DataOutputStream(httpURLConnection.outputStream)
                write.writeBytes(
                    body.toString()
                        .replace(", ", "&")
                        .replace("{", "")
                        .replace("}", "")
                )
                write.flush()
                write.close()

                val responseCode = httpURLConnection.responseCode
                return if (responseCode == 200) {
                    val input = BufferedReader(
                        InputStreamReader(httpURLConnection.inputStream)
                    )

                    val inputData: String = input.readLine()
                    val response = JSONObject(inputData)
                    input.close()
                    response
                } else {
                    JSONObject("{Status: 1, Pesan: '${R.string.error404}'}")
                }
            } catch (e: Exception) {
                return JSONObject("{Status: 1, Pesan: '${R.string.error500}'}")
            }
        }
    }

    class ClassicBot(
        private var session: String,
        private var basePayIn: String,
        private var maxPayIn: String,
        private var seed: String,
        private var maxBait: String = "200"
    ) :
        AsyncTask<Void, Void, JSONObject>() {
        override fun doInBackground(vararg params: Void?): JSONObject {
            try {
                val userAgent = "Mozilla/5.0"
                val url = URL("${Url.getUrlDoge()}/web.aspx")
                val httpURLConnection = url.openConnection() as HttpURLConnection
                //add request header
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("User-Agent", userAgent)
                httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
                httpURLConnection.setRequestProperty("Accept", "application/json")

                val body = HashMap<String, Any>()
                body["a"] = "PlaceAutomatedBets"
                body["s"] = session
                body["BasePayIn"] = basePayIn
                body["Low"] = "0"
                body["High"] = "499999"
                body["MaxBets"] = maxBait
                body["ResetOnWin"] = "1"
                body["ResetOnLose"] = "0"
                body["IncreaseOnWinPercent"] = "0"
                body["IncreaseOnLosePercent"] = "1"
                body["MaxPayIn"] = maxPayIn
                body["ResetOnLoseMaxBet"] = "1"
                body["StopOnLoseMaxBet"] = "0"
                body["StopMaxBalance"] = "0"
                body["StopMinBalance"] = "1000"
                body["StartingPayIn"] = basePayIn
                body["Compact"] = "1"
                body["ClientSeed"] = seed
                body["Currency"] = "doge"

                // Send post request
                httpURLConnection.doOutput = true
                val write = DataOutputStream(httpURLConnection.outputStream)
                write.writeBytes(
                    body.toString()
                        .replace(", ", "&")
                        .replace("{", "")
                        .replace("}", "")
                )
                write.flush()
                write.close()

                val responseCode = httpURLConnection.responseCode
                return if (responseCode == 200) {
                    val input = BufferedReader(
                        InputStreamReader(httpURLConnection.inputStream)
                    )

                    val inputData: String = input.readLine()
                    val response = JSONObject(inputData)
                    input.close()
                    response
                } else {
                    JSONObject("{Status: 1, Pesan: '${R.string.error404}'}")
                }
            } catch (e: Exception) {
                return JSONObject("{Status: 1, Pesan: '${R.string.error500}'}")
            }
        }
    }
}