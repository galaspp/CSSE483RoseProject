package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R
import kotlinx.android.synthetic.main.create_edit_team_card_view.view.*

class CreateEditTeamHolder: RecyclerView.ViewHolder {
    val textView: TextView = itemView.create_edit_team_card_text_view
    val permissionsView: TextView = itemView.create_edit_team_permission_card_text_view
    var view: View

    constructor(itemView: View, adapter: CreateEditTeamAdapter): super(itemView) {
        view = itemView
        view.setBackgroundColor(view.resources.getColor(R.color.backgroundcolor))
    }

    fun bind(textofitem: MemberObject) {
        textView.text = textofitem.userName
//        permissionsView.text = textofitem.permissions
        view.setBackgroundColor(view.resources.getColor(R.color.backgroundcolor))
    }
}