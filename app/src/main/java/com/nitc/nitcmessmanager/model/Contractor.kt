package com.nitc.nitcmessmanager.model

data class Contractor(var contractorId : String = "",
                      var contractorEmail : String = "",
                      var contractorPassword : String = "",
                      var userType : String = "",
                      var contractorName : String = "",
                      var costPerDay : Int = 0,
                      var messName : String = "",
                      var foodType : String = "",
                      var capacity : Int = 0,
                      var availability : Int = 0,
                      var feedbackReceived : ArrayList<Feedback> = ArrayList(),
                      var studentEnrolled : ArrayList<Student> = ArrayList()
) {
}