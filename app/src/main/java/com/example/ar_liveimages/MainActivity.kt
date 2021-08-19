package com.example.ar_liveimages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Config
import com.google.ar.core.Session

class MainActivity : AppCompatActivity() {
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }

    override fun onPause() {
        super.onPause()
        if (this::session.isInitialized)
        {
            session.close()
        }
    }

    fun createSession() {
        // Create a new ARCore session.
        session = Session(this)

        // Create a session config.
        val config = Config(session)

        // Do feature-specific operations here, such as enabling depth or turning on
        // support for Augmented Faces.

        // Configure the session.
        session.configure(config)
    }



}