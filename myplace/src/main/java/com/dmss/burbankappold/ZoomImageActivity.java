package com.dmss.burbankappold;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;

import common.AppController;
import okhttp3.OkHttpClient;

public class ZoomImageActivity extends AppCompatActivity {

    AppController controller;
    PhotoView photoView;
    ImageView zoomCloseImageView;
    ProgressDialog dialog2;
    String imageUrl;
    int urlInt;

    String root;
    DisplayMetrics metrics;
    OkHttpClient client;
    Picasso picasso;
    File currentPic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoomable_image_dailog);
        imageUrl = getIntent().getStringExtra("SelectedImage");
        urlInt = getIntent().getIntExtra("SelectedImageUrlInt", 0);
        root = Environment.getExternalStorageDirectory().toString();
        initializeUI();
    }

    public void initializeUI() {
        controller = (AppController) getApplicationContext();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        dialog2 = new ProgressDialog(ZoomImageActivity.this);
        dialog2.setMessage("Loading Image, please wait...");
        dialog2.setCanceledOnTouchOutside(false);


        photoView = (PhotoView) findViewById(R.id.zoomImageView);
        zoomCloseImageView = (ImageView) findViewById(R.id.zoomCloseImageView);
        photoView.bringToFront();
        zoomCloseImageView.bringToFront();
        zoomCloseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        loadImage();
    }

    private void loadImage() {
        dialog2.show();
        if (imageUrl == null) {
            if (dialog2.isShowing()) {
                dialog2.cancel();
            }
            return;
        }
        Glide.with(this)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                        if (dialog2.isShowing()) {
                            dialog2.cancel();
                        }

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (dialog2.isShowing()) {
                            dialog2.cancel();
                        }
                        return false;
                    }

                })
                .into(photoView); }

    public class CropSquareTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "square()";
        }
    }


}
