package edu.rosehulman.galaspp.roseproject.ui.project

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import edu.rosehulman.galaspp.roseproject.ui.createeditteam.TeamObject

data class TaskObject (
    var name : String = "",
    var assignedTo : String = "",
    var urgency: Int = 0,
    var currentStatus: Int = 0,
    var hours: Double = 0.0,
    var projectTaskLog: ArrayList<String> = ArrayList()
) {
    @get: Exclude var id = ""
    companion object {
        //        const val LAST_TOUCHED_KEY = "lastTouched"
        fun fromSnapshot(snapshot: DocumentSnapshot): TaskObject {
            val task = snapshot.toObject(TaskObject::class.java)!!
            task.id = snapshot.id
            return task
        }
    }
}
