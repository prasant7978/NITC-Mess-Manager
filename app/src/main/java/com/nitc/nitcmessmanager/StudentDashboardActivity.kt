package com.nitc.nitcmessmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.R
import com.nitc.nitcmessmanager.databinding.ActivityStudentDashboardBinding

class StudentDashboardActivity : AppCompatActivity() {

    lateinit var dashboardBinding: ActivityStudentDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dashboardBinding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        val view = dashboardBinding.root

        setContentView(view)


    }
}