package com.dmss.burbankapp.utils.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


import com.dmss.burbankapp.R;

public class BigCustomTextView extends androidx.appcompat.widget.AppCompatTextView {

    public BigCustomTextView(Context context) {
        this(context, null);
        init(context);
    }

    public BigCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setCustomTypeFace(context);
        setTextSize(getResources().getDimension(R.dimen.bigtextSize));
    }

    private void setCustomTypeFace(Context context) {
        Typeface typeFaces = Typeface.createFromAsset(context.getAssets(), "fonts/proxima_nova_extrabold.otf");
        this.setTypeface(typeFaces);
    }
}