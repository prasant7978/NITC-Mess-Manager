package com.nitc.nitcmessmanager.model

data class Contractor(val contractorId : Long = 0,
                      val contractorEmail : String = "",
                      val userType : String = "",
                      val contractorName : String = "",
                      val costPerDay : Long = 0,
                      val messName : String = "",
                      val foodType : String = "",
                      val capacity : Long = 0,
                      val availability : Long = 0,
                      val feedbackReceived : ArrayList<Feedback> = ArrayList()) {
}