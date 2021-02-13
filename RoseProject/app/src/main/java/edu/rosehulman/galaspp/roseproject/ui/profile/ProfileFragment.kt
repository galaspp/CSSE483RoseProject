package edu.rosehulman.galaspp.roseproject.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import edu.rosehulman.galaspp.roseproject.AuthenticationListener
import edu.rosehulman.galaspp.roseproject.Constants
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.MemberObject
import kotlinx.android.synthetic.main.fragment_profile.view.*


private const val ARG_USER = "user"
class ProfileFragment : Fragment() {

    private var user: MemberObject? = null
    private lateinit var adapter: ProfileAdapter
    private val membersRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.MEMBER_COLLECTION)

    companion object {
        var listener: AuthenticationListener? = null
        @JvmStatic
        fun newInstance(user: MemberObject) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USER, user)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable(ARG_USER)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        //Populate fields
//        membersRef.document(user?.id!!).get().addOnSuccessListener {
//            view.name_text_view.text = it.get(Constants.USERNAME_FIELD) as String
//            view.username_text_view.text = it.get(Constants.NAME_FIELD) as String
//        }
        view.name_text_view.text = user?.name
        view.username_text_view.text = user?.userName


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

        Log.d(Constants.TAG, "User(${user?.name}) teams size: ${user?.teams?.size}")
        for(team in user!!.teams){
            //TODO:Fix - should not automatically set status to owner
            adapter.add(ProfileTeamModel(team.teamName, user!!.statuses[team.id] ?: error("")))
        }

        return view
    }

    @SuppressLint("UseRequireInsteadOfGet")
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