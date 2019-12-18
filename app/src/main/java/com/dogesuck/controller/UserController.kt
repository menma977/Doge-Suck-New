package com.dogesuck.controller

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

class UserController {
    class GetType(var username: String) : AsyncTask<Void, Void, JSONObject>() {
        override fun doInBackground(vararg params: Void?): JSONObject {
            try {
                val userAgent = "Mozilla/5.0"
                val url = URL("${Url.getUrl()}/index.php")
                val httpURLConnection = url.openConnection() as HttpURLConnection
                //add request header
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("User-Agent", userAgent)
                httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
                httpURLConnection.setRequestProperty("Accept", "application/json")

                val body = HashMap<String, Any>()
                body["a"] = "StatusMember"
                body["username"] = username

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

    class GetBalance(var usernameDoge: String, var passwordDoge: String) :
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
                body["a"] = "Login"
                body["Key"] = "56f1816842b340a6bc07246801552702"
                body["Username"] = usernameDoge
                body["password"] = passwordDoge
                body["Totp"] = "''"

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

    class GetStatus(var username: String) : AsyncTask<Void, Void, JSONObject>() {
        override fun doInBackground(vararg params: Void?): JSONObject {
            try {
                val userAgent = "Mozilla/5.0"
                val url = URL("${Url.getUrl()}/index.php")
                val httpURLConnection = url.openConnection() as HttpURLConnection
                //add request header
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("User-Agent", userAgent)
                httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
                httpURLConnection.setRequestProperty("Accept", "application/json")

                val body = HashMap<String, Any>()
                body["a"] = "CekStatus"
                body["username"] = username
                body["plan"] = "A"

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