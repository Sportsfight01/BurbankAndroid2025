package common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dmss.burbankappold.R;

/**
 * Created by Jaya.Krishna on 08-01-2018.
 */

public class CustomTripleTextView extends LinearLayout {
    public TextView contactHeadingHeadTextView, contactNameTextVew, contactDetailTextVew;
    public boolean consumeTouch;
    public ImageView callIcon, mailIcon;

    public CustomTripleTextView(Context context) {
        this(context, null);
    }

    public CustomTripleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.LEFT);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_triple_textview, this, true);

        contactHeadingHeadTextView = (TextView) findViewById(R.id.contactHeadingHeadTextView);
        contactNameTextVew = (TextView) findViewById(R.id.contactNameTextVew);
        contactDetailTextVew = (TextView) findViewById(R.id.contactDetailTextVew);
        callIcon = (ImageView) findViewById(R.id.callIcon);
        mailIcon = (ImageView) findViewById(R.id.mailIcon);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (consumeTouch) {
            return true;
        } else {
            return false;
        }
    }

    public void setText(CharSequence tail) {
        contactNameTextVew.setText(tail);
    }

    public void setHeadText(CharSequence head) {
        contactHeadingHeadTextView.setText(head);
    }

    public void setDetailText(CharSequence tail) {
        contactDetailTextVew.setText(tail);
    }

    public void setText(String tail) {
        contactNameTextVew.setText(tail);
    }

    public void setHeadText(String head) {
        contactHeadingHeadTextView.setText(head);
    }

    public void setDetailText(String tail) {
        contactDetailTextVew.setText(tail);
    }

    public void setFont(Typeface tf) {
        contactHeadingHeadTextView.setTypeface(tf);
        contactNameTextVew.setTypeface(tf);
        contactDetailTextVew.setTypeface(tf);
    }

    public void hideDetails() {
        contactDetailTextVew.setVisibility(GONE);
    }

    public void callIconVisible(boolean visible) {
        if (visible) {
            callIcon.setVisibility(VISIBLE);
        } else {
            callIcon.setVisibility(GONE);
        }
    }

    public void emailIconVisible(boolean visible) {
        if (visible) {
            mailIcon.setVisibility(VISIBLE);
        } else {
            mailIcon.setVisibility(GONE);
        }
    }

    public void sendEmail(final String mail, final Activity act) {
        mailIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(act, "My place details not valid for this job number", Common.errorCase);
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", mail, null));
                act.startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
    }
}