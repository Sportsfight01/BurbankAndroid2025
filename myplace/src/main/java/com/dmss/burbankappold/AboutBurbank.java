package com.dmss.burbankappold;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Ashish.Kumar on 31-08-2017.
 */

public class AboutBurbank extends AppCompatActivity {

    WebView aboutBurbankWV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_burbank);
        aboutBurbankWV = (WebView) findViewById(R.id.aboutBurbankWV);
        aboutBurbankWV.loadUrl("file:///android_asset/about_burbank.html");
        //make sure you cast the web view before using loadUrl() function :
        actionBarSettings();
    }


    public void actionBarSettings() {
        ImageView backImage = findViewById(R.id.back_image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        TextView first = findViewById(R.id.firstText);
        first.setText("About Burbank");
        TextView second = findViewById(R.id.secondText);
        second.setVisibility(View.GONE);

        /*ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_actionbar_transparent, null);
        view.setBackgroundResource(R.color.black);
        final ImageView backImageView = (ImageView) view.findViewById(R.id.backImageView);
        backImageView.setVisibility(View.VISIBLE);
        final ImageView burbankLogoImageView = (ImageView) view.findViewById(R.id.burbankLogoImageView);
        burbankLogoImageView.setVisibility(View.GONE);
        TextView actionBarHeadingTextView = (TextView) view.findViewById(R.id.actionBarHeadingTextView);
        actionBarHeadingTextView.setVisibility(View.VISIBLE);
        actionBarHeadingTextView.setText("About Burbank");
        TextView saveSettings = (TextView) view.findViewById(R.id.saveSettings);
        saveSettings.setVisibility(View.GONE);
        actionBar.setCustomView(view, new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
    }
}
