package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import edu.rosehulman.galaspp.roseproject.Constants
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.project.ProjectObject
import edu.rosehulman.galaspp.roseproject.ui.project.TaskObject
import kotlinx.android.synthetic.main.create_project_modal.view.*

class NavDrawerAdapter (val context: Context, val listener: OnNavDrawerListener, var userObject : MemberObject? = null) : RecyclerView.Adapter<NavDrawerHolder>() {
    var teams : ArrayList<TeamObject> = ArrayList()
    private val teamsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.TEAMS_COLLECTION)
    private val projectsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.PROJECTS_COLLECTION)
    private val tasksRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.TASKS_COLLECTION)
    private val memberRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.MEMBER_COLLECTION)

    private var teamReturnReference: ListenerRegistration? = null
    private var projectReturnReference: ListenerRegistration? = null
    private var taskReturnReference: ListenerRegistration? = null
    private var memberReturnReference: ListenerRegistration? = null

    lateinit var viewNavDrawer: View

    fun setup(member: MemberObject) {
        userObject = member
        teams.clear()
        teamReturnReference = teamsRef.orderBy(TeamObject.LAST_TOUCHED_KEY, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if(exception != null) {
                    Log.e("Nav Drawer Error", "Listen Error: $exception")
                    return@addSnapshotListener
                }
                for(teamChange in snapshot!!.documentChanges) {
                    val team = TeamObject.fromSnapshot(teamChange.document)
                    if(userObject?.let { team.teamMemberReferences.contains(it.id) } == true) {
                        when (teamChange.type) {
                            DocumentChange.Type.ADDED -> {
                                getProjectsFromIDs(team.projectReferences, team)
                                teams.add(0, team)
                                notifyItemInserted(0)
                            }
                            DocumentChange.Type.REMOVED -> {
                                val pos = teams.indexOfFirst { team.id == it.id }
                                deleteProjectReferences(team.projectReferences)
                                teams.removeAt(pos)
                                notifyItemRemoved(pos)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                val pos = teams.indexOfFirst { team.id == it.id }
                                teams[pos] = team
                                getProjectsFromIDs(team.projectReferences, team)
                                notifyItemChanged(pos)
                            }
                        }
                    }
                }
            }
        memberReturnReference = memberRef.addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
            if(exception != null) {
                Log.e("NavDrawer MemRef Error ", "Listen Error: $exception")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                for(eachMember in snapshot.documents) {
                    val tempMember = MemberObject.fromSnapshot(eachMember)
                    if(userObject!!.id == tempMember.id)
                        userObject = tempMember
                }
            }
        }
    }

    private fun deleteProjectReferences(projectReferences: ArrayList<String>) {
        for( refID in projectReferences ){
            projectsRef.document(refID).delete()
        }
    }
    private fun deleteTaskReferences(taskReferences: ArrayList<String>) {
        for( refID in taskReferences ){
            tasksRef.document(refID).delete()
        }
    }

    private fun getProjectsFromIDs(projIds : ArrayList<String>, tm : TeamObject) {
        //Get top level projects reference from firebase
        //loop over ids and match with documents from projects reference, then convert and add to lists
        projectReturnReference = projectsRef
            .addSnapshotListener {snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if(exception != null) {
                    Log.e("Nav Drawer Error", "Listen Error: $exception")
                    return@addSnapshotListener
                }
                for(projChange in snapshot!!.documentChanges) {
                    val po = ProjectObject.fromSnapshot(projChange.document)
                    if(projIds.contains(po.id)) {
                        when (projChange.type) {
                            DocumentChange.Type.ADDED -> {
                                getTasksFromIDs(po.taskReferences, po)
                                tm.projects.add(0, po)
                                notifyDataSetChanged()
                            }
                            DocumentChange.Type.REMOVED -> {
                                deleteTaskReferences(po.taskReferences)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                val pos = tm.projects.indexOfFirst { po.id == it.id }
                                tm.projects[pos] = po
                                getTasksFromIDs(po.taskReferences, po)
                                notifyItemChanged(pos)
                            }
                        }
                    }
                }
            }
    }

    private fun getTasksFromIDs(ids : ArrayList<String>, po : ProjectObject) {
        //Get top level task reference from firebase
        //loop over ids and match with documents from tasks reference, then convert and add to lists
        taskReturnReference = tasksRef
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
                    Log.e("Nav Drawer Error", "Listen Error: $exception")
                    return@addSnapshotListener
                }
                for (taskChange in snapshot!!.documentChanges) {
                    val task = TaskObject.fromSnapshot(taskChange.document)
                    if (ids.contains(task.id)) {
                        when (taskChange.type) {
                            DocumentChange.Type.ADDED -> {
                                po.projectTasks.add(task)
                                notifyDataSetChanged()
                            }
                            DocumentChange.Type.REMOVED -> {
//                                val pos = teams.indexOfFirst { team.id == it.id }
//                                deleteProjectReferences(team.projectReferences)
//                                teams.removeAt(pos)
//                                notifyItemRemoved(pos)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                val pos = po.projectTasks.indexOfFirst{ task.id == it.id }
                                po.projectTasks[pos] = task
                                notifyDataSetChanged()
//                                getProjectsFromIDs(team.projectReferences, team)
//                                notifyItemChanged(pos)
                            }
                        }
                    }
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavDrawerHolder {
        viewNavDrawer = LayoutInflater.from(context).inflate(R.layout.drawer_card_view, parent, false)
        return NavDrawerHolder(context, viewNavDrawer, this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: NavDrawerHolder, position: Int) {
        holder.bind(teams[position])
    }

    override fun getItemCount(): Int {
        return teams.size
    }

    fun addTeam(team: TeamObject, list: ArrayList<String>, statusList: ArrayList<Int>){
        //Add team to firebase and its reference to the member along with the member's status
        teamsRef.add(team).addOnSuccessListener {
            val nestedData = hashMapOf(it.id to Constants.OWNER)
            val data = hashMapOf(Constants.STATUSES_FIELD to nestedData)
            memberRef.document(userObject?.id!!).set(data, SetOptions.merge())

            //TODO: FIX this error when you create a new team
            if(list.size != 0 && statusList.size != 0) {
                for (i in 0 until statusList.size) {
                    val nestedData2 = if (statusList[i] == 0) {
                        hashMapOf(it.id to Constants.MEMBER)
                    } else {
                        hashMapOf(it.id to Constants.OWNER)
                    }
                    val data2 = hashMapOf(Constants.STATUSES_FIELD to nestedData2)
                    memberRef.document(list[i]).set(data2, SetOptions.merge())
                }
            }
        }
    }

    fun editTeamClicked(adapterPosition: Int)    {
        listener.onEditTeamItemSelected(adapterPosition, this)
    }

    fun getTeamDetails(position: Int): TeamObject    {
        return teams[position]
    }

    fun logout() {
        teams.clear()
        notifyDataSetChanged()
        teamReturnReference?.remove()
        projectReturnReference?.remove()
        taskReturnReference?.remove()
        memberReturnReference?.remove()
    }

    @Suppress("UNCHECKED_CAST")
    fun editTeamAtPosition(position: Int, teamName: String, teamDescription: String,
                           members: ArrayList<String>){
        //Todo: Complete edit team
        teams[position].teamName = teamName
        teams[position].teamDescription = teamDescription

        for(i in 0 until teams[position].teamMemberReferences.size) {
            if(!members.contains(teams[position].teamMemberReferences[i])) {
                memberRef.document(teams[position].teamMemberReferences[i]).get().addOnSuccessListener {
                    val memberObject = MemberObject.fromSnapshot(it)
                    val membersToRemove = (it[Constants.STATUSES_FIELD] as MutableMap<String, String>)
                    membersToRemove.remove(teams[position].id)
                    memberObject.statuses = membersToRemove
                    memberRef.document(memberObject.id).set(memberObject)
                }
            }
        }
        teams[position].teamMemberReferences = members
        teamsRef.document(teams[position].id).set(teams[position])
    }


    fun showCreateProjectModal(childPosition: Int = -1,  project: ProjectObject?, team: TeamObject?) {
        //Check if user has permision to create/edit project within the team
        if(userObject?.statuses?.get(team?.id) == Constants.OWNER) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(if(childPosition==-1) R.string.create_project else R.string.edit_project)
            val view = LayoutInflater.from(context).inflate(R.layout.create_project_modal, null, false)
            builder.setView(view)
            if(childPosition != -1){
                Log.d(Constants.TAG, project!!.projectTitle)
                view.edit_text_project_name.setText(project!!.projectTitle)
                view.edit_text_project_description.setText(project.projectDescription)
                builder.setNeutralButton(R.string.Delete) { _,_ ->
                    confirmDeleteModal(project, team!!)
                }
            }
            builder.setPositiveButton(R.string.Save) { _, _ ->
                if(childPosition == -1) {
                    // Create Project from dialog boxes
                    val addedProject = ProjectObject(view.edit_text_project_name.text.toString(),
                        view.edit_text_project_description.text.toString())
                    //Add project to project collection in firebase
                    projectsRef.add(addedProject).addOnSuccessListener { snapshot: DocumentReference ->
                        //Add project ID to team document in firebase
                        teamsRef.document(team!!.id).update(Constants.PROJECTS_FIELD, FieldValue.arrayUnion(snapshot.id))
                    }
                } else {
                    project?.projectTitle = view.edit_text_project_name.text.toString()
                    project?.projectDescription = view.edit_text_project_description.text.toString()
                    projectsRef.document(project!!.id).set(project)
                }
            }
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.create().show()
        } else {
            Snackbar.make(viewNavDrawer, R.string.no_permission, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun confirmDeleteModal(project: ProjectObject, team: TeamObject)    {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.deleteProject)
        builder.setMessage(R.string.deleteProjectMessage)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            removeProject(project, team)
        }
        builder.setNegativeButton(android.R.string.cancel, null) //Do Nothing
        builder.create().show()
    }

    private fun removeProject(project: ProjectObject, team: TeamObject) {
        //Delete Tasks in task ref
        deleteTaskReferences(project.taskReferences)
        //Delete Project in project ref
        projectsRef.document(project.id).delete().addOnSuccessListener {
            //delete Project from ref in team Ref
            teamsRef.document(team.id).update(Constants.PROJECTS_FIELD, FieldValue.arrayRemove(project.id))
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun delete(position: Int) {
        val teamID = teams[position].id
        memberRef.get().addOnSuccessListener { snapshot: QuerySnapshot ->
            for(snap in snapshot.documents) {
                val statuses = snap[Constants.STATUSES_FIELD] as MutableMap<String, String>
                statuses.remove(teamID)
                memberRef.document(snap.id).update(Constants.STATUSES_FIELD, statuses)
            }
        }
        teamsRef.document(teamID).delete()
    }

    interface OnNavDrawerListener {
        fun onEditTeamItemSelected(position: Int, adapter: NavDrawerAdapter)
    }

}