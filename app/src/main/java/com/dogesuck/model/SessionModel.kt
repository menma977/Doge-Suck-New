package com.dogesuck.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import org.json.JSONObject

@SuppressLint("CommitPrefEdits")
class SessionModel(context: Context) {
    private val sharedPreferences: SharedPreferences
    private val sharedPreferencesEditor: SharedPreferences.Editor

    init {
        sharedPreferences = context.getSharedPreferences(userData, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()
    }

    fun save(jsonObject: String) {
        sharedPreferencesEditor.putString("json", jsonObject)
        sharedPreferencesEditor.commit()
    }

    fun get(): JSONObject {
        return JSONObject(sharedPreferences.getString("json", "[]"))
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
        sharedPreferencesEditor.clear()
    }

    companion object {
        private const val userData = "userData"
    }
}