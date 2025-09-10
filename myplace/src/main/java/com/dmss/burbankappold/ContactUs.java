package com.dmss.burbankappold;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.ContactUsAdapter;
import common.AppController;
import common.Common;
import common.CustomCircleBlocks;
import common.TransparentProgressDialog;
import common.Utils;
import models.ContactUsModel;

public class ContactUs extends AppCompatActivity implements View.OnClickListener {
    CustomCircleBlocks callCircleBlock, emailCircleBlock;
    ArrayList<ContactUsModel> contactUsNotesArrayList = new ArrayList<ContactUsModel>();
    TransparentProgressDialog dialog;
    AppController controller;
    ContactUsAdapter contactUsAdapter;

    ListView contactUsUpdatesListView;
    boolean apiCallStarted = false;
    Dialog enquireDialog;
    LinearLayout emptyElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeOld);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        controller = (AppController) getApplicationContext();
        controller.getAnalytics().dashboardHistoryLogIconTouchEvent();
        actionBarSettings();
        initializeUIElements();
        callWebApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!apiCallStarted) {
            initializeUIElements();
            callWebApi();
        }

    }

    public void initializeUIElements() {
        ImageView backImage = findViewById(R.id.back_image);
        backImage.setVisibility(View.GONE);
        backImage.setOnClickListener(view -> onBackPressed());
        TextView firstText = findViewById(R.id.firstText);
        firstText.setText("History");
        TextView secondText = findViewById(R.id.secondText);
        secondText.setText("Log");

        emptyElement = (LinearLayout) findViewById(R.id.emptyElement);
        callCircleBlock = (CustomCircleBlocks) findViewById(R.id.callCircleBlock);
        callCircleBlock.setText("Call");
        callCircleBlock.setImageResource(R.drawable.phone_orange);
        emailCircleBlock = (CustomCircleBlocks) findViewById(R.id.emailCircleBlock);
        emailCircleBlock.setText("Email");
        emailCircleBlock.setImageResource(R.drawable.email_orange);
        contactUsUpdatesListView = (ListView) findViewById(R.id.contactUsUpdatesListView);
    }

    /**
     * Method used to call the web API to get the data from server
     **/
    private void callWebApi() {
        /**
         * Checking whether the network is available or not
         */
        if (Utils.isNetworkAvailable(ContactUs.this)) {
            apiCallStarted = true;
            dialog = Utils.getProgress(ContactUs.this);
            if (controller.getMy_Place_Details() != null) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
                        byte[] message = sample.getBytes();
                        String encoded = Base64.encodeToString(message, Base64.NO_WRAP);
                        String contactUsResult = controller.webApiCall().getDataWithHeaders(Common.newMyPlaceContactUs, encoded, controller.getMy_Place_Details().getJobNumber());
                        if (contactUsResult.length() > 2) {
                            onSuccessResult(contactUsResult, false);
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    apiCallStarted = false;
                                    if (dialog != null) {
                                        dialog.cancel();
                                    }
                                    emptyElement.setVisibility(View.VISIBLE);
                                    contactUsUpdatesListView.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }).start();








                /*if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("VIC")) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String urll = Common.myPlaceBaseUrlVic;

                            String result = controller.webApiCall().postData_to_MyPlace(urll + "" + Common.MyPlaceUserCheckUrl, getMyPlaceLoginJson());
                            if (result.equalsIgnoreCase("true")) {
                                String loginResult = controller.webApiCall().getData_From_MyPlace(urll + "" + Common.MyPlaceGetUserDetailsUrl);
                                try {
                                    JSONObject jsonObject = new JSONObject(loginResult);
                                    String constructionID = jsonObject.isNull("ConstructionID") ? "" : jsonObject.getString("ConstructionID");
                                    if (constructionID.length() > 0) {
                                        String contactUsResult = controller.webApiCall().getData_From_MyPlace("https://www.burbank.com.au/victoria/myplace/api/contact/GetContact?constructionTicketID=" + constructionID + "&region=VIC");
                                        if (contactUsResult.length() > 2) {
                                            onSuccessResult(contactUsResult, true);
                                        } else {
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    apiCallStarted = false;

                                                    emptyElement.setVisibility(View.VISIBLE);
                                                    contactUsUpdatesListView.setVisibility(View.GONE);
                                                    if (dialog != null) {
                                                        dialog.cancel();
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                apiCallStarted = false;
                                                if (dialog != null) {
                                                    dialog.cancel();
                                                }
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            apiCallStarted = false;
                                            if (dialog != null) {
                                                dialog.cancel();
                                            }
                                        }
                                    });

                                }
                            } else if (result.equalsIgnoreCase("false")) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        apiCallStarted = false;
                                        dialog.cancel();
                                        Utils.showToast(ContactUs.this, "My place details not valid for this job number", Common.errorCase);
                                        //controller.setMy_Place_Details(null);
                                    }
                                });
                            } else {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        apiCallStarted = false;
                                        dialog.cancel();
                                    }
                                });
                            }
                        }
                    });
                    t.start();
                } else if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("QLD") || controller.getMy_Place_Details().getRegion().equalsIgnoreCase("SA")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
                            byte[] message = sample.getBytes();
                            String encoded = Base64.encodeToString(message, Base64.NO_WRAP);
                            String contactUsResult = controller.webApiCall().getDataWithHeaders(Common.newMyPlaceContactUs, encoded, controller.getMy_Place_Details().getJobNumber());
                            if (contactUsResult.length() > 2) {
                                onSuccessResult(contactUsResult, false);
                            } else {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        apiCallStarted = false;
                                        if (dialog != null) {
                                            dialog.cancel();
                                        }
                                        emptyElement.setVisibility(View.VISIBLE);
                                        contactUsUpdatesListView.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    }).start();
                }*/
            } else {
                dialog.cancel();
            }

        } else {
            dialog.cancel();
        }
    }

    public String getMyPlaceLoginJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Region", controller.getMy_Place_Details().getRegion());
            jsonObject.put("JobNumber", controller.getMy_Place_Details().getJobNumber());
            jsonObject.put("UserName", controller.getMy_Place_Details().getUsername());
            jsonObject.put("Password", controller.getMy_Place_Details().getPassword());
        } catch (JSONException ex) {
            ex.fillInStackTrace();
        }
        return jsonObject.toString();
    }

    public void onSuccessResult(String result, boolean vic) {
        contactUsNotesArrayList.clear();
        contactUsNotesArrayList = new ArrayList<ContactUsModel>();
        if (result != null || result == "" || result.length() < 3) {

            apiCallStarted = false;
        }
        try {
            JSONArray jobJsonArray = new JSONArray(result);
            if (vic) {
                for (int i = 0; i < jobJsonArray.length(); i++) {
                    ContactUsModel contactUsModel = new ContactUsModel(jobJsonArray.get(i).toString(), true);
                    contactUsNotesArrayList.add(contactUsModel);
                }
            } else {
                for (int i = 0; i < jobJsonArray.length(); i++) {
                    ContactUsModel contactUsModel = new ContactUsModel(jobJsonArray.get(i).toString(), false);
                    contactUsNotesArrayList.add(contactUsModel);
                }
            }
        } catch (Exception ex) {
            ex.fillInStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null) {
                        dialog.cancel();
                    }
                }
            });

        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (contactUsNotesArrayList.size() > 0) {
                    contactUsAdapter = new ContactUsAdapter(ContactUs.this, contactUsNotesArrayList);
                    contactUsUpdatesListView.setAdapter(contactUsAdapter);
                    emptyElement.setVisibility(View.GONE);
                    contactUsUpdatesListView.setVisibility(View.VISIBLE);
                } else {
                    emptyElement.setVisibility(View.VISIBLE);
                    contactUsUpdatesListView.setVisibility(View.GONE);
                }

                dialog.cancel();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.callCircleBlock) {
            showCallDialog(false);
            //Utils.showToast(ContactUs.this, "Work in progress", Common.sucessCase);
        } else if (id == R.id.emailCircleBlock) {
            showCallDialog(true);
            //Utils.showToast(ContactUs.this, "Work in progress", Common.sucessCase);
        }
    }

    public void showCallDialog(boolean email) {
        enquireDialog = new Dialog(ContactUs.this);
        enquireDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        enquireDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        enquireDialog.setContentView(R.layout.alert_dialog_ios);
        TextView dataTextView = (TextView) enquireDialog.findViewById(R.id.dataTextView);
        dataTextView.setVisibility(View.VISIBLE);
        if (email) {
            dataTextView.setText("Need more information to implement E-mail Functionality. Please provide more details. Before sending email, customer will save comments (Subject and Body ). But to whom the customer will email. Which service to get the Email-id of responsible office.");
        }
        TextView okTextView = (TextView) enquireDialog.findViewById(R.id.okTextView);
        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enquireDialog.cancel();
            }
        });
        enquireDialog.show();
    }

    public void actionBarSettings() {
       /* ActionBar actionBar;
        actionBar = getSupportActionBar();
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
        actionBarHeadingTextView.setText("History Log");
        TextView saveSettings = (TextView) view.findViewById(R.id.saveSettings);
        saveSettings.setVisibility(View.GONE);
        actionBar.setCustomView(view, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*final Dialog exitDialog = new Dialog(ContactUs.this);
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
        exitDialog.show();*/
    }
}
