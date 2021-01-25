package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.project.ProjectAdapter
import kotlinx.android.synthetic.main.drawer_card_view.view.*

class NavDrawerHolder(itemView: View, adapter: NavDrawerAdapter) : RecyclerView.ViewHolder(itemView) {
    val textView: TextView = itemView.drawer_card_view_text_view
    val imageButton: ImageButton = itemView.drop_down_button
    val recyclerViewItem: RecyclerView = itemView.drawer_item_recycler_view
    var projectAdapter: ProjectAdapter = ProjectAdapter(itemView.context)
    var view: View = itemView
    var buttonDropdownState: Int = 0

    init {
        val dropDownMenu = PopupMenu(itemView.context, itemView.card_options_button)
        val menu = dropDownMenu.menu
        menu.add(0, 0, 0, "Create Project")
        menu.add(0, 1, 0, "Edit Team")
        dropDownMenu.menuInflater.inflate(R.menu.team_menu_options, menu)
        dropDownMenu.setOnMenuItemClickListener {
             when (it.itemId) {
                0 -> {
                    projectAdapter.showCreateProjectModal()
                    true
                }
                1-> {
                    adapter.editTeamClicked(adapterPosition)
                    true
                }
                else -> false
            }
        }
        itemView.card_options_button.setOnClickListener {
           dropDownMenu.show()
        }
        imageButton.setOnClickListener {
            if(buttonDropdownState == 0)
            {
                recyclerViewItem.visibility = VISIBLE
                imageButton.setImageResource(R.drawable.ic_arrow_down_24)
                buttonDropdownState = 1
            }
            else
            {
                recyclerViewItem.visibility = GONE
                imageButton.setImageResource(R.drawable.ic_baseline_navigate_next_24)
                buttonDropdownState = 0
            }
        }
        recyclerViewItem.adapter = projectAdapter
        recyclerViewItem.layoutManager = LinearLayoutManager(itemView.context)
        recyclerViewItem.setHasFixedSize(true)
    }

    fun bind(team: TeamObject)
    {
        textView.text = team.teamName
    }
}
