package edu.rosehulman.galaspp.roseproject.ui.profile

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R
import kotlin.collections.ArrayList

class ProfileAdapter(
        private var context: Context?,
) : RecyclerView.Adapter<ProfileViewHolder>(){

    private val userTeams = ArrayList<ProfileTeamModel>()

    override fun getItemCount() = userTeams.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.profile_row_view, parent, false)
        return ProfileViewHolder(view, this, context)
    }

    init{
        add(ProfileTeamModel("GPE", "Admin"))
        add(ProfileTeamModel("Catalyst", "Owner"))
        add(ProfileTeamModel("Life", "Member"))
        add(ProfileTeamModel("Rose", "Member"))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(viewHolder: ProfileViewHolder, index: Int) {
        viewHolder.bind(userTeams[index])
    }

    private fun add(profileTeamModel: ProfileTeamModel){
        userTeams.add(profileTeamModel)
        notifyDataSetChanged()
    }

    private fun remove(position: Int){
        userTeams.removeAt(position)
        notifyItemRemoved(position)
    }

}