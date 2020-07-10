package ua.itaysonlab.hfrss.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.prof.rssparser.Channel
import com.prof.rssparser.Parser
import kotlinx.android.synthetic.main.feed_item.view.*
import kotlinx.android.synthetic.main.feed_manager.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import ua.itaysonlab.hfrss.R
import ua.itaysonlab.hfrss.data.SavedFeedModel
import ua.itaysonlab.hfrss.pref.HFPluginPreferences

class FeedManagerActivity: AppCompatActivity(), CoroutineScope by MainScope() {
    var list = listOf<SavedFeedModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feed_manager)

        add_feed.setOnClickListener {
            NewFeedBottomSheet { url ->
                launch {
                    var data: Channel? = null

                    withContext(Dispatchers.Default) {
                        val parser = Parser(OkHttpClient())
                        try {
                            data = parser.getChannel(url)
                        } catch (e: Exception) {

                        }
                    }

                    data ?: run {
                        Toast.makeText(this@FeedManagerActivity, "URL is not a RSS feed!", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    HFPluginPreferences.add(SavedFeedModel(
                        data!!.title!!,
                        data!!.description ?: "",
                        url,
                        data!!.image?.url ?: ""
                    ))

                    reload()
                }
            }.show(supportFragmentManager, null)
        }

        reload()
    }

    private fun reload() {
        list = HFPluginPreferences.parsedFeedList
        feeds.apply {
            layoutManager = LinearLayoutManager(this@FeedManagerActivity)
            adapter = SourceAdapter()
        }
    }

    inner class SourceAdapter: RecyclerView.Adapter<SourceAdapter.SourceVH>() {
        private lateinit var inflater: LayoutInflater

        inner class SourceVH(itemView: View): RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceVH {
            if (!::inflater.isInitialized) inflater = LayoutInflater.from(parent.context)
            return SourceVH(inflater.inflate(R.layout.feed_item, parent, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: SourceVH, position: Int) {
            val item = list[position]
            holder.itemView.apply {
                if (item.pic_url.isBlank()) {
                    this.app_icon.visibility = View.GONE
                } else {
                    this.app_icon.visibility = View.VISIBLE
                    this.app_icon.load(item.pic_url)
                }

                this.plugin_name.text = item.name
                this.plugin_author.text = item.feed_url
                this.plugin_desc.text = item.desc

                this.plugin_status.setOnClickListener {
                    AlertDialog.Builder(it.context).apply {
                        setTitle(R.string.remove_title)
                        setMessage(resources.getString(R.string.remove_desc, item.name))
                        setNeutralButton(R.string.remove_action_nope, null)
                        setPositiveButton(R.string.remove_action_yes) { _, _ ->
                            HFPluginPreferences.remove(item)
                            reload()
                        }
                    }.show()
                }
            }
        }
    }
}