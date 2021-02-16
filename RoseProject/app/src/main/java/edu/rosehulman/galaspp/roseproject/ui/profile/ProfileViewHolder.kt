package edu.rosehulman.galaspp.roseproject.ui.profile

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R
import kotlinx.android.synthetic.main.profile_row_view.view.*


class ProfileViewHolder : RecyclerView.ViewHolder {
    private val teamTitleTextView: TextView = itemView.team_name_text_view
    private lateinit var context: Context
    private var hasPermission : Boolean = false

    constructor(itemView: View, adapter: ProfileAdapter, context: Context?):  super(itemView){
        this.context = context!!
        itemView.setOnClickListener{
            adapter.openExportDialog(adapterPosition, hasPermission)
        }
        itemView.setOnLongClickListener {
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun bind(teamModel: ProfileTeamModel) {
        teamTitleTextView.text = teamModel.team_name
        hasPermission = teamModel.status == "Admin"
                || teamModel.status == "Owner"
                || teamModel.status == "admin"
                || teamModel.status == "owner"
        if(hasPermission){
            val color = ContextCompat.getColor(context, R.color.rosered)
            teamTitleTextView.setTextColor(color)
        }
    }



}