package edu.rosehulman.galaspp.roseproject.ui.createeditteam

data class TeamObject(var teamName: String = "", var teamDescription: String = "", var members: ArrayList<MemberObject> = ArrayList<MemberObject>()) {
}