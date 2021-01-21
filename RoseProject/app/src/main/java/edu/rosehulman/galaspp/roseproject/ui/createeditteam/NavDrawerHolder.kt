package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R
import kotlinx.android.synthetic.main.create_edit_team_card_view.view.*
import kotlinx.android.synthetic.main.drawer_card_view.view.*

class NavDrawerHolder: RecyclerView.ViewHolder {
    val textView: TextView = itemView.drawer_card_view_text_view
    var view: View

    constructor(itemView: View, adapter: NavDrawerAdapter): super(itemView) {
        view = itemView
    }

    fun bind(team: TeamObject)
    {
        textView.text = team.teamName
    }
}
