package edu.rosehulman.galaspp.roseproject.ui.project

data class Task (var name : String, var assignedTo : String, var urgency: Int){
    var status: Int = -1
}
