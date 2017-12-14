package com.ab.texteditor

import android.annotation.SuppressLint
import android.content.Context

/**
 * Created by: anirban on 18/11/17.
 */
class Utils {
    companion object {


        @SuppressLint("HardwareIds")
        fun getDeviceIdForPushNotification(context: Context): String {
            return android.provider.Settings.Secure.getString(
                    context.contentResolver, android.provider.Settings.Secure.ANDROID_ID)
        }
    }
}