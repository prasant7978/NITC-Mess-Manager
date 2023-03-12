package com.nitc.nitcmessmanager.model

data class Contractor(val contractorId : String = "",
                      val contractorEmail : String = "",
                      val contractorPassword : String = "",
                      val userType : String = "",
                      val contractorName : String = "",
                      val costPerDay : Int = 0,
                      val messName : String = "",
                      val foodType : String = "",
                      val capacity : Int = 0,
                      val availability : Int = 0,
                      val feedbackReceived : ArrayList<Feedback> = ArrayList(),
                      val studentEnrolled : ArrayList<Student> = ArrayList()
) {
}