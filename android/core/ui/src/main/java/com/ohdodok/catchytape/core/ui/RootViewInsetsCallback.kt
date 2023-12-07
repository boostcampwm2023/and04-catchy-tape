package com.ohdodok.catchytape.core.ui

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsCompat

class RootViewInsetsCallback : androidx.core.view.OnApplyWindowInsetsListener {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onApplyWindowInsets(view: View, insets: WindowInsetsCompat): WindowInsetsCompat {
        val systemBar = WindowInsetsCompat.Type.systemBars()

        val typeInsets = insets.getInsets(systemBar)
        view.setPadding(typeInsets.left, typeInsets.top, typeInsets.right, 0)

        return WindowInsetsCompat.CONSUMED
    }
}