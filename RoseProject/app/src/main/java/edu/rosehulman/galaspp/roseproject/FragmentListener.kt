package edu.rosehulman.galaspp.roseproject

import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.MemberObject

interface FragmentListener {
    var fab: FloatingActionButton
    fun openFragment(fragment: Fragment, addToBackStack: Boolean = true, name: String = "fragment_name")
    fun openProfile(userID: String)
    fun removeCurrentFragment()
    fun getUpdatedUser(): MemberObject?
    fun openProjectFromHome(teamID: String, projID: String, taskType : Int)
}