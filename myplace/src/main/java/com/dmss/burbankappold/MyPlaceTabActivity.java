package com.dmss.burbankappold;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import androidx.core.content.ContextCompat;


public class MyPlaceTabActivity extends TabActivity {


    TabHost tabHost;
    TextView title;
    ImageView icon;

    public void changeStatusBarColor() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(Color.BLACK);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_place_tab);
        tabHost = getTabHost();
        setTabs();
        tabHost.setCurrentTab(0);
        setFirstTabColor();

        getTabHost().setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                int position = tabHost.getCurrentTab();
                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    ImageView iv = (ImageView) getTabHost().getTabWidget().getChildAt(i).findViewById(R.id.icon);
                    if (position == i) {
                        iv.setColorFilter(ContextCompat.getColor(MyPlaceTabActivity.this, R.color.black),
                                android.graphics.PorterDuff.Mode.MULTIPLY);
                    } else {
                        iv.setColorFilter(ContextCompat.getColor(MyPlaceTabActivity.this, R.color.botton_icon_color),
                                android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                }

                hideKeyboard(MyPlaceTabActivity.this);
            }
        });

    }

    /**
     * Setting Tab Name And Icons
     ***/
    private void setTabs() {

        FeedBackActivity feedBackActivity = new FeedBackActivity();

        /*addTab("Feedback",
                R.drawable.message, FeedBackActivity.class);*/
        addTab("Dashboard",
                R.drawable.first_tab, DashBoard.class);
        addTab("Events",
                R.drawable.second_tab, MyAppointments.class);
        addTab("Messages",
                R.drawable.third_tab, ContactUs.class);
        addTab("Contacts",
                R.drawable.fourth_tab, ContactsActivity.class);

    }

    /***
     * Adding tab name and images for tab
     ***/
    private void addTab(String labelId, int drawableId, Class<?> c) {

        Intent intent = new Intent(this, c);
        TabHost.TabSpec spec = tabHost.newTabSpec(labelId);

        View tabIndicator = LayoutInflater.from(this).inflate(
                R.layout.tab_indicator, getTabWidget(), false);

        title = (TextView) tabIndicator.findViewById(R.id.title);
        title.setText(labelId);
        icon = (ImageView) tabIndicator.findViewById(R.id.icon);
        icon.setImageResource(drawableId);
        spec.setIndicator(tabIndicator);
        spec.setContent(intent);
        tabHost.newTabSpec(labelId);
        tabHost.addTab(spec);

    }

    @Override
    public void onBackPressed() {
        tabHost.setCurrentTab(0);
        setFirstTabColor();
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setFirstTabColor() {
        ImageView iv = (ImageView) getTabHost().getTabWidget().getChildAt(0).findViewById(R.id.icon);
        iv.setColorFilter(ContextCompat.getColor(MyPlaceTabActivity.this, R.color.black),
                android.graphics.PorterDuff.Mode.MULTIPLY);
    }


}
