package edu.rosehulman.galaspp.roseproject.ui.project

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.NavDrawerAdapter
import kotlinx.android.synthetic.main.create_project_modal.view.*

class ProjectAdapter(var context: Context): RecyclerView.Adapter<ProjectHolder>() {

    private var listOfProjects: ArrayList<ProjectObject> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.project_name_card_view, parent, false)
        return ProjectHolder(view, this)
    }

    override fun onBindViewHolder(holder: ProjectHolder, position: Int) {
        holder.bind(listOfProjects[position])
    }

    override fun getItemCount(): Int {
        return listOfProjects.size
    }

    fun addProject(projectObject: ProjectObject)
    {
        Log.d("TAG", projectObject.toString())
        listOfProjects.add(0, projectObject)
        notifyItemInserted(0)
    }

    fun addProjectData(projectData: ArrayList<ProjectObject>)
    {
        listOfProjects = projectData
        notifyDataSetChanged()
    }

    fun showCreateProjectModal()
    {
        val builder = AlertDialog.Builder(context)
        //TODO: Change title based on whether editing or creating Project
        //TODO: Prepopulate items as needed
        builder.setTitle("Create Project? (Admin Only)")

        val view = LayoutInflater.from(context).inflate(R.layout.create_project_modal, null, false)
        builder.setView(view)

        builder.setPositiveButton("Save") { _, _ ->
            addProject(ProjectObject(
                    view.edit_text_project_name.text.toString(),
                    view.edit_text_project_description.text.toString()
            ))
        }

        builder.setNegativeButton(android.R.string.cancel, null)

        builder.create().show()
    }
}