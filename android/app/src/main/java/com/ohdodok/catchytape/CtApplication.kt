package com.ohdodok.catchytape

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class CtApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {

                override fun createStackElementTag(element: StackTraceElement): String {
                    return String.format(
                        getString(R.string.timber_log_format),
                        super.createStackElementTag(element),
                        element.lineNumber,
                    )
                }
            })
        }
    }
}