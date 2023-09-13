package com.limtis.wifinotify

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WifiSet(context: Context) {
    companion object {
        const val PREF_KEY = "wifi_set"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
    private val gson = Gson()
    private var set: MutableSet<String>

    init {
        set = loadSet()
    }

    // Function to load the set from SharedPreferences
    private fun loadSet(): MutableSet<String> {
        val jsonSet = sharedPreferences.getString(PREF_KEY, null)
        return if (jsonSet != null) {
            val setType = object : TypeToken<MutableSet<String>>() {}.type
            gson.fromJson(jsonSet, setType) ?: mutableSetOf()
        } else {
            mutableSetOf()
        }
    }

    private fun save() {
        val editor = sharedPreferences.edit()
        val jsonSet = gson.toJson(set)
        editor.putString(PREF_KEY, jsonSet)
        editor.apply()
    }

    fun add(item: String) {
        set.add(item)
        save()
    }

    fun remove(item: String) {
        set.remove(item)
        save()
    }

    fun contains(item: String): Boolean {
        return set.contains(item)
    }
}