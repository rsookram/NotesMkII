package io.github.rsookram.notesmkii

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.EditText

/**
 * A customized [EditText] which works around a bug in scrolling the cursor
 * into position.
 *
 * When the IME wasn't shown, tapping on text would bring it up. If the text
 * that was tapped on was not on the last line, the parent ScrollView wouldn't
 * scroll to bring the selection into view. This was problematic when the IME
 * covered the selection, since it would force the user to manually scroll to
 * bring it into view.
 *
 * This was happening because EditText doesn't account for bottom padding when
 * specifying where the selection is, unless it's part of the last line.
 * Normally this isn't an issue since bottom padding tends to be similar in
 * height to a line of text. In this app however, bottom padding is added to
 * the EditText to adjust for system insets. So the padding can be as large as
 * the IME.
 */
class WorkaroundEditText(context: Context, attrs: AttributeSet) : EditText(context, attrs) {

    private var addBottomPadding = false

    override fun bringPointIntoView(offset: Int): Boolean {
        val line = layout.getLineForOffset(offset)

        // EditText handles adding padding for the last line, so only add for
        // previous lines to avoid double padding.
        // See TextView#getInterestingRect.
        addBottomPadding = line < layout.lineCount - 1

        return super.bringPointIntoView(offset)
    }

    override fun requestRectangleOnScreen(rectangle: Rect, immediate: Boolean): Boolean {
        if (addBottomPadding) {
            rectangle.bottom += paddingBottom
            addBottomPadding = false
        }

        return super.requestRectangleOnScreen(rectangle, immediate)
    }
}
