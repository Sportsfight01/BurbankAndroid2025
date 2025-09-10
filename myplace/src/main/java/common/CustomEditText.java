package common;

import android.content.Context;
import android.graphics.Typeface;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dmss.burbankappold.R;

/**
 * Created by sandeep.kumar on 12-12-2016.
 */
public class CustomEditText extends LinearLayout {

    public EditText customEditText;
    public ImageView icon;
    public View disableView;
    RelativeLayout layout;
    public CustomEditText(Context context) {
        this(context, null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.LEFT);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_edittext, this, true);
        layout=(RelativeLayout) findViewById(R.id.mainLayout);
        customEditText = (EditText) findViewById(R.id.customEditText);
        disableView = (View) findViewById(R.id.disableView);
        icon = (ImageView) findViewById(R.id.icon);
        customEditText.setHint(R.string.email_Id);
        customEditText.setCursorVisible(true);
        customEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    public void setLayoutBackground(int val) {
        layout.setBackgroundResource(val);
    }
    public void setImage(int id) {
        if(id==0)
        {
            icon.setVisibility(GONE);
            icon.setImageDrawable(null);
        }else {
            icon.setVisibility(VISIBLE);
            icon.setImageResource(id);
        }
    }

    public String getText() {
        if (customEditText.getText().length() > 0) {
            return customEditText.getText().toString();
        } else {
            return "";
        }
    }

    public void setText(String text) {
        customEditText.setText(text);
    }
    public void setSelection(int length) {
        customEditText.setSelection(length);
    }

    public void setEnabledd(boolean val) {
        if (!val) {
            disableView.setVisibility(View.VISIBLE);
            disableView.bringToFront();
        } else {
            disableView.setVisibility(View.GONE);
        }
        customEditText.setEnabled(val);
        customEditText.setFocusable(val);
    }

    public void setInputType(int type) {
        customEditText.setInputType(type);
    }

    public void setTransformationMethod(PasswordTransformationMethod type) {
        customEditText.setTransformationMethod(type);
    }

    public String getCustomEditText() {
        return customEditText.getText().toString().trim();
    }

    public void setCustomEditText(EditText customEditText) {
        this.customEditText = customEditText;
    }

    /***
     * Setting the font to the custom textView
     */
    public void setFont(Typeface tf) {
        customEditText.setTypeface(tf);
    }

    /***
     * Highlighting the textView when pressed or selected.
     */
    public void setEditTextSelected() {
        //customEditText.setBackgroundColor(getResources().getColor(R.color.appOrange));
        // customEditText.setTextColor(getResources().getColor(R.color.white));
    }

    /***
     * setting the textView to normal state.
     */
    public void setEditTextUnSelected() {
        //  customEditText.setBackgroundColor(getResources().getColor(R.color.white));
        // customEditText.setTextColor(getResources().getColor(R.color.black));
    }

    public void setHint(String hint) {
        customEditText.setHint(hint);
    }


    public void hideImage(){
        icon.setVisibility(GONE);
    }
}
