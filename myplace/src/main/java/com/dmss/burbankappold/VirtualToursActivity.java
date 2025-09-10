package com.dmss.burbankappold;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class VirtualToursActivity extends AppCompatActivity {
    WebView toursWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //line for removing top title bar from screen////////////
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        ///////line for opening screen in full mode///////////////
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//AVBPR6623C
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_virtual_tours);
        toursWebView = (WebView) findViewById(R.id.toursWebView);
        toursWebView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(VirtualToursActivity.this, description, Toast.LENGTH_SHORT).show();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("matterport.com/vr/show")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                return false;
            }
        });

        toursWebView.getSettings().setJavaScriptEnabled(true);
        toursWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        toursWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        toursWebView.loadUrl("https://my.matterport.com/show/?m=4uUwjj9Q5YP&utm_source=4");


    }
}
