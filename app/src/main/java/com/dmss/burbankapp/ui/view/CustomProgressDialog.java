package com.dmss.burbankapp.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dmss.burbankapp.R;

public class CustomProgressDialog {

    private Dialog mDialog;
    private TextView mTxtMessage;

    public CustomProgressDialog(Context context) {
        mDialog = new Dialog(context, R.style.style_new_dialog);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater mInflater = LayoutInflater.from(context);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View layout = mInflater.inflate(R.layout.custom_progress_dialog, null);
        //for progress bar color
        ProgressBar mProgress = (ProgressBar) layout.findViewById(R.id.progress);
        mProgress.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.colorAccent),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        mDialog.setContentView(layout);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);

    }

    public void showProgress() {
        if (mDialog != null) {
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }
    }

    public void dismissProgress() {
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    }
}
