package com.example.ar_liveimages

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import com.google.ar.core.ArCoreApk
import com.google.ar.core.exceptions.UnavailableException

fun isARCoreSupportedAndUpToDate(activity: Activity): Boolean {
    return when (ArCoreApk.getInstance().checkAvailability(activity.applicationContext)) {
        ArCoreApk.Availability.SUPPORTED_INSTALLED -> true
        ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD, ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {

            try {
                // Request ARCore installation or update if needed.
                when (ArCoreApk.getInstance().requestInstall(activity, true)) {
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                        Log.i(TAG, "ARCore installation requested.")
                        false
                    }
                    ArCoreApk.InstallStatus.INSTALLED -> true
                }
            } catch (e: UnavailableException) {
                Log.e(TAG, "ARCore not installed", e)
                false
            }
        }

        ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE ->
            // This device is not supported for AR.
            false

        else -> false
    }
}