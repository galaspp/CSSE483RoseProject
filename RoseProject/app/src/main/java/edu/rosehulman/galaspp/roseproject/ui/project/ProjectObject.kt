package edu.rosehulman.galaspp.roseproject.ui.project

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProjectObject(
    var projectTitle: String = "",
    var projectDescription: String = "",
    var taskReferences: ArrayList<String> = ArrayList()
): Parcelable
{
    @get: Exclude var projectTasks: ArrayList<TaskObject> = ArrayList()
    @get: Exclude var id = ""

    companion object{
        fun fromSnapshot(snapshot: DocumentSnapshot): ProjectObject {
            val proj = snapshot.toObject(ProjectObject::class.java)!!
            proj.id = snapshot.id
            return proj
        }
    }

}