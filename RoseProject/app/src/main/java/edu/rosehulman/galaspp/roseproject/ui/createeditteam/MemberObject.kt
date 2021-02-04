package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude


data class MemberObject(
        var userName : String = "",
        var name : String = "",
        var id : String = ""
//        var teamsReferences : ArrayList<String> = ArrayList(),
//        var statuses : Map<String, String> = mapOf(),
) {
//    @get: Exclude var id = ""
    @get: Exclude var teams = ArrayList<TeamObject>()
    companion object{
        fun fromSnapshot(snapshot: DocumentSnapshot): MemberObject {
            val member = snapshot.toObject(MemberObject::class.java)!!
            member.id = snapshot.id
            return member
        }
    }
}