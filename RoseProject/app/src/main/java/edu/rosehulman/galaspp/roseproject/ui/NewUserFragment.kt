package edu.rosehulman.galaspp.roseproject.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import edu.rosehulman.galaspp.roseproject.*
import edu.rosehulman.galaspp.roseproject.R
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.MemberObject
import edu.rosehulman.galaspp.roseproject.ui.homepage.WelcomeFragment
import kotlinx.android.synthetic.main.fragment_new_user.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.io.ByteArrayOutputStream
import kotlin.random.Random

@SuppressLint("UseRequireInsteadOfGet")
class NewUserFragment(
        private var listener: FragmentListener,
        private val user: MemberObject,
        private val app_bar_view: AppBarLayout
        ) : Fragment(), PictureHelper.PictureListener {

    private lateinit var pictureHelper: PictureHelper
    private val membersRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.MEMBER_COLLECTION)
    private val storageRef = FirebaseStorage
        .getInstance()
        .reference
        .child("images")

    companion object {
        var hasPicture = false
        var firstGlobal = ""
        var lastGlobal = ""
        var userGlobal = ""
    }

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
        } else{
            view.edit_first_name_view.setText(firstGlobal)
            view.edit_last_name_view.setText(lastGlobal)
        }
        if(user.id.length < 20){
            //TODO: This is bad code I know - Cam
            //Set the username to userID (this only works for Rosefire login b/c userID=username)
            view.edit_username_view.setText(user.id)
        } else {
            view.edit_username_view.setText(userGlobal)
        }
        //Set image button
        membersRef.document(user!!.id).addSnapshotListener{
                documentSnapshot: DocumentSnapshot?,
                firebaseFirestoreException: FirebaseFirestoreException? ->
            val photourl = documentSnapshot?.get(Constants.PHOTOID)
            if(photourl!=null){
                Picasso.get().load(photourl.toString()).into(view.profile_button_view)
                user.photoID = photourl.toString()
            }
        }
        view.profile_button_view.setOnClickListener {
            //Save textbox data
            firstGlobal = view.edit_first_name_view.text.toString()
            lastGlobal = view.edit_last_name_view.text.toString()
            userGlobal = view.edit_username_view.text.toString()
            //Launch Camera Intent and set hasPicture to return to new user fragment
            hasPicture = true
            pictureHelper.getPicture()
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
                            membersRef.document(user.id).update(Constants.NAME_FIELD, "$firstName $lastName")
                            membersRef.document(user.id).update(Constants.USERNAME_FIELD, usernameText)
                            app_bar_view.isVisible = true
//                            listener.removeCurrentFragment()//Used to make the fragments not overlap upon reloading welcome screen
                            listener.openFragment(WelcomeFragment(user, listener), false, "welcome")
                        }
                    }
            }
        }

        pictureHelper = PictureHelper(context!!, activity!!, this, this)
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        pictureHelper.onActivityResult(requestCode, resultCode, data)
    }

    override fun getPictureTask(localPath: String) {
        Log.d(Constants.TAG, "get pic task in newuwres")
        ImageRescaleTask(localPath).execute()
    }

    inner class ImageRescaleTask(val localPath: String) : AsyncTask<Void, Void, Bitmap>() {
        override fun doInBackground(vararg p0: Void?): Bitmap? {
            // Reduces length and width by a factor (currently 2).
            val ratio = 2
            return BitmapUtils.rotateAndScaleByRatio(context!!, localPath, ratio)
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            // https://firebase.google.com/docs/storage/android/upload-files
            storageAdd(bitmap)
        }
    }
    private fun storageAdd(bitmap: Bitmap?) {
        //Delete old picture
        if(user?.photoID!=null){
            FirebaseStorage.getInstance().getReferenceFromUrl(user?.photoID!!).delete()
        }
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val id = Math.abs(Random.nextLong()).toString()
        val uploadTask = storageRef.child(id).putBytes(data)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if(!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation storageRef.child(id).downloadUrl
        }).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val downloadUri = task.result
                val map = hashMapOf(Constants.PHOTOID to downloadUri.toString())
                membersRef.document(user!!.id).set(map, SetOptions.merge())
            }
        }
    }
}