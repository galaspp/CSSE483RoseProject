package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ExpandableListView
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
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

    init {
        val dropDownMenu = PopupMenu(itemView.context, itemView.card_options_button)
        val menu = dropDownMenu.menu
        menu.add(0, 0, 0, "Create Project")
        menu.add(0, 1, 0, "Edit Team")
        dropDownMenu.menuInflater.inflate(R.menu.team_menu_options, menu)
        dropDownMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                0 -> {
                    adapter.showCreateProjectModal(adapterPosition)
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

//    @RequiresApi(Build.VERSION_CODES.Q)
    fun bind(team: TeamObject){
        expListView = view.findViewById(R.id.expandable_list_view) as ExpandableListView
        // preparing list data
        listDataHeader = ArrayList()
        (listDataHeader as ArrayList<String>).add("       " + team.teamName)//TODO: fix spacing

        listDataChild = HashMap()
        val projectNames: MutableList<String> = ArrayList()
//        for(p in adapter.projects[team.teamName]!!){
//            projectNames.add(p.projectTitle)
//        }
        for(p in team.projects){
            projectNames.add(p.projectTitle)
        }

        listDataChild!![(listDataHeader as ArrayList<String>)[0]] = projectNames// Header, Child data

        //Set list adapter
        listAdapter = CustomExpandableListAdapter(context, listDataHeader, listDataChild)
        expListView.setAdapter(listAdapter)
//        expListView.bottomEdgeEffectColor = ContextCompat.getColor(context, R.color.white)

        //Adjust size based on # of projects in view and if group is expanded
        val card : CardView = view.drawer_card_view
//        val numProjects = adapter.projects[team.teamName]?.size
        val numProjects = team.projects.size
        //Add on click listeners to adjust size
        expListView.setOnGroupExpandListener {
//            Log.d("test", "You expanded the thing!")
//            card.layoutParams.height = DEFAULT_HEIGHT +
//                    expListView[0].height * numProjects!!
            card.layoutParams.height = DEFAULT_HEIGHT +
                    expListView[0].height * numProjects
        }
        expListView.setOnGroupCollapseListener {
//            Log.d("test", "You collapsed the thing!")
            card.layoutParams.height = DEFAULT_HEIGHT
        }

        expListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val childName = listAdapter.getChild(groupPosition, childPosition).toString()
            Log.d("test", "You clicked on project $childName!")
//            val project = adapter.projects[team.teamName]!![childPosition]
            val project = team.projects[childPosition]
//            val projectName = adapter.projects[team.teamName]!![childPosition].projectTitle
            val projectName = team.projects[childPosition].projectTitle
            (context as FragmentListener)
                .openFragment(ProjectFragment.newInstance(project), true, projectName)
            false
        }
    }
}
