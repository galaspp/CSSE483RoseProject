package edu.rosehulman.galaspp.roseproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.rosehulman.galaspp.roseproject.FragmentListener
import edu.rosehulman.galaspp.roseproject.ui.profile.ProfileAdapter
import edu.rosehulman.galaspp.roseproject.R

class WelcomeFragment() : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if(context is FragmentListener) {
            (context as FragmentListener).fab.show()
        }
        return  inflater.inflate(R.layout.fragment_welcome, container, false)
    }
}