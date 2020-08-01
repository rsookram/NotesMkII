package io.github.rsookram.notesmkii

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View

fun Activity.applySystemUiVisibility(toolbar: View, content: View) {
    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }

    toolbar.setOnApplyWindowInsetsListener { v, insets ->
        with (insets) {
            v.setPadding(systemWindowInsetLeft, systemWindowInsetTop, systemWindowInsetRight, 0)
        }

        insets
    }

    content.setOnApplyWindowInsetsListener { v, insets ->
        val padding = resources.getDimensionPixelSize(R.dimen.content_padding)
        with (insets) {
            v.setPadding(
                padding + systemWindowInsetLeft,
                padding,
                padding + systemWindowInsetRight,
                padding + systemWindowInsetBottom
            )
        }

        insets
    }
}
