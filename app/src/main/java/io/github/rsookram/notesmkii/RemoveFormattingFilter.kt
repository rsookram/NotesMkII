package io.github.rsookram.notesmkii

import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.SuperscriptSpan
import android.text.style.URLSpan

/**
 * [InputFilter] which removes spans used for formatting that may appear in pasted text, while
 * preserving spans added by IMEs
 */
class RemoveFormattingFilter : InputFilter {

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        if (source !is Spanned) return null

        if (source.hasSpan<BulletSpan>() &&
            source.hasSpan<SuperscriptSpan>() &&
            source.hasSpan<URLSpan>()
        ) {
            return null
        }

        return SpannableStringBuilder(source, start, end).apply {
            removeSpans<BulletSpan>()
            removeSpans<SuperscriptSpan>()
            removeSpans<URLSpan>()
        }
    }

    private inline fun <reified T> Spanned.hasSpan(): Boolean =
        nextSpanTransition(0, length, T::class.java) == length

    private inline fun <reified T> SpannableStringBuilder.removeSpans() {
        getSpans(0, length, T::class.java).forEach(::removeSpan)
    }
}
