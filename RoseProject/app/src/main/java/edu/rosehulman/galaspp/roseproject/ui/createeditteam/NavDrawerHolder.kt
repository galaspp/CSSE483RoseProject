package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ExpandableListView
import android.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.Constants
import edu.rosehulman.galaspp.roseproject.FragmentListener
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.CustomExpandableListAdapter
import edu.rosehulman.galaspp.roseproject.ui.project.ProjectFragment
import kotlinx.android.synthetic.main.drawer_card_view.view.*

class NavDrawerHolder(var context: Context, itemView: View, var adapter: NavDrawerAdapter) : RecyclerView.ViewHolder(itemView) {
    var view: View = itemView
    lateinit var listAdapter: CustomExpandableListAdapter
    lateinit var expListView: ExpandableListView
    var listDataHeader: List<String>? = null
    var listDataChild: HashMap<String, List<String>>? = null
    private val DEFAULT_HEIGHT = 92
    lateinit var team: TeamObject

    init {
        val dropDownMenu = PopupMenu(itemView.context, itemView.card_options_button)
        val menu = dropDownMenu.menu
        menu.add(0, 0, 0, R.string.create_project)
        menu.add(0, 1, 0, R.string.edit_team)
        dropDownMenu.menuInflater.inflate(R.menu.team_menu_options, menu)
        dropDownMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                0 -> {
                    adapter.showCreateProjectModal(-1, null, team)
                    true
                }
                1 -> {
                    adapter.editTeamClicked(adapterPosition)
                    true
                }
                else -> false
            }
        }
        itemView.card_options_button.setOnClickListener {
            dropDownMenu.show()
        }
    }

    fun bind(team: TeamObject){
        this.team = team
        expListView = view.findViewById(R.id.expandable_list_view) as ExpandableListView
        // preparing list data
        listDataHeader = ArrayList()
        (listDataHeader as ArrayList<String>).add("       " + team.teamName)//TODO: fix spacing

        listDataChild = HashMap()
        val projectNames: MutableList<String> = ArrayList()

        for(p in team.projects){
            projectNames.add(p.projectTitle)
        }

        listDataChild!![(listDataHeader as ArrayList<String>)[0]] = projectNames// Header, Child data

        //Set list adapter
        listAdapter = CustomExpandableListAdapter(context, listDataHeader, listDataChild)
        expListView.setAdapter(listAdapter)

        //Adjust size based on # of projects in view and if group is expanded
        expListView.collapseGroup(0)
        val card : CardView = view.drawer_card_view
        val numProjects = team.projects.size
        //Add on click listeners to adjust size
        expListView.setOnGroupExpandListener {
            card.layoutParams.height = DEFAULT_HEIGHT +
                    expListView[0].height * numProjects
        }
        expListView.setOnGroupCollapseListener {
            card.layoutParams.height = DEFAULT_HEIGHT
        }
        expListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val childName = listAdapter.getChild(groupPosition, childPosition).toString()
//            Log.d("test", "You clicked on project $childName!")
            val project = team.projects[childPosition]
            val projectName = team.projects[childPosition].projectTitle
            adapter.userObject?.let { ProjectFragment.newInstance(project, it.id, team.id) }?.let {
                (context as FragmentListener)
                    .openFragment(it, true, projectName)
            }
            false
        }

        expListView.setOnItemLongClickListener { parent, view, position, id ->
//            Log.d(Constants.TAG, "position $position")
            val index = position - 1
            if(index < 0){
                adapter.showCreateProjectModal(index, null, team)
            } else {
                adapter.showCreateProjectModal(index, team.projects[position - 1], team)

            }
            true
        }
    }
}
