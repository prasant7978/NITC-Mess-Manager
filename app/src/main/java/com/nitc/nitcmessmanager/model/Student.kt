package com.nitc.nitcmessmanager.model

data class Student(var studentId : String = "",
                   var studentName : String = "",
                   var studentEmail : String = "",
                   var studentPassword : String = "",
                   var studentRollNo : String = "",
                   var userType : String = "",
                   var messBill : Int = 0,
                   var paymentStatus : String = "",
                   var messEnrolled : String = "") {
}