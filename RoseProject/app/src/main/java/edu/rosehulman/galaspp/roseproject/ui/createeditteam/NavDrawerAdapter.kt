package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R

class NavDrawerAdapter (var context: Context) : RecyclerView.Adapter<NavDrawerHolder>() {
    private var allTeams : ArrayList<TeamObject> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavDrawerHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.drawer_card_view, parent, false)
        return NavDrawerHolder(view, this)
    }

    override fun onBindViewHolder(holder: NavDrawerHolder, position: Int) {
        holder.bind(allTeams[position])
    }

    override fun getItemCount(): Int {
        return allTeams.size
    }

    fun addTeam(newTeam: TeamObject)
    {
        allTeams.add(0, newTeam)
        notifyItemInserted(0)
    }

    interface OnNavDrawerListener {
        fun onEditTeamItemSelected()
    }

}