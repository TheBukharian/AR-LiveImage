package com.example.ar_liveimages.view.fragments

import android.util.Log
import com.example.ar_liveimages.MainActivity
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment


class MainFragment : ArFragment() {

    override fun getSessionConfiguration(session: Session): Config {
        planeDiscoveryController.setInstructionView(null)
        val config = Config(session)
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        session.configure(config)
        arSceneView.setupSession(session)
        if ((activity as MainActivity?)!!.setupAugmentedImagesDB(
                config,
                session
            )
        ) Log.d("arcoreimg_db", "success") else Log.e("arcoreimg_db", "faliure setting up db")
        return config
    }
}