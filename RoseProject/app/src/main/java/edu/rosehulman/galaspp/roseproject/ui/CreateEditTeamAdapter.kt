package edu.rosehulman.galaspp.roseproject.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.profile.CreateEditTeamHolder

class CreateEditTeamAdapter(var context: Context) : RecyclerView.Adapter<CreateEditTeamHolder>() {
    private var listofusernames : ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateEditTeamHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.create_edit_team_card_view, parent, false)
        return CreateEditTeamHolder(view, this)
    }

    override fun onBindViewHolder(holder: CreateEditTeamHolder, position: Int) {
        holder.bind(listofusernames[position])
    }

    override fun getItemCount(): Int {
        return listofusernames.size
    }
}