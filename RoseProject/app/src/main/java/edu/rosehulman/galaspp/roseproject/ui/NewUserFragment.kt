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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import edu.rosehulman.galaspp.roseproject.Constants
import edu.rosehulman.galaspp.roseproject.FragmentListener
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.MemberObject
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_new_user.view.*

class NewUserFragment(
        private var listener: FragmentListener,
        private val user: MemberObject,
        private val app_bar_view: AppBarLayout
        ) : Fragment() {

    private val membersRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.MEMBER_COLLECTION)

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

        //Prepopulate fields if data exists
        if(user.name != ""){
            //Returns the substring of the name before and after the space
            view.edit_first_name_view.setText(user.name.substringBefore(" "))
            view.edit_last_name_view.setText(user.name.substringAfter(" "))
        }
        if(user.id.length < 20){
            //Set the username to userID (this only works for Rosefire login b/c userID=username)
            view.edit_username_view.setText(user.id)
        }

        //Set Listener for done button
        view.done_button.setOnClickListener {
            //Get entered  text
            val firstName = view.edit_first_name_view.text.toString()
            val lastName = view.edit_last_name_view.text.toString()
            val usernameText = view.edit_username_view.text.toString()
            if(usernameText.isEmpty() || lastName.isEmpty() || firstName.isEmpty()) {
                Toast.makeText(
                    context!!,
                    "Fields cannot be empty. Please re-enter your information.",
                    Toast.LENGTH_SHORT).show()
            } else{
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
                            // TODO: Add a members object listener somewhere i think idk
                            membersRef.document(user.id).update(Constants.NAME_FIELD, "$firstName $lastName")
                            membersRef.document(user.id).update(Constants.USERNAME_FIELD, usernameText)
                            app_bar_view.isVisible = true
//                            listener.removeCurrentFragment()//Used to make the fragments not overlap upon reloading welcome screen
                            listener.openFragment(WelcomeFragment(usernameText), false, "welcome")
                        }
                    }
            }
        }
        return view
    }
}