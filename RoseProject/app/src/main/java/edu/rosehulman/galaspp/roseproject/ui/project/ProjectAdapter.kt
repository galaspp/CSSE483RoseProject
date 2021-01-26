package edu.rosehulman.galaspp.roseproject.ui.project

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R
import kotlinx.android.synthetic.main.add_remove_members_modal.view.*
import kotlinx.android.synthetic.main.create_edit_task_modal.view.*
import kotlin.collections.ArrayList

class ProjectAdapter(
        private var context: Context,
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

    fun add(task: Task){
        tasks.add(task)
        notifyDataSetChanged()
    }

    private fun editItem(position: Int, task: Task) {
        tasks[position].name = task.name
        tasks[position].assignedTo = task.assignedTo
        tasks[position].urgency = task.urgency
        notifyDataSetChanged()

    }

    private fun remove(position: Int){
        tasks.removeAt(position)
        notifyItemRemoved(position)
    }

    fun showCreateorEditTaskModal(position:Int = -1)
    {
        val builder = AlertDialog.Builder(context)
        //TODO: Change title based on whether editing or creating team
        //TODO: Prepopulate items as needed
        if (position == -1)
            builder.setTitle("Create Task?")
        else
            builder.setTitle("Edit Task?")

        val view = LayoutInflater.from(context).inflate(R.layout.create_edit_task_modal, null, false)
        builder.setView(view)

        if(position != -1)
        {
            view.edit_text_task_name.setText(tasks[position].name)
            view.edit_text_assign_description.setText(tasks[position].assignedTo)
            view.edit_text_urgency_description.setText(tasks[position].urgency.toString())
        }

        var arrayVal = view.resources.getStringArray(R.array.task_status_array)
        var aa = ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayVal)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.spinner.adapter = aa

        builder.setPositiveButton("Save") { _, _ ->
            val task = Task(view.edit_text_task_name.text.toString(),
                    view.edit_text_assign_description.text.toString(),
                    view.edit_text_urgency_description.text.toString().toInt()
            )
            if(position == -1)
            {
                add(task)
            }
            else
            {
                editItem(position, task)
            }

        }

        builder.setNegativeButton(android.R.string.cancel, null)

        builder.create().show()
    }

}