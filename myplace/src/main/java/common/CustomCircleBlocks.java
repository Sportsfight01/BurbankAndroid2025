package common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dmss.burbankappold.R;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class CustomCircleBlocks extends LinearLayout {
    private ImageView headingFieldIcon;
    public TextView customHeadingTextView;
    private LinearLayout lineaLyBlock;
    public boolean consumeTouch;

    int selectedTextColor, unSelectedTextColor;
    int selectedIcon, unSelectedIcon;
    String selectionText, unSelectionText;

    public CustomCircleBlocks(Context context) {
        this(context, null);
    }

    public CustomCircleBlocks(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.LEFT);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_circle_blocks, this, true);

        headingFieldIcon = (ImageView) findViewById(R.id.headingFieldIcon);
        lineaLyBlock = (LinearLayout) findViewById(R.id.lineaLyBlock);
        lineaLyBlock.setBackgroundResource(R.drawable.circle);
        customHeadingTextView = (TextView) findViewById(R.id.customHeadingTextView);
        this.unSelectedTextColor = getResources().getColor(R.color.black);
        this.selectedTextColor = getResources().getColor(R.color.fontColorOrange);
    }

    public void setMaxLines(int lines) {
        customHeadingTextView.setMaxLines(lines);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (consumeTouch) {
            return true;
        } else {
            return false;
        }
    }

    public void setText(CharSequence text) {
        customHeadingTextView.setText(text);
    }

    public void setText(String text) {
        customHeadingTextView.setText(text);
        this.unSelectionText = null;
    }

    public String getText() {
        return customHeadingTextView.getText().toString().trim();
    }

    public void setImageResource(int unSelectedIcon, int selectedIcon) {
        this.unSelectedIcon = unSelectedIcon;
        this.selectedIcon = selectedIcon;
        headingFieldIcon.setImageResource(unSelectedIcon);
    }

    public void setText(String text, String unselectText) {
        this.selectionText = text;
        this.unSelectionText = unselectText;
    }


    public void setDynamicSelection(boolean selection) {
        /*if(){
            this.unSelectedTextColor = getResources().getColor(R.color.black);
            this.selectedTextColor = getResources().getColor(R.color.black);
        }else{
            this.unSelectedTextColor = getResources().getColor(R.color.black);
            this.selectedTextColor = getResources().getColor(R.color.fontColorOrange);
        }*/
        if (selection) {
            headingFieldIcon.setImageResource(selectedIcon);
            if (selectionText != null && unSelectionText != null) {
                customHeadingTextView.setTextColor(unSelectedTextColor);
                customHeadingTextView.setText(selectionText);
            } else {
                customHeadingTextView.setTextColor(selectedTextColor);
            }

        } else {
            headingFieldIcon.setImageResource(unSelectedIcon);
            if (selectionText != null && unSelectionText != null) {
                customHeadingTextView.setText(unSelectionText);
            }
            customHeadingTextView.setTextColor(unSelectedTextColor);
        }

    }

    public void setFont(Typeface tf) {
        customHeadingTextView.setTypeface(tf);
    }

    public void setTextSize(int size) {
        customHeadingTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setBlackBackground() {
        lineaLyBlock.setBackgroundResource(R.color.black);
    }

    public void setTextColor(int color) {
        customHeadingTextView.setTextColor(getResources().getColor(R.color.white));
    }

    public void setImageResource(int unSelectedIcon) {
        headingFieldIcon.setImageResource(unSelectedIcon);
    }

}