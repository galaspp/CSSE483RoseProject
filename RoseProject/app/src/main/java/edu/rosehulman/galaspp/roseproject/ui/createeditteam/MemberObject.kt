package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MemberObject(
        var userName : String = "",
        var name : String = "",
        var id : String = "",
        var statuses : Map<String, String> = mapOf()
) :Parcelable {
    @get: Exclude var teams = ArrayList<TeamObject>()
    companion object{
        fun fromSnapshot(snapshot: DocumentSnapshot): MemberObject {
            val member = snapshot.toObject(MemberObject::class.java)!!
            member.id = snapshot.id
            return member
        }
    }
}