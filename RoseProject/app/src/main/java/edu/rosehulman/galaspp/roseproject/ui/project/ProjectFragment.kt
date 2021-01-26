package edu.rosehulman.galaspp.roseproject.ui.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.profile.ProfileAdapter
import kotlinx.android.synthetic.main.add_remove_members_modal.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_project.view.*


private const val ARG_PROJECT = "project"

class ProjectFragment : Fragment() {

    private var project: ProjectObject? = null
    private lateinit var adapter: ProjectAdapter

    companion object {
        @JvmStatic
        fun newInstance(project: ProjectObject) =
                ProjectFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PROJECT, project)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            project = it.getParcelable<ProjectObject>(ARG_PROJECT)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_project, container, false)

        //Title
        view.project_title.text = project?.projectTitle

        //Spinner
        val arrayVal = resources.getStringArray(R.array.task_status_array)
        val aa = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, arrayVal)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.project_fragment_spinner.adapter = aa

        val recyclerView = view.findViewById<RecyclerView>(R.id.project_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        adapter = ProjectAdapter(context)
        recyclerView.adapter = adapter

        return view
    }
}