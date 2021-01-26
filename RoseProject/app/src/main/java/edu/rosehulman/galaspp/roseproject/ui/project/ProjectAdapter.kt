package edu.rosehulman.galaspp.roseproject.ui.project

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R
import kotlin.collections.ArrayList

class ProjectAdapter(
        private var context: Context?,
) : RecyclerView.Adapter<ProjectViewHolder>(){

    val tasks = ArrayList<Task>()

    override fun getItemCount() = tasks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_row_view, parent, false)
        return ProjectViewHolder(view, this, context)
    }

    init{
        add(Task("Do things", "Piotr", 3))
        add(Task("Complete pls", "Cameron", 10))
        add(Task("Not sure", "IDK", 0))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(viewHolder: ProjectViewHolder, index: Int) {
        viewHolder.bind(tasks[index])
    }

    private fun add(task: Task){
        tasks.add(task)
        notifyDataSetChanged()
    }

    private fun remove(position: Int){
        tasks.removeAt(position)
        notifyItemRemoved(position)
    }

}