package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.profile.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.create_edit_team_card_view.view.*
import kotlinx.android.synthetic.main.drawer_card_view.view.*

class NavDrawerHolder: RecyclerView.ViewHolder {
    val textView: TextView = itemView.drawer_card_view_text_view
    var view: View

    constructor(itemView: View, adapter: NavDrawerAdapter): super(itemView) {
        view = itemView

        val dropDownMenu = PopupMenu(itemView.context, itemView.card_options_button)
        val menu = dropDownMenu.menu
        menu.add(0, 0, 0, "Create Project")
        menu.add(0, 0, 0, "Edit Team")
        dropDownMenu.menuInflater.inflate(R.menu.team_menu_options, menu)

        dropDownMenu.setOnMenuItemClickListener {
             when (it.itemId) {
                0 -> {
                    
                    true
                }
                1-> {

                    true
                }
                else -> false
            }
        }
        itemView.card_options_button.setOnClickListener {
           dropDownMenu.show()
        }
    }

    fun bind(team: TeamObject)
    {
        textView.text = team.teamName
    }
}
