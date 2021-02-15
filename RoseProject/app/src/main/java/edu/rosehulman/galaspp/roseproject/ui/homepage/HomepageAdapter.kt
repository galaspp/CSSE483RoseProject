package edu.rosehulman.galaspp.roseproject.ui.homepage

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import edu.rosehulman.galaspp.roseproject.Constants
import edu.rosehulman.galaspp.roseproject.FragmentListener
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.MemberObject
import edu.rosehulman.galaspp.roseproject.ui.project.ProjectFragment
import edu.rosehulman.galaspp.roseproject.ui.project.TaskObject
import kotlin.collections.ArrayList

class HomepageAdapter(
    private var context: Context?,
    private var user: MemberObject,
    private var listener : FragmentListener
) : RecyclerView.Adapter<HomepageViewHolder>(){

    private val userTasks = ArrayList<TaskWrapper>()
    private val teamsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.TEAMS_COLLECTION)
    private val projectsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.PROJECTS_COLLECTION)
    private val tasksRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.TASKS_COLLECTION)

    override fun getItemCount() = userTasks.size

    private fun reload(){
        //Ridiculous Cascading for loop that adds all tasks the user is assigned to
        userTasks.clear()
        val teams = user.statuses.keys
        for(teamID in teams){
            teamsRef.document(teamID).get().addOnSuccessListener { teamSnapshot: DocumentSnapshot ->
                val teamName = teamSnapshot["teamName"] as String
                val projects = teamSnapshot[Constants.PROJECTS_FIELD] as List<String>
                for(projID in projects) {
                    projectsRef.document(projID).get().addOnSuccessListener { projectSnapshot: DocumentSnapshot ->
                        val tasks =  projectSnapshot[Constants.TASKS_FIELD] as List<String>
                        val projectName = projectSnapshot["projectTitle"] as String
                        for(taskID in tasks) {
                            tasksRef.document(taskID).get().addOnSuccessListener{ taskSnapshot: DocumentSnapshot ->
                                if(taskSnapshot.get("assignedTo") == user.name ){
                                    add(TaskWrapper(TaskObject.fromSnapshot(taskSnapshot), projectName, projID, teamName, teamID))
                                }
                            }
                        }
                    }
                }
            }
        }
        userTasks.sortedBy { it.task.lastTouched }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomepageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.homepage_row_view, parent, false)
        return HomepageViewHolder(view, this, context)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(viewHolder: HomepageViewHolder, index: Int) {
        viewHolder.bind(userTasks[index])
    }

    fun add(taskWrapper: TaskWrapper){
        userTasks.add(0, taskWrapper)
        notifyDataSetChanged()//TODO: notifyItemInserted(0) wasn't working
    }

    private fun remove(position: Int){
        userTasks.removeAt(position)
        notifyItemRemoved(position)
    }

    fun reload(updatedUser: MemberObject?) {
        this.user = updatedUser!!
        reload()
    }

    fun goToProject(position: Int) {
        val tw = userTasks[position]
        listener.openProjectFromHome(tw.teamID, tw.projID, tw.task.currentStatus)
    }

    class TaskWrapper(val task: TaskObject, val projectName: String, val projID: String, val teamName : String, val teamID: String)

}