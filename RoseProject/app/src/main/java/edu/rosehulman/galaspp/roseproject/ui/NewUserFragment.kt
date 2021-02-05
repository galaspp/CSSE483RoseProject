package edu.rosehulman.galaspp.roseproject.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import edu.rosehulman.galaspp.roseproject.Constants
import edu.rosehulman.galaspp.roseproject.FragmentListener
import edu.rosehulman.galaspp.roseproject.R
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_new_user.view.*

class NewUserFragment(
        private var listener: FragmentListener,
        private var membersRef: CollectionReference,
        private val uid: String,
        private val app_bar_view: AppBarLayout
        ) : Fragment() {

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_user, container, false)

        app_bar_view.isVisible = false

        //Allows user to set profile picture
        view.profile_button_view.setOnClickListener {
            //TODO: Find out how to import picture from phone gallery
            Log.d(Constants.TAG, "Learn how to import pics and save to firebase")
        }

        //Set Listener for done button
        view.done_button.setOnClickListener {
            //Get entered Username text
            val usernameText = view.edit_username_view.text.toString()
            //Look for any member object with usernameText as a field
            membersRef.whereEqualTo(Constants.USERNAME_FIELD, usernameText)
                    .get().addOnSuccessListener { snapshot : QuerySnapshot ->
                        if(!snapshot.isEmpty){
                            //Change color to red and notify user to redo username
                            val color = ContextCompat.getColor(context!!, R.color.red)
                            view.edit_username_view.setTextColor(color)
                            Toast.makeText(
                                    context!!,
                                    "This username already exists. Please enter a new one.",
                                    Toast.LENGTH_SHORT).show()
                        } else {
                            //Save information to firebase object
                            // TODO: Add a members object listener somewhere
                            val firstName = view.edit_first_name_view.text.toString()
                            val lastName = view.edit_last_name_view.text.toString()
                            membersRef.document(uid).update(Constants.NAME_FIELD, "$firstName $lastName")
                            membersRef.document(uid).update(Constants.USERNAME_FIELD, usernameText)
                            app_bar_view.isVisible = true
                            //TODO: Beyond this point is complete tom-foolery
                            //There is a bug where the current fragment doesn't fully disappear
                            listener.openFragment(WelcomeFragment(usernameText), false, "welcome")
                            listener.removeCurrentFragment()
                        }
                    }
        }
        return view
    }
}