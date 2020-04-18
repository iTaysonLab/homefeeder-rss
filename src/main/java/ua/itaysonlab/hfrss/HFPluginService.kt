package ua.itaysonlab.hfrss

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.os.bundleOf
import com.prof.rssparser.Parser
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import ua.itaysonlab.hfrss.pref.HFPluginPreferences
import ua.itaysonlab.hfsdk.*
import ua.itaysonlab.hfsdk.actions.ActionType
import ua.itaysonlab.hfsdk.actions.CardAction
import ua.itaysonlab.hfsdk.content.StoryCardContent
import ua.itaysonlab.hfsdk.content.TextCardContent
import java.text.SimpleDateFormat
import java.util.*

/**
 * Your most important class (maybe)
 * This defines which data does the plugin send to HomeFeeder.
 */
class HFPluginService: Service(), CoroutineScope by MainScope() {
    val sourceSdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)

    private val mBinder = object: IFeedInterface.Stub() {
        override fun getFeed(
            callback: IFeedInterfaceCallback?,
            page: Int,
            category_id: String,
            parameters: Bundle?
        ) {
            callback ?: return

            launch {
                val list = mutableListOf<FeedItem>()

                withContext(Dispatchers.Default) {
                    val parser = Parser(OkHttpClient())

                    HFPluginPreferences.parsedFeedList.forEach { model ->
                        parser.getChannel(model.feed_url).articles.forEach { article ->
                            list.add(FeedItem(
                                "${model.name} [RSS]",
                                FeedItemType.STORY_CARD,
                                StoryCardContent(
                                    title = article.title!!,
                                    text = article.description!!,
                                    background_url = article.image ?: "",
                                    link = article.link ?: "",
                                    source = FeedCategory(model.feed_url, model.name, Color.GREEN, model.pic_url)
                                ),
                                sourceSdf.parse(article.pubDate!!)!!.time
                            ))
                        }
                    }
                }

                callback.onFeedReceive(list)
            }
        }

        override fun getCategories(callback: IFeedInterfaceCallback) {
            callback.onCategoriesReceive(HFPluginPreferences.parsedFeedList.map {
                FeedCategory(it.feed_url, it.name, Color.GREEN, it.pic_url)
            })
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }
}