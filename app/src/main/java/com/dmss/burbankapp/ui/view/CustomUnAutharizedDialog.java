package com.dmss.burbankapp.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dmss.burbankapp.R;
import com.dmss.burbankapp.ui.login.ChooseOptionsActivity;

public class CustomUnAutharizedDialog extends Dialog {

    public CustomUnAutharizedDialog(@NonNull Context context) {
        super(context);
    }

    public CustomUnAutharizedDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomUnAutharizedDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.layout_unautharized);
        this.setCanceledOnTouchOutside(false);
        TextView okText = findViewById(R.id.ok);
        okText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                /*Intent i = new Intent(getContext(), ChooseOptionsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(i);*/
            }
        });
    }
}
