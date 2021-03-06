package edu.rosehulman.galaspp.roseproject.ui.project

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import edu.rosehulman.galaspp.roseproject.Constants
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.MemberObject
import kotlinx.android.synthetic.main.create_edit_task_modal.view.*
import kotlin.collections.ArrayList

class ProjectAdapter(
        private var context: Context,
        private var project: ProjectObject,
        var userObject: String,
        private var teamId: String
) : RecyclerView.Adapter<ProjectViewHolder>(){

    private var itemFilter: Int = 0
    private val projectsRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.PROJECTS_COLLECTION)
    private val tasksRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.TASKS_COLLECTION)
    private val teamsRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.TEAMS_COLLECTION)
    private val membersRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.MEMBER_COLLECTION)
    override fun getItemCount() = project.projectTasks.filter { s -> s.currentStatus == itemFilter }.size//tasks.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_row_view, parent, false)
        return ProjectViewHolder(view, this, context)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(viewHolder: ProjectViewHolder, index: Int) {
        viewHolder.bind(project.projectTasks.filter { s -> s.currentStatus == itemFilter }[index])
    }

    fun add(task: TaskObject){
        tasksRef.add(task).addOnSuccessListener { snapshot: DocumentReference ->
            projectsRef.document(project.id).update(Constants.TASKS_FIELD, FieldValue.arrayUnion(snapshot.id))
            task.id = snapshot.id
            project.projectTasks.add(task)
            notifyDataSetChanged()
        }
    }

    fun setFilter(position: Int) {
        itemFilter = position
        notifyDataSetChanged()
    }

    private fun editItem(position: Int, task: TaskObject) {
        tasksRef
                .document(project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].id)
                .set(task)
        project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].name = task.name
        project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].assignedTo = task.assignedTo
        project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].urgency = task.urgency
        project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].projectTaskLog = task.projectTaskLog

        //Must occur in the end since this will change the position of the item
        project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].currentStatus = task.currentStatus
        notifyDataSetChanged()

    }

    private fun remove(position: Int){
        val taskId = project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].id
        project.projectTasks.removeAt(position)
        tasksRef.document(taskId).delete().addOnSuccessListener {
            projectsRef.document(project.id).update(Constants.TASKS_FIELD, FieldValue.arrayRemove(taskId))
        }
        notifyItemRemoved(position)
    }
    @Suppress("UNCHECKED_CAST")
    fun showCreateorEditTaskModal(position: Int = -1, taskName: String = "", urgency: String = "",
                                  assignedTo: String = "", status: Int = 0){
        //Access teams reference and extract name of each user in team, send to modal
        val allNames = ArrayList<String>()
        teamsRef.document(teamId).get().addOnSuccessListener { snapshot: DocumentSnapshot ->
            val allIDs = snapshot[Constants.MEMBERS_FIELD] as ArrayList<String>
            for(index in 0 until allIDs.size){
                Log.d(Constants.TAG, index.toString())
                membersRef.document(allIDs[index]).get().addOnSuccessListener {
                    allNames.add(it[Constants.NAME_FIELD] as String)
                    if(index == allIDs.size-1){
                        Log.d(Constants.TAG, allNames.toString())
                        showCreateorEditTaskModalHelper(position, taskName, urgency, assignedTo, status, allNames)
                    }
                }
            }
        }
    }

    fun showCreateorEditTaskModalHelper(position: Int = -1, taskName: String = "", urgency: String = "",
                                  assignedTo: String = "", status: Int = 0, allNames: ArrayList<String>){
        val builder = AlertDialog.Builder(context)
        var recyclerViewAdapter: TaskLogAdapter? = null
        //TODO: Change title based on whether editing or creating team
        //TODO: Prepopulate items as needed
        if (position == -1)
            builder.setTitle(R.string.create_task)
        else
            builder.setTitle(R.string.edit_task)

        val view = LayoutInflater.from(context).inflate(R.layout.create_edit_task_modal, null, false)
        builder.setView(view)

        //Set Autocomplete
        val autoAdapter = ArrayAdapter(context, android.R.layout.select_dialog_item, allNames)
        view.edit_text_assign_description.threshold = 1
        view.edit_text_assign_description.setAdapter(autoAdapter)

        val arrayVal = view.resources.getStringArray(R.array.task_status_array)
        val aa = ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayVal)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.spinner_task.adapter = aa

        //Fill/Refill text boxes - used to refill if assignedTo user does not exists
        view.edit_text_task_name.setText(taskName)
        view.edit_text_urgency_description.setText(urgency)
        view.edit_text_assign_description.setText(assignedTo)
        view.spinner_task.setSelection(status)

        if(position != -1){
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
                var hours = 0.0
                if(view.edit_text_log_hours.text.toString() != "")
                    hours = view.edit_text_log_hours.text.toString().toDouble()
                project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].hours = project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].hours + hours
                project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].projectTaskLog.add("Logged hours $hours Total Hours: ${project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].hours}")
                editItem(position, project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position])
                recyclerViewAdapter.notifyDataSetChanged()
                view.edit_text_log_hours.setText("")
            }
        }

        builder.setPositiveButton(R.string.Save) { _, _ ->
            val assignedMember = view.edit_text_assign_description.text.toString()
            if (!allNames.contains(assignedMember)){
                val taskNameOld = view.edit_text_task_name.text.toString()
                val urgencyOld = view.edit_text_urgency_description.text.toString()
                val assignedToOld = view.edit_text_assign_description.text.toString()
                val statusOld = view.spinner_task.selectedItemPosition
                showCreateorEditTaskModal(position, taskNameOld, urgencyOld, assignedToOld, statusOld)
                Toast.makeText(context, "User: $assignedMember does not exist.", Toast.LENGTH_SHORT).show()
            } else {
                var urgancy = 1
                if(view.edit_text_urgency_description.text.toString() != ""){
                    urgancy = view.edit_text_urgency_description.text.toString().toInt()
                    if (urgancy < 1) urgancy = 1
                    else if (urgancy > 10) urgancy = 10
                }
                if(position == -1){
                    val arrayList: ArrayList<String> = arrayListOf("Task Created")
                    val task = TaskObject(view.edit_text_task_name.text.toString(),
                            view.edit_text_assign_description.text.toString(),
                            urgancy,
                            view.spinner_task.selectedItemPosition,
                            0.0,
                            arrayList
                    )
                    add(task)
                } else {
                    if(view.spinner_task.selectedItemPosition != project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].currentStatus) {
                        project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].projectTaskLog.add("Changed Status To ${arrayVal[view.spinner_task.selectedItemPosition]}")
                    }
                    val task = TaskObject(view.edit_text_task_name.text.toString(),
                            view.edit_text_assign_description.text.toString(),
                            urgancy,
                            view.spinner_task.selectedItemPosition,
                            project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].hours,
                            project.projectTasks.filter { s -> s.currentStatus == itemFilter }[position].projectTaskLog
                    )
                    editItem(position, task)
                    recyclerViewAdapter?.notifyDataSetChanged()
                }
            }
        }
        builder.setNegativeButton(android.R.string.cancel, null)

        if(position != -1){
            membersRef.document(userObject).get().addOnSuccessListener {
                val memObj = MemberObject.fromSnapshot(it)
                if(memObj.statuses[teamId] == Constants.OWNER){
                    builder.setNeutralButton(R.string.Delete) { _, _ ->
                        confirmDeleteModal(position)
                    }
                }
                builder.create().show()
            }
        } else{
            builder.create().show()
        }
    }

    //Reuses for task modal
    private fun getAllMemberNames() : ArrayList<String> {
        //Access members reference and extract name of each user, return in array
        val ret = ArrayList<String>()
        membersRef.get().addOnSuccessListener { snapshot: QuerySnapshot ->
            for (doc in snapshot){
                ret.add(MemberObject.fromSnapshot(doc).name)
            }
        }
        return ret
    }

    private fun confirmDeleteModal(position : Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.deleteTask)
        builder.setMessage(R.string.deleteTaskMessage)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            remove(position)
        }
        builder.setNegativeButton(android.R.string.cancel, null) //Do Nothing
        builder.create().show()
    }

}