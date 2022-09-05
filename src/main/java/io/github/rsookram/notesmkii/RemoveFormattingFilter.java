package io.github.rsookram.notesmkii;

import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.URLSpan;

/**
 * {@link InputFilter} which removes spans used for formatting that may appear in pasted text, while
 * preserving spans added by IMEs
 */
public class RemoveFormattingFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source,
                               int start,
                               int end,
                               Spanned dest,
                               int dstart,
                               int dend) {
        if (!(source instanceof Spanned)) {
            return null;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(source, start, end);
        Object[] spans = builder.getSpans(0, builder.length(), Object.class);

        boolean changed = false;
        for (Object span : spans) {
            if (span instanceof BulletSpan
                    || span instanceof SuperscriptSpan
                    || span instanceof URLSpan) {
                changed = true;
                builder.removeSpan(span);
            }
        }

        if (!changed) {
            return null;
        }

        return builder;
    }
}
