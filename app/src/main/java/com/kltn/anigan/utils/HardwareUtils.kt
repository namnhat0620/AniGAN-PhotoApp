package com.kltn.anigan.utils;

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

public class HardwareUtils {
    companion object

    {
        @SuppressLint("HardwareIds")
        fun getMobileId(context: Context): String {
            return Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }
    }
}
