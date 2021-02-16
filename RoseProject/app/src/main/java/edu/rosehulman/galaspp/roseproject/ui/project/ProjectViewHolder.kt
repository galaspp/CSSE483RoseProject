package edu.rosehulman.galaspp.roseproject.ui.project

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_row_view.view.*


class ProjectViewHolder : RecyclerView.ViewHolder {
    private var context: Context?
    private val task_view : TextView = itemView.task_text_view
    private val assigned_view : TextView = itemView.assigned_text_view
    private val urgency_view : TextView = itemView.urgency_text_view

    constructor(itemView: View, adapter: ProjectAdapter, context: Context?):  super(itemView){
        this.context = context
        itemView.setOnClickListener{
        }
        itemView.setOnLongClickListener {
            adapter.showCreateorEditTaskModal(adapterPosition)
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun bind(task: TaskObject) {
        task_view.text = task.name
        assigned_view.text = task.assignedTo
        urgency_view.text = task.urgency.toString()
    }



}