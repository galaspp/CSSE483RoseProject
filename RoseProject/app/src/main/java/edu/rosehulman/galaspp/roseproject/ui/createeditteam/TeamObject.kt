package edu.rosehulman.galaspp.roseproject.ui.createeditteam

import edu.rosehulman.galaspp.roseproject.ui.project.ProjectObject

data class TeamObject(var teamName: String = "",
                      var teamDescription: String = "",
                      var members: ArrayList<MemberObject> = ArrayList<MemberObject>(),
                      var projects: ArrayList<ProjectObject> = ArrayList()
) {
}