package edu.rosehulman.galaspp.roseproject.ui.homepage

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.homepage_row_view.view.*

class HomepageViewHolder : RecyclerView.ViewHolder {
    private val taskTitleTextView: TextView = itemView.task_title_text_view
    private val teamTitleTextView: TextView = itemView.team_title_text_view
    private var context: Context?

    constructor(itemView: View, adapter: HomepageAdapter, context: Context?):  super(itemView){
        this.context = context
        itemView.setOnClickListener{
            adapter.goToProject(adapterPosition)
        }
        itemView.setOnLongClickListener {
            true
        }
    }

    fun bind(taskWrapper: HomepageAdapter.TaskWrapper) {
        taskTitleTextView.text = taskWrapper.task.name
        teamTitleTextView.text = "${taskWrapper.projectName}, ${taskWrapper.teamName}"
    }

}