package com.dogesuck.controller

import android.app.Activity
import android.os.AsyncTask
import com.dogesuck.R
import com.dogesuck.model.Url
import com.dogesuck.model.User
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class LoginController {
    class Login(var username: String, var password: String) : AsyncTask<Void, Void, JSONObject>() {
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
                body["a"] = "LoginSession"
                body["key"] = "cb48f0ffc3614a5884517d6425506a6b"
                body["username"] = username
                body["password"] = password

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

    class LoginDoge(var usernameDoge: String, var passwordDoge: String) :
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
                body["key"] = "56f1816842b340a6bc07246801552702"
                body["Username"] = usernameDoge
                body["Password"] = passwordDoge
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
}