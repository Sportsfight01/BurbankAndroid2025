package com.dmss.burbankappold;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import common.AppController;

public class FeedBackActivity extends AppCompatActivity implements View.OnClickListener {
    /*TextView *//*provideCircleBlock,*//* completeCircleBlock;*/
    AppController controller;
    Dialog enquireDialog;
    LinearLayout feedBackCircleBlocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        actionBarSettings();
        initializeUIElements();

    }

    public void initializeUIElements() {
        /*provideCircleBlock = (TextView) findViewById(R.id.provideCircleBlock);
        provideCircleBlock.setOnClickListener(this);*/
        controller = (AppController) getApplicationContext();
        feedBackCircleBlocks = (LinearLayout) findViewById(R.id.feedBackCircleBlocks);
        /*completeCircleBlock = (TextView) findViewById(R.id.completeCircleBlock);*/
        feedBackCircleBlocks.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        /*case R.id.provideCircleBlock:
                Utils.showToast(FeedBackActivity.this, "Under Development", Common.sucessCase);
                break;*/
        if (view.getId() == R.id.feedBackCircleBlocks) {
            showConfirmationDialog();
            //Utils.showToast(FeedBackActivity.this, "Under Development", Common.sucessCase);
        }
    }

    public void actionBarSettings() {
         ActionBar actionBar;
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_actionbar_transparent, null);
        view.setBackgroundResource(R.color.black);
        final ImageView backImageView = (ImageView) view.findViewById(R.id.backImageView);
        backImageView.setVisibility(View.GONE);
        final ImageView burbankLogoImageView = (ImageView) view.findViewById(R.id.burbankLogoImageView);
        burbankLogoImageView.setVisibility(View.GONE);
        TextView actionBarHeadingTextView = (TextView) view.findViewById(R.id.actionBarHeadingTextView);
        actionBarHeadingTextView.setVisibility(View.VISIBLE);
        actionBarHeadingTextView.setText("Help Us Improve");
        TextView saveSettings = (TextView) view.findViewById(R.id.saveSettings);
        saveSettings.setVisibility(View.GONE);
        actionBar.setCustomView(view, new  ActionBar.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM |  ActionBar.DISPLAY_SHOW_HOME);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        final Dialog exitDialog = new Dialog(FeedBackActivity.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitDialog.setContentView(R.layout.custom_dialog);
        final Button dialogOkButton = (Button) exitDialog.findViewById(R.id.dialogOkButton);
        final Button dialogCancelButton = (Button) exitDialog.findViewById(R.id.dialogCancelButton);
        final TextView dialogMessageTextView = (TextView) exitDialog.findViewById(R.id.dialogMessageTextView);
        dialogMessageTextView.setText("Do you want exit from the application");
        LinearLayout cancelButtonLayout = (LinearLayout) exitDialog.findViewById(R.id.cancelButtonLayout);
        cancelButtonLayout.setVisibility(View.VISIBLE);
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
                finish();
            }
        });

        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });
        exitDialog.show();
    }

    public void showConfirmationDialog() {
        enquireDialog = new Dialog(FeedBackActivity.this);
        enquireDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        enquireDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        enquireDialog.setContentView(R.layout.feedback_survey);
        final TextView numberTextView = (TextView) enquireDialog.findViewById(R.id.numberTextView);
        numberTextView.setVisibility(View.VISIBLE);
        numberTextView.setText("To Complete survey, you have to login to myplace and you will be redirected to myplace web portal. Proceed?");
        TextView headingTextView = (TextView) enquireDialog.findViewById(R.id.headingTextView);
        headingTextView.setText("FeedBack");
        Button cancelButton = (Button) enquireDialog.findViewById(R.id.cancelButton);
        cancelButton.setTypeface(controller.getTypeface());
        Button sendButton = (Button) enquireDialog.findViewById(R.id.sendButton);
        sendButton.setTypeface(controller.getTypeface());
        sendButton.setText("Proceed");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enquireDialog.cancel();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "";
                if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("VIC")) {
                    url = "https://www.burbank.com.au/victoria/myplace/";
                } else if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("QLD")) {
                    url = "https://www.burbank.com.au/myplace/?region=qld";
                } else if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("SA")) {
                    url = "https://www.burbank.com.au/myplace/?region=sa";
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });
        enquireDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (enquireDialog != null) {
            enquireDialog.cancel();
        }
    }
}
