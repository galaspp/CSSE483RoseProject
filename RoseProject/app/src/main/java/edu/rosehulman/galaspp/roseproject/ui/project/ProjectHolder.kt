package edu.rosehulman.galaspp.roseproject.ui.project

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.project_name_card_view.view.*

class ProjectHolder: RecyclerView.ViewHolder {
    val projectTitleTextView: Button = itemView.project_button

    constructor(itemView: View, adapter: ProjectAdapter): super(itemView) {

    }

    fun bind(project: ProjectObject)
    {
        projectTitleTextView.text = project.projectTitle
    }
}