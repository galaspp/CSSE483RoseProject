package edu.rosehulman.galaspp.roseproject.ui.profile

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.CreateEditTeamAdapter
import kotlinx.android.synthetic.main.create_edit_team_card_view.view.*

class CreateEditTeamHolder: RecyclerView.ViewHolder {
    val textView: TextView = itemView.create_edit_team_card_text_view
    var view: View

    constructor(itemView: View, adapter: CreateEditTeamAdapter): super(itemView) {
        view = itemView
    }

    fun bind(textofitem: String) {
        textView.text = textofitem
//        view.setBackgroundColor(view.resources.getColor(R.color.backgroundcolor))
    }
}