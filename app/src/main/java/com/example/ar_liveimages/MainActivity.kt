package com.example.ar_liveimages

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private val MIN_OPENGL_VERSION = 3.0
    private var isAdded = false
    var arFragment: ArFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkIsSupportedDeviceOrFinish(this)) return

        setContentView(com.example.ar_liveimages.R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(com.example.ar_liveimages.R.id.ux_fragment) as ArFragment?
        arFragment!!.planeDiscoveryController.hide()
        arFragment!!.arSceneView.scene.addOnUpdateListener { frametime: FrameTime ->
            onUpateFrame()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun onUpateFrame() {
        val frame = arFragment!!.arSceneView.arFrame
        val augmentedimages = frame!!.getUpdatedTrackables(
            AugmentedImage::class.java
        )
        for (augmentedimage in augmentedimages) {
            if (augmentedimage.trackingState == TrackingState.TRACKING) {
                if (augmentedimage.name == "code_image" && !isAdded) {
                    placeObject(
                        arFragment,
                        augmentedimage.createAnchor(augmentedimage.centerPose),
                        com.example.ar_liveimages.R.raw.mario
                    )
                    isAdded = true
                }
            }
        }
    }

    fun setupAugmentedImagesDB(config: Config, session: Session?): Boolean {
        val bitmap = loadAugmentedImage() ?: return false
        val augmentedImageDatabase = AugmentedImageDatabase(session)
        augmentedImageDatabase.addImage("code_image", bitmap)
        config.augmentedImageDatabase = augmentedImageDatabase
        return true
    }

    private fun loadAugmentedImage(): Bitmap? {
        try {
            assets.open("code_image.jpg").use { `is` -> return BitmapFactory.decodeStream(`is`) }
        } catch (e: IOException) {
            Log.e("arcoreimage", "io exception", e)
        }
        return null
    }

    private fun placeObject(arFragment: ArFragment?, anchor: Anchor, uri: Int) {
        ModelRenderable.builder()
            .setSource(arFragment!!.context, uri)
            .build()
            .thenAccept { modelRenderable: ModelRenderable ->
                addNodeToScene(
                    arFragment,
                    anchor,
                    modelRenderable
                )
            }
            .exceptionally { throwable: Throwable ->
                Toast.makeText(
                    arFragment.context,
                    "Error:" + throwable.message,
                    Toast.LENGTH_LONG
                ).show()
                null
            }
    }

    private fun addNodeToScene(arFragment: ArFragment?, anchor: Anchor, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(arFragment!!.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }

    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later")
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG)
                .show()
            activity.finish()
            return false
        }
        val openGlVersionString =
            (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later")
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                .show()
            activity.finish()
            return false
        }
        return true
    }
}