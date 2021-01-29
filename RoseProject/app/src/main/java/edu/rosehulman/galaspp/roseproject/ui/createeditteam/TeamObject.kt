package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import edu.rosehulman.galaspp.roseproject.ui.project.ProjectObject

data class TeamObject(var teamName: String = "",
                      var teamDescription: String = "",
                      var members: ArrayList<MemberObject> = ArrayList<MemberObject>(),
                      var projects: ArrayList<ProjectObject> = ArrayList()
) {
    @get: Exclude
    var id = ""
    @ServerTimestamp
    var lastTouched: Timestamp? = null
    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"
        fun fromSnapshot(snapshot: DocumentSnapshot): TeamObject {
            Log.d("TEST", "${snapshot["teamName"]}")
            val team = snapshot.toObject(TeamObject::class.java)
            team!!.id = snapshot.id
            return team
        }
    }
}