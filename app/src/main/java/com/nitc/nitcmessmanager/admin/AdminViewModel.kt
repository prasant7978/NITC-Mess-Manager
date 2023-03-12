package com.nitc.nitcmessmanager.admin

import androidx.lifecycle.ViewModel
import com.nitc.nitcmessmanager.model.Feedback
import com.nitc.nitcmessmanager.model.Student

class AdminViewModel: ViewModel() {

    lateinit var feedbackList:ArrayList<Feedback>
    lateinit var studentEnrolledList:ArrayList<Student>

}