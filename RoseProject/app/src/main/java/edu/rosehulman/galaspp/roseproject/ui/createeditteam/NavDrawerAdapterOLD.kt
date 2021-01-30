package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.project.ProjectObject
import kotlinx.android.synthetic.main.create_project_modal.view.*

class NavDrawerAdapterOLD (var context: Context, var listener: NavDrawerAdapter.OnNavDrawerListener){
//
//    : RecyclerView.Adapter<NavDrawerHolder>() {
//    private var allTeams : ArrayList<TeamObject> = ArrayList()
//    var projects = HashMap<String, ArrayList<ProjectObject>>()
//
//    private val teamRef = FirebaseFirestore
//        .getInstance()
//        .collection("teams")
//
//    init {
//        teamRef
//            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
//                if(exception != null) {
//                    Log.e("NAVDRAWERERROR", "Listen Error: $exception")
//                    return@addSnapshotListener
//                }
//                for(teamChange in snapshot!!.documentChanges) {
//                    val team = TeamObject.fromSnapshot(teamChange.document)
//                    when(teamChange.type) {
//                        DocumentChange.Type.ADDED -> {
////                            Log.d("NAVDRAWERERROR", "Team: $team")
//                            allTeams.add(0, team)
//                            projects[allTeams[0].teamName] = ArrayList()
//                            notifyItemInserted(0)
//                        }
//                        DocumentChange.Type.REMOVED -> {
////                            val pos = allTeams.indexOfFirst { team.id == it.id }
//                            //TODO: Finish this
//                        }
//                        DocumentChange.Type.MODIFIED -> {
//                            //TODO: Finish this
////                            val pos = allTeams.indexOfFirst{ team.id == it.id}
//                            allTeams[pos] = team
//                            notifyItemChanged(pos)
//                        }
//                    }
//                }
//            }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavDrawerHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.drawer_card_view, parent, false)
//        return NavDrawerHolder(context, view, this)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.Q)
//    override fun onBindViewHolder(holder: NavDrawerHolder, position: Int) {
//        holder.bind(allTeams[position])
//    }
//
//    override fun getItemCount(): Int {
//        return allTeams.size
//    }
//
//    fun addTeam(newTeam: TeamObject)
//    {
////        allTeams.add(0, newTeam)
//        newTeam.projects = ArrayList()
//        teamRef.add(newTeam)
//        projects[allTeams[0].teamName] = ArrayList()
//        notifyItemInserted(0)
//    }
//
//    fun editTeamClicked(adapterPosition: Int)
//    {
//        listener.onEditTeamItemSelected(adapterPosition, this)
//    }
//
//    fun getTeamDetails(position: Int): TeamObject
//    {
//        return allTeams[position]
//    }
//
//    fun getListOfProjects(position: Int): ArrayList<ProjectObject> {
//        return allTeams[position].projects
//    }
//
//    fun editTeamAtPosition(position: Int, teamName: String, teamDescription: String, members: ArrayList<MemberObject>, projects: ArrayList<ProjectObject>)
//    {
//        allTeams[position].teamName = teamName
//        allTeams[position].teamDescription = teamDescription
//        allTeams[position].members = members
//        allTeams[position].projects = projects
//        this.projects[allTeams[position].teamName] = projects
//
////        teamRef.document(allTeams[position].id).set(allTeams[position])
////        notifyItemChanged(position)
//    }
//
//    fun showCreateProjectModal(position: Int)
//    {
//        val builder = AlertDialog.Builder(context)
//        //TODO: Change title based on whether editing or creating Project
//        //TODO: Prepopulate items as needed
//        builder.setTitle("Create Project? (Admin Only)")
//
//        val view = LayoutInflater.from(context).inflate(R.layout.create_project_modal, null, false)
//        builder.setView(view)
//
//        builder.setPositiveButton("Save") { _, _ ->
//            val projectlist = projects[allTeams[position].teamName]
//            projectlist?.add(ProjectObject(view.edit_text_project_name.text.toString(),
//                    view.edit_text_project_description.text.toString()))
//
//            if (projectlist != null) {
//                editTeamAtPosition(position, allTeams[position].teamName,  allTeams[position].teamDescription,  allTeams[position].members, projectlist)
//            }
////            notifyItemChanged(position)
//        }
//        builder.setNegativeButton(android.R.string.cancel, null)
//        builder.create().show()
//    }
//
//    interface OnNavDrawerListener {
//        fun onEditTeamItemSelected(position: Int, adapter: NavDrawerAdapterOLD)
//    }

}