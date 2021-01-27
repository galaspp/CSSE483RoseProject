package edu.rosehulman.galaspp.roseproject.ui.project

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R
import kotlinx.android.synthetic.main.create_edit_task_modal.view.*
import kotlinx.android.synthetic.main.profile_row_view.view.*
import kotlinx.android.synthetic.main.task_row_view.view.*


class ProjectViewHolder : RecyclerView.ViewHolder {
    private var context: Context?
    val task_view : TextView = itemView.task_text_view
    val assigned_view : TextView = itemView.assigned_text_view
    val urgency_view : TextView = itemView.urgency_text_view
//    val spinner_view : Spinner = itemView.spinner_task

    constructor(itemView: View, adapter: ProjectAdapter, context: Context?):  super(itemView){
        this.context = context
        itemView.setOnClickListener{
//            Log.d("test", "Clicked on ${adapter.tasks[adapterPosition].name}")
        }
        itemView.setOnLongClickListener {
//            Log.d("test", "Edit ${adapter.tasks[adapterPosition].name}")
            adapter.showCreateorEditTaskModal(adapterPosition)
            true
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun bind(task: Task) {
        task_view.text = task.name
        assigned_view.text = task.assignedTo
        urgency_view.text = task.urgency.toString()
//        spinner_view.setSelection(task.currentStatus)
    }



}