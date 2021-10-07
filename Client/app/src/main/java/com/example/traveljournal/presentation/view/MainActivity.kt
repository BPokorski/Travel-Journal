package com.example.traveljournal.presentation.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.traveljournal.R
import com.example.traveljournal.data.SessionManager
import com.example.traveljournal.data.retrofit.TravelJournalService
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var editLogin: EditText
    private lateinit var editPassword: EditText
    private lateinit var signInButton:Button
    private lateinit var travelJournalService: TravelJournalService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Navigator.navigateToLogin(this)

       var sessionManager = SessionManager(this.applicationContext)
        sessionManager.removeSavedData()
        setLocale("en")
    }

    fun buttonClicked(view:View) {
        when(view.id) {
            R.id.map_button -> Navigator.navigateToMap(this)
            R.id.photo_button -> Navigator.navigateToCamera(this)
            R.id.gallery_button -> Navigator.navigateToGallery(this)
        }
    }

    private fun setLocale(languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
    }
}