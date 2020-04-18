package ua.itaysonlab.hfrss.pref

import org.json.JSONObject
import ua.itaysonlab.hfrss.data.SavedFeedModel
import ua.itaysonlab.hfrss.utils.PreferenceHelper

object HFPluginPreferences {
    private const val KEY = "HFFeedList"

    val feedList get() = PreferenceHelper.getSet(KEY)
    val parsedFeedList get() = PreferenceHelper.getSet(KEY).map {
        SavedFeedModel(JSONObject(it))
    }

    fun add(url: SavedFeedModel) {
        PreferenceHelper.setSet(KEY, feedList.apply {
            add(url.asJson().toString())
        })
    }

    fun remove(url: SavedFeedModel) {
        PreferenceHelper.setSet(KEY, feedList.apply {
            remove(url.asJson().toString())
        })
    }
}