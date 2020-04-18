package ua.itaysonlab.hfrss.utils

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import ua.itaysonlab.hfrss.Plugin
import ua.itaysonlab.hfsdk.HFPluginApplication

object PreferenceHelper {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(HFPluginApplication.instance)

    fun get(key: String, default: Boolean): Boolean {
        return prefs.getBoolean(key, default)
    }

    fun get(key: String, default: String): String {
        return prefs.getString(key, default)!!
    }

    fun getSet(key: String): MutableSet<String> {
        return prefs.getStringSet(key, mutableSetOf<String>())!!
    }

    fun setSet(key: String, set: MutableSet<String>) {
        prefs.edit().apply {
            remove(key)
            commit()
            putStringSet(key, set)
            commit()
        }
    }

    fun get(key: String, default: Int): Int {
        return prefs.getInt(key, default)
    }
}