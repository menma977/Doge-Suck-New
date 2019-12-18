package com.dogesuck.controller

import android.os.AsyncTask
import com.dogesuck.R
import com.dogesuck.model.Url
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RegisterController {
    class Post(var parameter : HashMap<String, String>) : AsyncTask<Void, Void, JSONObject>() {
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
                body["a"] = "Register"
                body["sponsor"] = parameter["sponsor"].toString()
                body["password"] = parameter["password"].toString()
                body["term"] = parameter["term"].toString()
                body["email"] = parameter["email"].toString()
                body["name"] = parameter["name"].toString()
                body["username"] = parameter["username"].toString()

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
                    println(inputData)
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