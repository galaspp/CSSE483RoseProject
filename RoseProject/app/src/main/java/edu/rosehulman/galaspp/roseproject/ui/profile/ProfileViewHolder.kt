package edu.rosehulman.galaspp.roseproject.ui.profile

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.ui.profile.ProfileAdapter
import edu.rosehulman.galaspp.roseproject.ui.profile.ProfileTeamModel
import kotlinx.android.synthetic.main.profile_row_view.view.*


class ProfileViewHolder : RecyclerView.ViewHolder {
    private val teamTitleTextView: TextView = itemView.team_name_text_view
    private val statusTextView: TextView = itemView.member_status_text_view
    private val exportButton: TextView = itemView.export_button_view
    private var context: Context?

    constructor(itemView: View, adapter: ProfileAdapter, context: Context?):  super(itemView){
        this.context = context
        itemView.setOnClickListener{

        }
        itemView.setOnLongClickListener {
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun bind(teamModel: ProfileTeamModel) {
        teamTitleTextView.text = teamModel.team_name
        statusTextView.text = teamModel.status
        exportButton.isEnabled = (teamModel.status == "Status") || teamModel.status == "Owner"
    }



}