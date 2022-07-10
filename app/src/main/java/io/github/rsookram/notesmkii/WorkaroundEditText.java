package io.github.rsookram.notesmkii;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * A customized {@link EditText} which works around a bug in scrolling the
 * cursor into position.
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
public class WorkaroundEditText extends EditText {

   private boolean addBottomPadding;

   public WorkaroundEditText(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public boolean bringPointIntoView(int offset) {
      int line = getLayout().getLineForOffset(offset);

      // EditText handles adding padding for the last line, so only add for
      // previous lines to avoid double padding.
      // See TextView#getInterestingRect.
      addBottomPadding = line < getLayout().getLineCount() - 1;

      return super.bringPointIntoView(offset);
   }

   @Override
   public boolean requestRectangleOnScreen(Rect rectangle, boolean immediate) {
      if (addBottomPadding) {
         rectangle.bottom += getPaddingBottom();
         addBottomPadding = false;
      }

      return super.requestRectangleOnScreen(rectangle, immediate);
   }
}
