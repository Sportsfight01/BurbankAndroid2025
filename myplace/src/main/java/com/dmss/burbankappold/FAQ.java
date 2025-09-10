package com.dmss.burbankappold;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapters.ExpendableListAdapter;
import common.AppController;
import common.Common;
import common.JustifiedTextView;
import common.MyPlaceSharedPreferences;
import common.TransparentProgressDialog;
import common.Utils;
import interfaces.ExpendableListCallBack;
import models.FAQModels;

public class FAQ extends AppCompatActivity implements View.OnClickListener, ExpendableListCallBack {
    ExpendableListAdapter adapter;
    ExpandableListView expandableListView;
    List<String> listDataHeader;
    HashMap<String, String> listDataChild;
    ArrayList<FAQModels> faqModelsArrayList = new ArrayList<FAQModels>();
    int position = 0;
    int previousGroup = -1;
    AppController controller;
    TransparentProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        controller = (AppController) getApplicationContext();
        actionBarSettings();
        initializeUIElements();

        dialog = Utils.getProgress(FAQ.this);
        String region = controller.getRegion();
        String myPlaceRegion = controller.getMy_Place_Details().getRegion();
        if(!controller.getRegion().equalsIgnoreCase(controller.getMy_Place_Details().getRegion())){
            getDataFromSharedPreferences();
        }else{
            if (controller.getFaqModelsArrayList().size() != 0){
                setDataToUi();
            }else{
                getDataFromSharedPreferences();
            }
        }
    }

    public void getDataFromSharedPreferences(){
        String faqData = MyPlaceSharedPreferences.getFaq(FAQ.this);
        if(faqData != null && faqData.length()>0){
            prepareListData(faqData);
        }else{
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String faqString = controller.webApiCall().getFAQData_From_MyPlace(Common.faq_Url);
                    if(faqString != null && faqString.length() > 0){
                        MyPlaceSharedPreferences.saveFaq(FAQ.this,faqString);
                        prepareListData(faqString);
                    }else{
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog != null) {
                                    dialog.cancel();
                                }
                            }
                        });
                    }
                }
            });
            t.start();
        }
    }
    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public void initializeUIElements() {
        Display newDisplay = getWindowManager().getDefaultDisplay();
        int width = newDisplay.getWidth();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthNew = metrics.widthPixels;

        expandableListView = (ExpandableListView) findViewById(R.id.lvExp);
        //expandableListView.setIndicatorBounds(width-80, width);

        expandableListView.setIndicatorBounds(widthNew - GetPixelFromDips(50), widthNew - GetPixelFromDips(10));



       /* expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return false;
            }
        });
*/
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    expandableListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableListView.setIndicatorBounds(expandableListView.getRight() - 80, expandableListView.getWidth());
        } else {
            expandableListView.setIndicatorBoundsRelative(expandableListView.getRight() - 80, expandableListView.getWidth());
        }
    }*/

    private void prepareListData(String result) {

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, String>();
        try {
            JSONArray faqServerJson = new JSONArray(result);
            for (int i = 0; i < faqServerJson.length(); i++) {
                JSONObject jsonObject = faqServerJson.getJSONObject(i);
                FAQModels faqModels =new FAQModels(jsonObject.toString());
                faqModelsArrayList.add(faqModels);
            }
            controller.setFaqModelsArrayList(faqModelsArrayList);
            setDataToUi();

        } catch (Exception ex) {
            if (dialog != null) {
                dialog.cancel();
            }
            MyPlaceSharedPreferences.saveFaq(FAQ.this,null);
            ex.fillInStackTrace();
        }
    }

    public void setDataToUi(){

        ArrayList<FAQModels> regionFaqModelsArrayList = new ArrayList<FAQModels>();
        regionFaqModelsArrayList.addAll(controller.getFaqModelsArrayList());
        for (int i = 0; i < regionFaqModelsArrayList.size(); i++)
        {
            if(controller.getMy_Place_Details().getRegion().equalsIgnoreCase(regionFaqModelsArrayList.get(i).getRegionId())){
                listDataHeader.add(regionFaqModelsArrayList.get(i).getQuestion());
                listDataChild.put(regionFaqModelsArrayList.get(i).getQuestion(), regionFaqModelsArrayList.get(i).getAnswer());
            }
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                expandableListView.setAdapter(new ExpendableListAdapter(FAQ.this, listDataHeader, listDataChild));
                if (dialog != null) {
                    dialog.cancel();
                }
            }
        });
    }

    public void actionBarSettings() {
        TextView firstText = findViewById(R.id.firstText);
        firstText.setText("FAQ's - "+controller.getMy_Place_Details().getRegion());
        TextView secondText = findViewById(R.id.secondText);
        secondText.setVisibility(View.GONE);

        ImageView backImage = findViewById(R.id.back_image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
       /* ActionBar actionBar;
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
        actionBarHeadingTextView.setText("FAQ's - "+controller.getMy_Place_Details().getRegion());
        TextView saveSettings = (TextView) view.findViewById(R.id.saveSettings);
        saveSettings.setVisibility(View.GONE);
        actionBar.setCustomView(view, new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM |  ActionBar.DISPLAY_SHOW_HOME);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
    }

    public void onClick(int position) {
        this.position = position;
        showAlertDialog();
    }

    public void showAlertDialog() {
        final Dialog dialog = new Dialog(FAQ.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.faq_alert);
        final TextView next = (TextView) dialog.findViewById(R.id.next);
        final TextView previous = (TextView) dialog.findViewById(R.id.previous);
        Button close = (Button) dialog.findViewById(R.id.close);
        final TextView questionNumber = (TextView) dialog.findViewById(R.id.questionNo);
        final TextView header = (TextView) dialog.findViewById(R.id.header);
        final JustifiedTextView content = (JustifiedTextView) dialog.findViewById(R.id.content);
        int question = position + 1;
        if(position == 0){
            buttonHandling(0,next,previous);
        }else if(position == listDataHeader.size()-1){
            buttonHandling(1,next,previous);
        }else{
            buttonHandling(2,next,previous);
        }
        questionNumber.setText("Q " + question + " :");
        header.setText(listDataHeader.get(position).toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            header.setText(Html.fromHtml(listDataHeader.get(position).trim(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            header.setText(Html.fromHtml(listDataHeader.get(position).trim()));
        }


        String contentData = listDataChild.get(listDataHeader.get(position).toString());

        setHtmlContent(contentData,content);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < listDataHeader.size() - 1) {
                    position = position + 1;
                    int question = position + 1;
                    questionNumber.setText("Q" + question + " :");
                    if(position == 0){
                        buttonHandling(0,next,previous);
                    }else if(position == listDataHeader.size()-1){
                        buttonHandling(1,next,previous);
                    }else{
                        buttonHandling(2,next,previous);
                    }

                    setHtmlContent(listDataChild.get(listDataHeader.get(position).toString()),content);
                    header.setText(listDataHeader.get(position).toString());

                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {
                    position = position - 1;
                    int question = position + 1;
                    questionNumber.setText("Q" + question + " :");
                    header.setText(listDataHeader.get(position).toString());
                    if(position == 0){
                        buttonHandling(0,next,previous);
                    }else if(position == listDataHeader.size()-1){
                        buttonHandling(1,next,previous);
                    }else{
                        buttonHandling(2,next,previous);
                    }
                    setHtmlContent(listDataChild.get(listDataHeader.get(position).toString()),content);

                }
            }
        });
        dialog.show();
    }

    public void buttonHandling(int displayPosition,TextView next,TextView previous){
        if(displayPosition == 0){
            previous.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
        }else if(displayPosition == 1){
            previous.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
        }else{
            previous.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
        }
    }

    public void setHtmlContent(String text,TextView textView){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(text));
        }
    }

}
