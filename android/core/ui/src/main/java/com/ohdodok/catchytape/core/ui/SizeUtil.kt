package com.ohdodok.catchytape.core.ui

import android.content.Context

fun Context.fromDpToPx(dp: Float): Float {
    val density = resources.displayMetrics.density
    return dp * density
}