package edu.rosehulman.galaspp.roseproject.ui.project

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_log_card_view.view.*

class TaskLogViewHolder: RecyclerView.ViewHolder {

    private val textView: TextView = itemView.task_log_card_view_text
    constructor(itemView: View, adapter: TaskLogAdapter): super(itemView) {
    }
    fun bind(text: String){
        textView.text = text
    }
}
