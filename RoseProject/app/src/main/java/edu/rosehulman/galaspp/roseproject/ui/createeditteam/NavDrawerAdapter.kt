package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.PointerIcon
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import edu.rosehulman.galaspp.roseproject.Constants
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.project.ProjectObject
import edu.rosehulman.galaspp.roseproject.ui.project.TaskObject
import kotlinx.android.synthetic.main.create_project_modal.view.*

class NavDrawerAdapter (var context: Context, var listener: OnNavDrawerListener) : RecyclerView.Adapter<NavDrawerHolder>() {
    private var teams : ArrayList<TeamObject> = ArrayList()
    var projects = HashMap<String, ArrayList<ProjectObject>>()

    private val teamsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.TEAMS_COLLECTION)
    private val projectsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.PROJECTS_COLLECTION)
    private val tasksRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.TASKS_COLLECTION)

    init {
        teamsRef
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if(exception != null) {
                    Log.e("Nav Drawer Error", "Listen Error: $exception")
                    return@addSnapshotListener
                }
                for(teamChange in snapshot!!.documentChanges) {
                    val team = TeamObject.fromSnapshot(teamChange.document)
                    when(teamChange.type) {
                        DocumentChange.Type.ADDED -> {
                            teams.add(0, team)
                            projects[teams[0].teamName] = ArrayList()
                            notifyItemInserted(0)
                        }
                        DocumentChange.Type.REMOVED -> {
                            val pos = teams.indexOfFirst { team.id == it.id }
                            deleteProjectReferences(team.projectReferences)
                            teams.removeAt(pos)
                            notifyItemRemoved(pos)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val pos = teams.indexOfFirst{ team.id == it.id }
                            teams[pos] = team
                            getProjectsFromIDs(team.projectReferences, team)
                            notifyItemChanged(pos)
                        }
                    }
                }
            }
        teams.clear()
        teamsRef.get().addOnSuccessListener { snapshot2: QuerySnapshot ->
            for(doc in 0 until snapshot2.size()){
                //Get Team DOC
                val tm = TeamObject.fromSnapshot(snapshot2.documents[snapshot2.size() - doc - 1]) //Converts Firebase to TeamObject
//                teams.add(tm) //Add team to list
                projects[tm.teamName] = ArrayList() //Add empty proj list to map with team names

                //Get Projects
                getProjectsFromIDs(tm.projectReferences, tm)
            }
            notifyDataSetChanged()
        }
    }

    private fun deleteProjectReferences(projectReferences: ArrayList<String>) {
        for( refID in projectReferences ){
            projectsRef.document(refID).delete()
        }
    }

    private fun getProjectsFromIDs(projIds : ArrayList<String>, tm : TeamObject) {
        //Get top level projects reference from firebase
        //loop over ids and match with documents from projects reference, then convert and add to lists
//        Log.d("TEST", projIds.toString())
        projectsRef
//                .whereEqualTo("id", projIds)
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
                                    tm.projects.add(0, po)
//                                projects[teams[0].teamName] = ArrayList()
                                    projects[tm.teamName]?.add(po)
                                    getTasksFromIDs(po.taskReferences, po)
//                                notifyItemInserted(0)
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
                                    tm.projects.add(po)
//                                getProjectsFromIDs(team.projectReferences, team)
                                    getTasksFromIDs(po.taskReferences, po)
//                                notifyItemChanged(pos)
                                    notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
//        for (projId in projIds) {
//            projectsRef.document(projId).get().addOnSuccessListener{ snapshot: DocumentSnapshot ->
//                //Add Project to team
//                val po = ProjectObject.fromSnapshot(snapshot)
//                tm.projects.add(po)
//                projects[tm.teamName]?.add(po)
//
//                //Get tasks
//                getTasksFromIDs(po.taskReferences, po)
//                notifyDataSetChanged()
//            }
//        }
    }

    private fun getTasksFromIDs(ids : ArrayList<String>, po : ProjectObject) {
        //Get top level task reference from firebase
        //loop over ids and match with documents from tasks reference, then convert and add to lists
//        tasksRef
//                .whereEqualTo("id", ids)
//                .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
//                    if (exception != null) {
//                        Log.e("Nav Drawer Error", "Listen Error: $exception")
//                        return@addSnapshotListener
//                    }
//                    for (taskChange in snapshot!!.documentChanges) {
//                        val task = TaskObject.fromSnapshot(taskChange.document)
//                        when (taskChange.type) {
//                            DocumentChange.Type.ADDED -> {
//                                po.projectTasks.add(0, task)
//                                notifyItemInserted(0)
//                            }
//                            DocumentChange.Type.REMOVED -> {
////                                val pos = teams.indexOfFirst { team.id == it.id }
////                                deleteProjectReferences(team.projectReferences)
////                                teams.removeAt(pos)
////                                notifyItemRemoved(pos)
//                            }
//                            DocumentChange.Type.MODIFIED -> {
////                                val pos = teams.indexOfFirst{ team.id == it.id }
////                                teams[pos] = team
////                                getProjectsFromIDs(team.projectReferences, team)
////                                notifyItemChanged(pos)
//                            }
//                        }
//                    }
//                }
        for (id in ids) {
            tasksRef.document(id).get().addOnSuccessListener{snapshot: DocumentSnapshot ->
                val task = TaskObject.fromSnapshot(snapshot)
                po.projectTasks.add(task)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavDrawerHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.drawer_card_view, parent, false)
        return NavDrawerHolder(context, view, this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: NavDrawerHolder, position: Int) {
        holder.bind(teams[position])
    }

    override fun getItemCount(): Int {
        return teams.size
    }

    fun addTeam(team: TeamObject){
        teamsRef.add(team)
    }

    fun editTeamClicked(adapterPosition: Int)    {
        listener.onEditTeamItemSelected(adapterPosition, this)
    }

    fun getTeamDetails(position: Int): TeamObject    {
        return teams[position]
    }

    fun getListOfProjects(position: Int): ArrayList<ProjectObject> {
        //TODO: re-evaluate
//        return teams[position].projects
        return ArrayList<ProjectObject>()
    }

    fun editTeamAtPosition(position: Int, teamName: String, teamDescription: String,
                           members: ArrayList<MemberObject>, projects: ArrayList<ProjectObject>){
        //Todo: Complete edit team
//        teams[position].teamName = teamName
//        teams[position].teamDescription = teamDescription
//        teams[position].members = members
//        teams[position].projects = projects
//        this.projects[teams[position].teamName] = projects

//        teamRef.document(teams[position].id).set(teams[position])
//        notifyItemChanged(position)
    }

    fun showCreateProjectModal(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Create Project? (Admin Only)")
        val view = LayoutInflater.from(context).inflate(R.layout.create_project_modal, null, false)
        builder.setView(view)
        builder.setPositiveButton("Save") { _, _ ->
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
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()
    }

    interface OnNavDrawerListener {
        fun onEditTeamItemSelected(position: Int, adapter: NavDrawerAdapter)
    }

}