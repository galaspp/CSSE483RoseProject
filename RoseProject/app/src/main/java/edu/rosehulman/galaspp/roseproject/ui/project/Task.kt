package edu.rosehulman.galaspp.roseproject.ui.project

data class Task (var name : String,
                 var assignedTo : String,
                 var urgency: Int,
                 var currentStatus: Int,
                 var hours: Double,
                 var projectTaskLog: ArrayList<String>
){
    var status: Int = -1
}
