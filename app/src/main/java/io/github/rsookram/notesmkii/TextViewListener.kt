package io.github.rsookram.notesmkii

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

/**
 * Invokes the given [action] when text is changing
 */
inline fun TextView.doOnTextChanged(crossinline action: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) =
            Unit

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            action.invoke(text?.toString().orEmpty())
        }

        override fun afterTextChanged(s: Editable?) = Unit
    })
}
