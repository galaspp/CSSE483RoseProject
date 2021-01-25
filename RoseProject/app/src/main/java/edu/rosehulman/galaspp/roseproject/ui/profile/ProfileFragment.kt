package edu.rosehulman.galaspp.roseproject.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.R
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


private const val ARG_PROFILE = "profile"

class ProfileFragment : Fragment() {

    private var profile: ProfileModel? = null
    private lateinit var adapter: ProfileAdapter

    companion object {
        @JvmStatic
        fun newInstance(profile: ProfileModel) =
                ProfileFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PROFILE, profile)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            profile = it.getParcelable<ProfileModel>(ARG_PROFILE)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        //Populate
        view.name_text_view.text = profile?.name
        view.username_text_view.text = profile?.username

//        val recycleView = inflater.inflate(R.layout.profile_recycler_view, container, false) as RecyclerView
//        adapter = ProfileAdapter(context)
//        recycleView.layoutManager = LinearLayoutManager(context)
//        recycleView.setHasFixedSize(true)
//        recycleView.adapter = adapter

        val recyclerView = view.findViewById<RecyclerView>(R.id.profile_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        adapter = ProfileAdapter(context)
        recyclerView.adapter = adapter

        return view
    }
}