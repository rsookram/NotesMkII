package io.github.rsookram.notesmkii

import android.app.Activity
import android.view.View
import android.view.WindowInsets

fun Activity.applySystemUiVisibility(toolbar: View, content: View) {
    window.setDecorFitsSystemWindows(false)

    toolbar.setOnApplyWindowInsetsListener { v, insets ->
        val systemInsets = insets.getInsets(WindowInsets.Type.systemBars())
        with(systemInsets) {
            v.setPadding(left, top, right, 0)
        }

        insets
    }

    content.setOnApplyWindowInsetsListener { v, insets ->
        val padding = resources.getDimensionPixelSize(R.dimen.content_padding)
        val systemInsets = insets.getInsets(WindowInsets.Type.systemBars())
        with(systemInsets) {
            v.setPadding(padding + left, padding, padding + right, padding + bottom)
        }

        insets
    }
}
