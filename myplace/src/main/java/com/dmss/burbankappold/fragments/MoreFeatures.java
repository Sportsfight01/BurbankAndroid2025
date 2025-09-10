package com.dmss.burbankappold.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dmss.burbankappold.Co_Burbank;
import com.dmss.burbankappold.FAQ;
import com.dmss.burbankappold.R;
import com.dmss.burbankappold.VirtualToursActivity;

import common.AppController;
import interfaces.FragmentCallBack;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class MoreFeatures extends Fragment {

    FragmentCallBack fragmentCallBack;
    View rootView;
    LinearLayout documentLayout, coBurbank, favPicsLayout,webView;
    LinearLayout faqLayout;
    AppController controller;
    TextView firstText;
    TextView secondText;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.gc();
        controller = (AppController) getActivity().getApplicationContext();
        controller.getAnalytics().setScreen(getActivity(),"More_Screen");
        rootView = inflater.inflate(R.layout.morefeatures, null);
        initializeUIElements();
        return rootView;
    }

    public void initializeUIElements() {
        firstText = rootView.findViewById(R.id.firstText);
        firstText.setText("More");
        secondText = rootView.findViewById(R.id.secondText);
        secondText.setText(" Features");
        ImageView backImage = rootView.findViewById(R.id.back_image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        fragmentCallBack = (FragmentCallBack) getActivity();
        documentLayout = (LinearLayout) rootView.findViewById(R.id.documentLayout);
        coBurbank = (LinearLayout) rootView.findViewById(R.id.coBurbank);
        faqLayout = (LinearLayout) rootView.findViewById(R.id.faq);
        favPicsLayout = (LinearLayout) rootView.findViewById(R.id.favPicsLayout);
        webView = (LinearLayout) rootView.findViewById(R.id.webView);
        favPicsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.getAnalytics().dashboardFavPhotosButtonTouchEvent();
                fragmentCallBack.actionbarTextChange("My Favourites");
                Fragment fragmentDocuments = new FavouritePhotos();
                FragmentManager fragmentManagerDocuments = getFragmentManager();
                FragmentTransaction transactionDocuments = fragmentManagerDocuments.beginTransaction();
                transactionDocuments.replace(R.id.fragmentContainer, fragmentDocuments).addToBackStack(null).commit();
            }
        });
        faqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.getAnalytics().dashboardFAQButtonTouchEvent();
                Intent in = new Intent(getActivity(), FAQ.class);
                startActivity(in);
            }
        });
        coBurbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.getAnalytics().dashboardShareWithPartnersButtonTouchEvent();
                Intent in = new Intent(getActivity(), Co_Burbank.class);
                startActivity(in);
            }
        });
        documentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.getAnalytics().documentsIconTouchEvent();
                //fragmentCallBack.actionbarTextChange("My Documents");
                firstText.setText("Documents");
                secondText.setVisibility(View.GONE);
                Fragment fragmentDocuments = new Documents();
                FragmentManager fragmentManagerDocuments = getFragmentManager();
                FragmentTransaction transactionDocuments = fragmentManagerDocuments.beginTransaction();
                transactionDocuments.replace(R.id.fragmentContainer, fragmentDocuments).addToBackStack(null).commit();
            }
        });

        webView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*String url = "https://my.matterport.com/show/?m=4uUwjj9Q5YP&utm_source=4";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);*/
                Intent in = new Intent(getActivity(), VirtualToursActivity.class);
                startActivity(in);
            }
        });
    }

    /*public void actionBarSettings() {
        android.support.v7.app.ActionBar actionBar;
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
        actionBarHeadingTextView.setText("More Features");
        TextView saveSettings = (TextView) view.findViewById(R.id.saveSettings);
        saveSettings.setVisibility(View.GONE);
        actionBar.setCustomView(view, new android.support.v7.app.ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
        Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }*/

    /*@Override
    public void onClick(View view) {

    }*/
}
