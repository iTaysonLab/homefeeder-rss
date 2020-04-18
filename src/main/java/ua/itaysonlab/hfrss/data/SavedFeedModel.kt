package ua.itaysonlab.hfrss.data

import org.json.JSONObject

data class SavedFeedModel (
    val name: String,
    val desc: String,
    val feed_url: String,
    val pic_url: String
) {
    constructor(obj: JSONObject): this(
        obj.getString("name"),
        obj.getString("desc"),
        obj.getString("feed_url"),
        obj.getString("pic_url")
    )

    fun asJson() = JSONObject().apply {
        put("name", name)
        put("desc", desc)
        put("feed_url", feed_url)
        put("pic_url", pic_url)
    }
}