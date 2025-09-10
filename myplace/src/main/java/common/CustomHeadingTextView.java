package common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dmss.burbankappold.R;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class CustomHeadingTextView extends LinearLayout {
    public TextView headingHeadTextView, headingTailTextVew;
    public boolean consumeTouch;

    public CustomHeadingTextView(Context context) {
        this(context, null);
    }

    public CustomHeadingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.LEFT);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_heading_textview, this, true);

        headingHeadTextView = (TextView) findViewById(R.id.headingHeadTextView);
        headingTailTextVew = (TextView) findViewById(R.id.headingTailTextVew);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (consumeTouch) {
            return true;
        } else {
            return false;
        }
    }

    public void setText(CharSequence tail) {
        headingTailTextVew.setText(tail);
    }

    public void setHeadText(CharSequence head) {
        headingHeadTextView.setText(head);
    }

    public void setText(String tail) {
        headingTailTextVew.setText(tail);
    }

    public void setHeadText(String head) {
        headingHeadTextView.setText(head);
    }

    public void setFont(Typeface tf) {
        headingHeadTextView.setTypeface(tf);
    }
}