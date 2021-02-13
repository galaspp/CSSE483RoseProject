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
//    var projects = HashMap<String, ArrayList<ProjectObject>>()

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
        teamReturnReference = teamsRef
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
                Log.e("Nav Drawer Error", "Listen Error: $exception")
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

    private fun getProjectsFromIDs(projIds : ArrayList<String>, tm : TeamObject) {
//    private fun getProjectsFromIDs(position: Int) {
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
//                        Log.d("TEST", teams[position].teamName)
                        if(projIds.contains(po.id)) {
                            when (projChange.type) {
                                DocumentChange.Type.ADDED -> {
//                                    Log.d("TEST", po.projectTitle)
                                    getTasksFromIDs(po.taskReferences, po)
                                    tm.projects.add(0, po)
                                    notifyDataSetChanged()
                                }
                                DocumentChange.Type.REMOVED -> {
//                                val pos = teams.indexOfFirst { team.id == it.id }
//                                deleteProjectReferences(team.projectReferences)
//                                teams.removeAt(pos)
//                                notifyItemRemoved(pos)
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    val pos = tm.projects.indexOfFirst { po.id == it.id }
                                    tm.projects[pos] = po
//                                getProjectsFromIDs(team.projectReferences, team)
                                    getTasksFromIDs(po.taskReferences, po)
                                    notifyItemChanged(pos)
//                                    notifyDataSetChanged()
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
//                .whereEqualTo("id", ids)
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
//                                val pos = teams.indexOfFirst{ team.id == it.id }
//                                teams[pos] = team
//                                getProjectsFromIDs(team.projectReferences, team)
//                                notifyItemChanged(pos)
                                }
                            }
                        }
                    }
                }
//        for (id in ids) {
//            tasksRef.document(id).get().addOnSuccessListener{snapshot: DocumentSnapshot ->
//                val task = TaskObject.fromSnapshot(snapshot)
//                po.projectTasks.add(task)
//                notifyDataSetChanged()
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavDrawerHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.drawer_card_view, parent, false)
//        return NavDrawerHolder(context, view, this)
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
//                    var nestedData: HashMap<String, String>
                    var nestedData = if (statusList[i] == 0) {
                        hashMapOf(it.id to Constants.MEMBER)
                    } else {
                        hashMapOf(it.id to Constants.OWNER)
                    }
                    val data = hashMapOf(Constants.STATUSES_FIELD to nestedData)
                    memberRef.document(list[i]).set(data, SetOptions.merge())
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
        teamReturnReference?.remove()
        projectReturnReference?.remove()
        taskReturnReference?.remove()
        memberReturnReference?.remove()
    }

    private fun removeProject(itemId : String) {
        var itemFound = false
        for (tm in teams)
        {
            for(po in tm.projects)
            {
                if(po.id == itemId)
                {
                    var taskItems = po.taskReferences
                    projectsRef.document(itemId).delete().addOnSuccessListener {
                        for(task in taskItems)
                        {
                            tasksRef.document(task).delete()
                        }
                        teamsRef.document(tm.id).update("projectReferences", FieldValue.arrayRemove(itemId))
                    }
                    itemFound = true
                    break
                }
            }
            if(itemFound)
                break
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun editTeamAtPosition(position: Int, teamName: String, teamDescription: String,
                           members: ArrayList<String>){
        //Todo: Complete edit team
        teams[position].teamName = teamName
        teams[position].teamDescription = teamDescription

        for(i in 0 until teams[position].teamMemberReferences.size)
        {
            if(!members.contains(teams[position].teamMemberReferences[i]))
            {
                memberRef.document(teams[position].teamMemberReferences[i]).get().addOnSuccessListener {
                    val memberObject = MemberObject.fromSnapshot(it)
                    val membersToRemove = (it[Constants.STATUSES_FIELD] as MutableMap<String, String>)
                    membersToRemove.remove(teams[position].id)
                    memberObject.statuses = membersToRemove
//                    val data = hashMapOf(Constants.STATUSES_FIELD to membersToRemove)
                    memberRef.document(memberObject.id).set(memberObject)
                }
            }
        }
        teams[position].teamMemberReferences = members
//        teams[position].projects = projects
//        this.projects[teams[position].teamName] = projects

        teamsRef.document(teams[position].id).set(teams[position])
//        notifyItemChanged(position)
    }


    fun showCreateProjectModal(position: Int, childPosition: Int = -1,  projects: ArrayList<ProjectObject> = ArrayList()) {
        val builder = AlertDialog.Builder(context)
        if(childPosition == -1)
            builder.setTitle("Create Project? (Admin Only)")
        else
            builder.setTitle("Edit Project? (Admin Only)")

        val view = LayoutInflater.from(context).inflate(R.layout.create_project_modal, null, false)
        if(childPosition != -1)
        {
            view.edit_text_project_name.setText(projects[childPosition].projectTitle)
            view.edit_text_project_description.setText(projects[childPosition].projectDescription)
        }
        builder.setView(view)
        builder.setPositiveButton("Save") { _, _ ->
            if(childPosition == -1) {
                // Create Project from dialog boxes
                val addedProject = ProjectObject(view.edit_text_project_name.text.toString(),
                        view.edit_text_project_description.text.toString())
                //Add project to project collection in firebase
                projectsRef.add(addedProject)
                        .addOnSuccessListener { snapshot: DocumentReference ->
                            //Add project ID to team document in firebase
                            teamsRef.document(teams[position].id).update(Constants.PROJECTS_FIELD, FieldValue.arrayUnion(snapshot.id))
                        }
            }
            else
            {
                var addedProject = projects[childPosition]
                addedProject.projectTitle = view.edit_text_project_name.text.toString()
                addedProject.projectDescription = view.edit_text_project_description.text.toString()
   
                projectsRef.document(projects[childPosition].id).set(addedProject)
            }
        }
        builder.setNegativeButton(android.R.string.cancel, null)

        if(childPosition != -1) {
            builder.setNeutralButton("Delete") { _,_ ->
                confirmDeleteModal(projects[childPosition].id)
            }
        }

        userObject?.let {
            if(userObject?.statuses?.get(teams[position].id) == Constants.OWNER)
            {
                builder.create().show()
            }
            else
            {
                Snackbar.make(viewNavDrawer, "You do not have this permission!", Snackbar.LENGTH_LONG).show()
            }

        } ?: run{
            Snackbar.make(viewNavDrawer, "You do not have this permission!", Snackbar.LENGTH_LONG).show()
        }
//        val parentLayout = android.R.id.content as View
//        Snackbar.make(parentLayout, "You do not have this permission!", Snackbar.LENGTH_LONG).show()
//        builder.create().show()
    }

    private fun confirmDeleteModal(itemId : String)
    {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.deleteProject)
        builder.setMessage(R.string.deleteProjectMessage)

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            removeProject(itemId)
        }

        builder.setNegativeButton(android.R.string.cancel, null) //Do Nothing

        builder.create().show()
    }

    interface OnNavDrawerListener {
        fun onEditTeamItemSelected(position: Int, adapter: NavDrawerAdapter)
    }

}