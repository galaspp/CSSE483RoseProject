package edu.rosehulman.galaspp.roseproject.ui.profile

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.galaspp.roseproject.AuthenticationListener
import edu.rosehulman.galaspp.roseproject.FragmentListener
import edu.rosehulman.galaspp.roseproject.R
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


private const val ARG_PROFILE = "profile"

class ProfileFragment : Fragment() {

    private var profile: ProfileModel? = null
    private lateinit var adapter: ProfileAdapter


    companion object {
        var listener: AuthenticationListener? = null
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
            profile = it.getParcelable(ARG_PROFILE)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        //Populate fields
        view.name_text_view.text = profile?.name
        view.username_text_view.text = profile?.username

        //Set listener for logout button
        view.logout_button.setOnClickListener {
            showSignOutDialog(context)
        }

        //Create recycler view for list of teams
        val recyclerView = view.findViewById<RecyclerView>(R.id.profile_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        adapter = ProfileAdapter(context)
        recyclerView.adapter = adapter

        return view
    }

    private fun showSignOutDialog(context : Context?) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(R.string.logout_confirmation)
        builder.setNeutralButton("NO") { _, _ -> }
        builder.setPositiveButton("YES") { _, _ ->
            listener?.signOut()
        }
        builder.show()
    }

}