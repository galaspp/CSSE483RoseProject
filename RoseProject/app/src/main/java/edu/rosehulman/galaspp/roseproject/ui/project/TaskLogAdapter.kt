package edu.rosehulman.galaspp.roseproject.ui.project

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R

class TaskLogAdapter(private var context: Context, private var arrayList: ArrayList<String>
): RecyclerView.Adapter<TaskLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskLogViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_log_card_view, parent, false)
        return TaskLogViewHolder(view, this)
    }

    override fun getItemCount() = arrayList.size

    override fun onBindViewHolder(holder: TaskLogViewHolder, position: Int) {
        holder.bind(arrayList[position])
    }
}