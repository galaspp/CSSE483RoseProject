package edu.rosehulman.galaspp.roseproject.ui.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.FragmentListener
import edu.rosehulman.galaspp.roseproject.R
import kotlinx.android.synthetic.main.fragment_project.view.*


private const val ARG_PROJECT = "project"
private const val ARG_TEAM = "team"
private const val ARG_USER = "user"
private const val ARG_TYPE = "type"

class ProjectFragment : Fragment() {

    private var project: ProjectObject? = null
    private lateinit var adapter: ProjectAdapter
    private lateinit var userID: String
    private lateinit var teamID: String
    private var taskType: Int = 0

    companion object {
        @JvmStatic
        fun newInstance(project: ProjectObject, userID: String, teamID: String, taskType: Int = 0) =
            ProjectFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PROJECT, project)
                    putString(ARG_USER, userID)
                    putString(ARG_TEAM, teamID)
                    putInt(ARG_TYPE, taskType)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            project = it.getParcelable<ProjectObject>(ARG_PROJECT)
            userID = it.getString(ARG_USER).toString()
            teamID = it.getString(ARG_TEAM).toString()
            taskType = it.getInt(ARG_TYPE)
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
        val aa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayVal)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.project_fragment_spinner.adapter = aa
        view.project_fragment_spinner.setSelection(taskType)

        val recyclerView = view.findViewById<RecyclerView>(R.id.project_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        adapter = project?.let { ProjectAdapter(requireContext(), it, userID, teamID) }!!
        recyclerView.adapter = adapter

        view.project_fragment_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    adapter.setFilter(position)
            }
        }

        if(context is FragmentListener) {
            (context as FragmentListener).fab.setOnClickListener {
                adapter.showCreateorEditTaskModal()
            }
            (context as FragmentListener).fab.show()
        }
        return view
    }
}