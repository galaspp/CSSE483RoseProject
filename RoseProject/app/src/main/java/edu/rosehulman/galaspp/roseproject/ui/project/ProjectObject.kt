package edu.rosehulman.galaspp.roseproject.ui.project

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProjectObject(var projectTitle: String = "",
                         var projectDescription: String = ""
): Parcelable
{
    var projectTasks: ArrayList<Task> = ArrayList()
}