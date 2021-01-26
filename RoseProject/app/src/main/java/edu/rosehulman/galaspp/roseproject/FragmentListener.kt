package edu.rosehulman.galaspp.roseproject

import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

interface FragmentListener {
    var fab: FloatingActionButton
    fun openFragment(fragment: Fragment, addToBackStack: Boolean = true, name: String = "fragment_name")
}