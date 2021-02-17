package edu.rosehulman.galaspp.roseproject.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import edu.rosehulman.galaspp.roseproject.*
import edu.rosehulman.galaspp.roseproject.ui.PictureHelper
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.MemberObject
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.TeamObject
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.io.ByteArrayOutputStream
import kotlin.random.Random


private const val ARG_USER = "user"
@SuppressLint("UseRequireInsteadOfGet")
class ProfileFragment (val fragmentListener : FragmentListener) : Fragment(), PictureHelper.PictureListener {

    private var userID: String? = null
    private lateinit var adapter: ProfileAdapter
    private lateinit var pictureHelper: PictureHelper
    private val membersRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.MEMBER_COLLECTION)
    private val teamsRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.TEAMS_COLLECTION)
    private val storageRef = FirebaseStorage
            .getInstance()
            .reference
            .child(Constants.IMAGES)

    companion object {
        var listener: AuthenticationListener? = null
        var hasPicture = false
        @JvmStatic
        fun newInstance(userID: String , fragmentListener : FragmentListener) =
                ProfileFragment(fragmentListener).apply {
                    arguments = Bundle().apply {
                        putString(ARG_USER, userID)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userID = it.getString(ARG_USER)
        }
        fragmentListener.fab.hide()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.profile_image.setOnClickListener {
            pictureHelper.getPicture()
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
        adapter.userTeams.clear()

        membersRef.document(userID!!).addSnapshotListener{
            documentSnapshot: DocumentSnapshot?,
            firebaseFirestoreException: FirebaseFirestoreException? ->

            //Populate fields
            view.name_text_view.text = documentSnapshot?.get(Constants.NAME_FIELD).toString()
            view.username_text_view.text = documentSnapshot?.get(Constants.USERNAME_FIELD).toString()

            //Set image button
            val photourl = documentSnapshot?.get(Constants.PHOTOID)
            if(photourl!=null && photourl.toString().isNotEmpty()){
                Picasso.get().load(photourl.toString()).into(view.profile_image)
            }

            val teamIDs = (documentSnapshot?.get(Constants.STATUSES_FIELD) as Map<String, String>)

            for (teamID in teamIDs.keys) {
                teamsRef.document(teamID).get().addOnSuccessListener { teamSnap: DocumentSnapshot ->
                    val to = TeamObject.fromSnapshot(teamSnap)
                    adapter.add(ProfileTeamModel(to.teamName, teamIDs[teamID] ?: "Member"))
                }
            }
        }

        //TODO: Possibly refactor as to not send "this" twice
        pictureHelper = PictureHelper(context!!, activity!!, this, this)
        return view
    }

    private fun showSignOutDialog(context: Context?) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(R.string.logout_confirmation)
        builder.setNeutralButton(R.string.No) { _, _ -> }
        builder.setPositiveButton(R.string.Yes) { _, _ ->
            listener?.signOut()
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        pictureHelper.onActivityResult(requestCode, resultCode, data)
    }

    override fun getPictureTask(localPath: String) {
        ImageRescaleTask(localPath).execute()
        //Set boolean to reopen profile fragement on main activity restart
        hasPicture = true
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
                membersRef.document(userID!!).set(map, SetOptions.merge())
            }
        }
    }
}