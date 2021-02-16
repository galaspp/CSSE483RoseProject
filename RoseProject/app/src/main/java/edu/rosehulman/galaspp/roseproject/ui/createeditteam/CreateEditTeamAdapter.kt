package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R

class CreateEditTeamAdapter(var context: Context) : RecyclerView.Adapter<CreateEditTeamHolder>() {
    private var listofusernames : ArrayList<MemberObject> = ArrayList()
    private var listOfIds : ArrayList<String> = ArrayList()
    private var listOfMemberStatus : ArrayList<Int> = ArrayList()

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

    fun addName(name: String, permissions: String)
    {
        listofusernames.add(0, MemberObject(name, permissions))
        notifyItemInserted(0)
    }

    fun getListOfMembers(): ArrayList<MemberObject> {
        return listofusernames
    }

    fun setListOfMembers(list: ArrayList<MemberObject>) {
        listofusernames = list
        notifyDataSetChanged()
    }

    fun addMember(memberObject: MemberObject, memberObjectID: String, memberStatus: Int)
    {
        if(listOfIds.contains(memberObjectID))
        {
            val position = listOfIds.indexOfFirst { it == memberObjectID }
            listOfMemberStatus[position] = memberStatus
            listofusernames[position] = memberObject
            listOfIds[position] = memberObjectID
            notifyDataSetChanged()
        }
        else {
            listofusernames.add(0, memberObject)
            listOfIds.add(0, memberObjectID)
            listOfMemberStatus.add(0, memberStatus)
            notifyItemInserted(0)
        }
    }

    fun getMemberObjectIds() :ArrayList<String>
    {
        return listOfIds
    }

    fun getMemberStatusList(): ArrayList<Int>
    {
        return listOfMemberStatus
    }

    fun removeName(name: String)
    {
        for(i in 0 until listofusernames.size) {
            if(listofusernames.get(i).userName == name) {
                listofusernames.removeAt(i)
                listOfIds.removeAt(i)
                listOfMemberStatus.removeAt(i)
                break
            }
        }
        notifyDataSetChanged()
    }
}