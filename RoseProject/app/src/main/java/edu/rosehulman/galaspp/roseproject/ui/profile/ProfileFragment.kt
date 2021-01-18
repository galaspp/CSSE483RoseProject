package edu.rosehulman.galaspp.roseproject.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.ui.profile.ProfileAdapter
import edu.rosehulman.galaspp.roseproject.R

class ProfileFragment : Fragment() {

    private lateinit var adapter: ProfileAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val recycleView = inflater.inflate(R.layout.profile_recycler_view, container, false) as RecyclerView

        adapter = ProfileAdapter(context)
        recycleView.layoutManager = LinearLayoutManager(context)
//        recycleView.setHasFixedSize(true)
        recycleView.adapter = adapter

        return recycleView
    }
}