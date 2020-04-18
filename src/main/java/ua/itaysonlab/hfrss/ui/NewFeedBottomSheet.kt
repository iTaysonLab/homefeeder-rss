package ua.itaysonlab.hfrss.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.add_feed_bottomsheet.view.*
import ua.itaysonlab.hfrss.R

class NewFeedBottomSheet(private val callback: (String) -> Unit): BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_feed_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.add.setOnClickListener {
            val txt = view.url.text
            if (txt.isNullOrEmpty()) return@setOnClickListener

            callback(txt.toString())
            dismiss()
        }
    }
}