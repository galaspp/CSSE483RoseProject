package edu.rosehulman.galaspp.roseproject.ui.project

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import edu.rosehulman.galaspp.roseproject.Constants
import edu.rosehulman.galaspp.roseproject.R
import kotlinx.android.synthetic.main.create_edit_task_modal.view.*
import kotlin.collections.ArrayList

class ProjectAdapter(
        private var context: Context,
        private var project: ProjectObject,
) : RecyclerView.Adapter<ProjectViewHolder>(){

//    var tasks = ArrayList<Task>()
    private var itemFilter: Int = 0
    private val projectsRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.PROJECTS_COLLECTION)
    private val tasksRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.TASKS_COLLECTION)

    override fun getItemCount() = project.projectTasks.filter { s -> s.currentStatus == itemFilter }.size//tasks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_row_view, parent, false)
        return ProjectViewHolder(view, this, context)
    }

//    init{
//        add(Task("Do things", "Piotr", 3, 0, 0.0))
//        add(Task("Complete pls", "Cameron", 10, 0, 0.0, ))
//        add(Task("Not sure", "IDK", 0, 0, 0.0))
//    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(viewHolder: ProjectViewHolder, index: Int) {
        //viewHolder.bind(tasks[index])
        viewHolder.bind(project.projectTasks.filter { s -> s.currentStatus == itemFilter }[index])
    }

    fun add(task: TaskObject){
        //tasks.add(task)
        project.projectTasks.add(task)
        tasksRef
                .add(task)
                .addOnSuccessListener { snapshot: DocumentReference ->
                    projectsRef.document(project.id).update("taskReferences", FieldValue.arrayUnion(snapshot.id))
                }

        notifyDataSetChanged()
    }

//    fun setDataSet(array: ArrayList<Task>) {
//        tasks = array
//        notifyDataSetChanged()
//    }
//
//    fun getDataSet(): ArrayList<Task> {
//        return tasks
//    }

    fun setFilter(position: Int) {
//        Log.d("YEET2",  project.projectTasks.filter { s -> s.currentStatus == position }.size.toString())
//        project.projectTasks.filter { s -> s.currentStatus == position }.size
        itemFilter = position
        notifyDataSetChanged()
    }


    private fun editItem(position: Int, task: TaskObject) {
//        tasks[position].name = task.name
//        tasks[position].assignedTo = task.assignedTo
//        tasks[position].urgency = task.urgency
        project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].name = task.name
        project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].assignedTo = task.assignedTo
        project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].urgency = task.urgency
        project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].projectTaskLog = task.projectTaskLog

        //Must occur in the end since this will change the position of the item
        project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].currentStatus = task.currentStatus
        notifyDataSetChanged()

    }

    private fun remove(position: Int){
//        tasks.removeAt(position)
        project.projectTasks.removeAt(position)
        notifyItemRemoved(position)
    }

    fun showCreateorEditTaskModal(position:Int = -1)
    {
        val builder = AlertDialog.Builder(context)
        var recyclerViewAdapter: TaskLogAdapter? = null
        //TODO: Change title based on whether editing or creating team
        //TODO: Prepopulate items as needed
        if (position == -1)
            builder.setTitle("Create Task?")
        else
            builder.setTitle("Edit Task?")

        val view = LayoutInflater.from(context).inflate(R.layout.create_edit_task_modal, null, false)
        builder.setView(view)

        var arrayVal = view.resources.getStringArray(R.array.task_status_array)
        var aa = ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayVal)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.spinner_task.adapter = aa


        if(position != -1)
        {
//            view.edit_text_task_name.setText(tasks[position].name)
//            view.edit_text_assign_description.setText(tasks[position].assignedTo)
//            view.edit_text_urgency_description.setText(tasks[position].urgency.toString())
            view.edit_text_task_name.setText(project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].name)
            view.edit_text_assign_description.setText(project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].assignedTo)
            view.edit_text_urgency_description.setText(project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].urgency.toString())
            view.spinner_task.setSelection(project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].currentStatus)

            view.taskLogHoursTextView.visibility = VISIBLE
            view.edit_text_log_hours.visibility = VISIBLE
            view.submit_time_button.visibility = VISIBLE

            val recyclerView = view.taskLogRecyclerView
            recyclerViewAdapter = TaskLogAdapter(context, project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].projectTaskLog)
            recyclerView.adapter = recyclerViewAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)

            view.submit_time_button.setOnClickListener {
                var hours: Double = 0.0
                if(view.edit_text_log_hours.text.toString() != "")
                    hours = view.edit_text_log_hours.text.toString().toDouble()
                project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].hours = project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].hours + hours

                project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].projectTaskLog.add("Logged hours $hours Total Hours: ${project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].hours}")
                recyclerViewAdapter.notifyDataSetChanged()
                view.edit_text_log_hours.setText("")
            }

        }

        builder.setPositiveButton("Save") { _, _ ->
            var urgancy = 0

            if(view.edit_text_urgency_description.text.toString() != "")
                urgancy = view.edit_text_urgency_description.text.toString().toInt()


            if(position == -1)
            {
                val arrayList: ArrayList<String> = arrayListOf("Task Created")
                val task = TaskObject(view.edit_text_task_name.text.toString(),
                    view.edit_text_assign_description.text.toString(),
                    urgancy,
                    view.spinner_task.selectedItemPosition,
                    0.0
                )
//                val task = Task(view.edit_text_task_name.text.toString(),
//                        view.edit_text_assign_description.text.toString(),
//                        urgancy,
//                        view.spinner_task.selectedItemPosition,
//                        0.0,
//                        arrayList
//                )
                add(task)
            }
            else
            {
                if(view.spinner_task.selectedItemPosition != project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].currentStatus)
                {
                    project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].projectTaskLog.add("Changed Status To ${arrayVal[view.spinner_task.selectedItemPosition]}")
                }

                val task = TaskObject(view.edit_text_task_name.text.toString(),
                    view.edit_text_assign_description.text.toString(),
                    urgancy,
                    view.spinner_task.selectedItemPosition,
                    project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].hours,
//                  project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].projectTaskLog
                )
                editItem(position, task)
                recyclerViewAdapter?.notifyDataSetChanged()
            }

        }

        builder.setNegativeButton(android.R.string.cancel, null)

        builder.create().show()
    }

}