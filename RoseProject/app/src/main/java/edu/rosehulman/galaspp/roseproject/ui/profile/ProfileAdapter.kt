package edu.rosehulman.galaspp.roseproject.ui.profile

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.Constants
import edu.rosehulman.galaspp.roseproject.R
import kotlin.collections.ArrayList

class ProfileAdapter(
        private var context: Context?
) : RecyclerView.Adapter<ProfileViewHolder>(){

    private val userTeams = ArrayList<ProfileTeamModel>()

    override fun getItemCount() = userTeams.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.profile_row_view, parent, false)
        return ProfileViewHolder(view, this, context)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(viewHolder: ProfileViewHolder, index: Int) {
        viewHolder.bind(userTeams[index])
    }

    fun add(profileTeamModel: ProfileTeamModel){
        userTeams.add(0, profileTeamModel)
        notifyItemInserted(0)
    }

    private fun remove(position: Int){
        userTeams.removeAt(position)
        notifyItemRemoved(position)
    }

    fun openExportDialog(position: Int, hasPermission: Boolean) {
        if(!hasPermission){
            Toast.makeText(context, R.string.no_permission, Toast.LENGTH_SHORT)
            return
        }
//        userTeams[position]
//        val builder = AlertDialog.Builder(context!!)
//        builder.setTitle(R.string.export_dialog_title)
//        builder.setNeutralButton(R.string.No) { _, _ -> }
//        builder.setPositiveButton(R.string.Yes) { _, _ ->
//            Log.d(Constants.TAG, "Export ")
//        }
//        builder.show()
    }

}