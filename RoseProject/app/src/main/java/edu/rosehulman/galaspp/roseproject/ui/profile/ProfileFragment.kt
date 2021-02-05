package edu.rosehulman.galaspp.roseproject.ui.profile

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import edu.rosehulman.galaspp.roseproject.AuthenticationListener
import edu.rosehulman.galaspp.roseproject.Constants
import edu.rosehulman.galaspp.roseproject.FragmentListener
import edu.rosehulman.galaspp.roseproject.R
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


private const val ARG_PROFILE = "profile"
private const val ARG_UID = "uid"

class ProfileFragment : Fragment() {

    private var profile: ProfileModel? = null
    private var uid: String? = null
    private lateinit var adapter: ProfileAdapter
    private val membersRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.MEMBER_COLLECTION)


    companion object {
        var listener: AuthenticationListener? = null
        @JvmStatic
        fun newInstance(profile: ProfileModel, uid: String) =
                ProfileFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PROFILE, profile)
                        putString(ARG_UID, uid)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            profile = it.getParcelable(ARG_PROFILE)
            uid = it.getString(ARG_UID)
        }


    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        //Populate fields
        membersRef.document(uid!!).get().addOnSuccessListener {
            view.name_text_view.text = it.get(Constants.USERNAME_FIELD) as String
            view.username_text_view.text = it.get(Constants.NAME_FIELD) as String
        }

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