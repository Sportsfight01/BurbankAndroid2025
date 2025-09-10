package com.dmss.burbankappold;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import common.AppController;
import common.Common;
import common.TransparentProgressDialog;
import common.Utils;
import models.ContactsModel;

public class ContactsActivity extends BaseActivity implements View.OnClickListener{
    ContactsModel contactsModelDetails;
    TransparentProgressDialog dialog;
    AppController controller;
    //String urll;
    boolean apiCallStarted = false;

    TextView supervisorNameTextVew, croNameTextVew, colorNameTextVew,
            electricalNameTextVew, salesNameTextVew, staffNameTextVew;

    TextView supervisorEmailTextView, croEmailTextView, colorEmailTextView,
            electricalEmailTextView, salesEmailTextView;

    TextView supervisorMobileTextView, croMobileTextView, colorMobileTextView,
            electricalMobileTextView, salesMobileTextView;

    ImageView supervisorCallImageView, supervisorMailImageView,
            croCallIcon, croMailIcon,
            colorCallIcon, colorMailIcon,
            electricalCallIcon, electricalMailIcon,
            salesCallIcon, salesMailIcon,
            staffCallIcon, staffMailIcon;

    String currentJob = "", previousJob = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor();
        setContentView(R.layout.activity_contacts);
        controller = (AppController) getApplicationContext();
        controller.getAnalytics().dashboardContactsIconTouchEvent();
        actionBarSettings();
        initializeUIElements();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!apiCallStarted) {
            dataRefresh();
        }
    }

    public void initializeUIElements() {

        ImageView backImage = findViewById(R.id.back_image);
        backImage.setVisibility(View.GONE);
        backImage.setOnClickListener(view -> onBackPressed());
        TextView firstText = findViewById(R.id.firstText);
        firstText.setText("Contacts");
        TextView secondText = findViewById(R.id.secondText);
        secondText.setVisibility(View.GONE);


        supervisorNameTextVew = (TextView) findViewById(R.id.supervisorNameTextVew);
        croNameTextVew = (TextView) findViewById(R.id.croNameTextVew);
        colorNameTextVew = (TextView) findViewById(R.id.colorNameTextVew);
        electricalNameTextVew = (TextView) findViewById(R.id.electricalNameTextVew);
        salesNameTextVew = (TextView) findViewById(R.id.salesNameTextVew);
        staffNameTextVew = (TextView) findViewById(R.id.staffNameTextVew);

        supervisorEmailTextView = (TextView) findViewById(R.id.supervisorEmailTextView);
        croEmailTextView = (TextView) findViewById(R.id.croEmailTextView);
        colorEmailTextView = (TextView) findViewById(R.id.colorEmailTextView);
        electricalEmailTextView = (TextView) findViewById(R.id.electricalEmailTextView);
        salesEmailTextView = (TextView) findViewById(R.id.salesEmailTextView);

        supervisorMobileTextView = (TextView) findViewById(R.id.supervisorMobileTextView);
        croMobileTextView = (TextView) findViewById(R.id.croMobileTextView);
        colorMobileTextView = (TextView) findViewById(R.id.colorMobileTextView);
        electricalMobileTextView = (TextView) findViewById(R.id.electricalMobileTextView);
        salesMobileTextView = (TextView) findViewById(R.id.salesMobileTextView);


        supervisorCallImageView = (ImageView) findViewById(R.id.supervisorCallImageView);
        supervisorMailImageView = (ImageView) findViewById(R.id.supervisorMailImageView);
        croCallIcon = (ImageView) findViewById(R.id.croCallIcon);
        croMailIcon = (ImageView) findViewById(R.id.croMailIcon);
        colorCallIcon = (ImageView) findViewById(R.id.colorCallIcon);
        colorMailIcon = (ImageView) findViewById(R.id.colorMailIcon);
        electricalCallIcon = (ImageView) findViewById(R.id.electricalCallIcon);
        electricalMailIcon = (ImageView) findViewById(R.id.electricalMailIcon);
        salesCallIcon = (ImageView) findViewById(R.id.salesCallIcon);
        salesMailIcon = (ImageView) findViewById(R.id.salesMailIcon);
        staffCallIcon = (ImageView) findViewById(R.id.staffCallIcon);
        staffMailIcon = (ImageView) findViewById(R.id.staffMailIcon);
        supervisorCallImageView.setOnClickListener(this);
        supervisorMailImageView.setOnClickListener(this);
        croCallIcon.setOnClickListener(this);
        croMailIcon.setOnClickListener(this);
        colorCallIcon.setOnClickListener(this);
        colorMailIcon.setOnClickListener(this);
        electricalCallIcon.setOnClickListener(this);
        electricalMailIcon.setOnClickListener(this);
        salesCallIcon.setOnClickListener(this);
        salesMailIcon.setOnClickListener(this);
        staffCallIcon.setOnClickListener(this);
        staffMailIcon.setOnClickListener(this);
        dataRefresh();

        /*contactsModelDetails = controller.getContactsModelDetails();
        if(!currentJob.equalsIgnoreCase(previousJob)){
            contactsModelDetails = new ContactsModel();
            getContactsDetails();
        }else{
            if (contactsModelDetails == null) {
                contactsModelDetails = new ContactsModel();
                getContactsDetails();
            } else {
                supervisorNameTextVew.setText(contactsModelDetails.getSiteSupervisor());
                croNameTextVew.setText(contactsModelDetails.getClientRelationsOfficer());
                colorNameTextVew.setText(contactsModelDetails.getColorConsultant());
                electricalNameTextVew.setText(contactsModelDetails.getElectricalConsultant());
                salesNameTextVew.setText(contactsModelDetails.getSalesConsultant());
                staffNameTextVew.setText(contactsModelDetails.getStaffManager());

                supervisorEmailTextView.setText(contactsModelDetails.getSupervisorEmail());
                croEmailTextView.setText(contactsModelDetails.getcROEmail());
                colorEmailTextView.setText(contactsModelDetails.getColourEmail());
                electricalEmailTextView.setText(contactsModelDetails.getElectricalEmail());
                salesEmailTextView.setText(contactsModelDetails.getSalesEmail());

                supervisorMobileTextView.setText(contactsModelDetails.getSiteSupervisorPhone());
                croMobileTextView.setText(contactsModelDetails.getcROPhone());
                colorMobileTextView.setText(contactsModelDetails.getInteriorDesignerPhone());
                electricalMobileTextView.setText(contactsModelDetails.getElectricalConsultantPhone());
                salesMobileTextView.setText(contactsModelDetails.getNewHomeConsultantPhone());
            }
        }*/
    }

    public void dataRefresh() {
        currentJob = controller.getMy_Place_Details().getJobNumber();
        previousJob = controller.getContactsJobNumber();
        contactsModelDetails = controller.getContactsModelDetails();
        if (!currentJob.equalsIgnoreCase(previousJob)) {
            apiCallStarted = true;
            setTextToUi(true);
            contactsModelDetails = new ContactsModel();
            getContactsDetails();
            controller.setContactsJobNumber(controller.getMy_Place_Details().getJobNumber());
        } else {
            if (contactsModelDetails == null) {
                setTextToUi(true);
                contactsModelDetails = new ContactsModel();
                getContactsDetails();
            } else {
                setTextToUi(false);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.notificationBtn) {
        } else if (id == R.id.supervisorCallImageView) {
            if (contactsModelDetails.getSiteSupervisorPhone() != null &&
                    contactsModelDetails.getSiteSupervisorPhone().length() > 2) {
                call(contactsModelDetails.getSiteSupervisorPhone());
                controller.getAnalytics().dashboardContactsCallIconTouchEvent();
            }
        } else if (id == R.id.supervisorMailImageView) {
            if (contactsModelDetails.getSupervisorEmail() != null &&
                    contactsModelDetails.getSupervisorEmail().length() > 2) {
                sendEmail(contactsModelDetails.getSupervisorEmail());
                controller.getAnalytics().dashboardContactsEmailIconTouchEvent();
            }
        } else if (id == R.id.croCallIcon) {
            if (contactsModelDetails.getcROPhone() != null &&
                    contactsModelDetails.getcROPhone().length() > 2) {
                call(contactsModelDetails.getcROPhone());
                controller.getAnalytics().dashboardContactsCallIconTouchEvent();
            }
        } else if (id == R.id.croMailIcon) {
            if (contactsModelDetails.getcROEmail() != null &&
                    contactsModelDetails.getcROEmail().length() > 2) {
                sendEmail(contactsModelDetails.getcROEmail());
                controller.getAnalytics().dashboardContactsEmailIconTouchEvent();
            }
        } else if (id == R.id.colorCallIcon) {
            if (contactsModelDetails.getInteriorDesignerPhone() != null &&
                    contactsModelDetails.getInteriorDesignerPhone().length() > 2) {
                call(contactsModelDetails.getInteriorDesignerPhone());
                controller.getAnalytics().dashboardContactsCallIconTouchEvent();
            }
        } else if (id == R.id.colorMailIcon) {
            if (contactsModelDetails.getColourEmail() != null &&
                    contactsModelDetails.getColourEmail().length() > 2) {
                sendEmail(contactsModelDetails.getColourEmail());
                controller.getAnalytics().dashboardContactsEmailIconTouchEvent();
            }
        } else if (id == R.id.electricalCallIcon) {
            if (contactsModelDetails.getElectricalConsultantPhone() != null &&
                    contactsModelDetails.getElectricalConsultantPhone().length() > 2) {
                call(contactsModelDetails.getElectricalConsultantPhone());
                controller.getAnalytics().dashboardContactsCallIconTouchEvent();
            }
        } else if (id == R.id.electricalMailIcon) {
            if (contactsModelDetails.getElectricalEmail() != null &&
                    contactsModelDetails.getElectricalEmail().length() > 2) {
                sendEmail(contactsModelDetails.getElectricalEmail());
                controller.getAnalytics().dashboardContactsEmailIconTouchEvent();
            }
        } else if (id == R.id.salesCallIcon) {
            if (contactsModelDetails.getNewHomeConsultantPhone() != null &&
                    contactsModelDetails.getNewHomeConsultantPhone().length() > 2) {
                call(contactsModelDetails.getNewHomeConsultantPhone());
                controller.getAnalytics().dashboardContactsCallIconTouchEvent();
            }
        } else if (id == R.id.salesMailIcon) {
            if (contactsModelDetails.getSalesEmail() != null &&
                    contactsModelDetails.getSalesEmail().length() > 2) {
                sendEmail(contactsModelDetails.getSalesEmail());
                controller.getAnalytics().dashboardContactsEmailIconTouchEvent();
            }
        }

    }

    public void sendEmail(String mail) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", mail, null));
        String name ="";
        if ((controller.getUserProfile().getUserDetails().size() > 0) && (controller.getUserProfile().getUserDetails().get(0).getFullName() != null)) {
            name = " - "+controller.getUserProfile().getUserDetails().get(0).getFullName();
        } else {
            name = "";
        }
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MyPlace – "+controller.getMy_Place_Details().getJobNumber()+name);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n* Kindly do not change the subject to track your queries.");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void call(String mobile) {
        if (mobile != null && !mobile.equalsIgnoreCase("null") && mobile.length() > 0) {
            String test2;
            if (mobile.length() > 10) {
                String test = mobile.substring(1);
                test2 = "+61" + test;
            } else {
                test2 = "+61" + mobile;
            }
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", test2, null)));
        }
    }


    public void getContactsDetails() {
        if (controller.getMy_Place_Details() != null) {
            if (Utils.isNetworkAvailable(ContactsActivity.this)) {
                dialog = Utils.getProgress(ContactsActivity.this);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String result = controller.webApiCall().postData_to_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceUserCheckUrl, getMyPlaceLoginJson());
                        if (result.equalsIgnoreCase("true")) {
                            String result2 = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceGetUserDetailsUrl);
                            if (result2 != null && !result2.equalsIgnoreCase("null")) {
                                final String contactDetails = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.MyPlaceGetContactDetails + "" + controller.getJobNumber());
                                if (contactDetails != null) {
                                    if (contactDetails.contains("Error")) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (contactDetails.contains("timeout")) {
                                                    Toast.makeText(ContactsActivity.this, "Error: Network is slow,\nPlease try again later", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(ContactsActivity.this, contactDetails, Toast.LENGTH_LONG).show();
                                                }
                                                dialog.cancel();
                                            }
                                        });
                                    } else {
                                        onSuccessResult(contactDetails);
                                    }
                                }else{
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.cancel();
                                            Utils.showToast(ContactsActivity.this, "My place details not valid for this job number", Common.errorCase);
                                            infoDialog();
                                        }
                                    });
                                }
                            }

                        } else if (result.equalsIgnoreCase("false")) {

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.cancel();
                                    Utils.showToast(ContactsActivity.this, "My place details not valid for this job number", Common.errorCase);
                                    infoDialog();
                                }
                            });
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.cancel();
                                }
                            });
                        }
                        /*String url1 = Common.myPlaceBaseUrlVic + "" + Common.MyPlaceUserCheckUrl;
                        String result = controller.webApiCall().postData_to_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.MyPlaceUserCheckUrl, getMyPlaceLoginJson());
                        if (result.equalsIgnoreCase("true")) {
                            String url2 = Common.myPlaceBaseUrlVic + "" + Common.MyPlaceGetUserDetailsUrl;
                            String url3 = Common.myPlaceBaseUrlVic + "" + Common.MyPlaceGetContactDetails + "" + controller.getMy_Place_Details().getJobNumber();
                            final String contactDetails = controller.webApiCall().getContactDetails(url2, url3);
                            if(contactDetails != null && contactDetails.contains("Error")){
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(contactDetails.contains("timeout")){
                                            Toast.makeText(ContactsActivity.this,"Error: Network is slow,\nPlease try again later",Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(ContactsActivity.this,contactDetails,Toast.LENGTH_LONG).show();
                                        }
                                        dialog.cancel();
                                    }
                                });
                            }else{
                                onSuccessResult(contactDetails);
                            }
                        } else {
                            controller.setContactsModelDetails(null);
                            apiCallStarted = false;
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.cancel();
                                }
                            });
                        }*/
                    }
                });
                t.start();
            } else {
                apiCallStarted = false;
                controller.setContactsModelDetails(null);
            }
        } else {
            apiCallStarted = false;
            controller.setContactsModelDetails(null);
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

    public void onSuccessResult(String result) {
        apiCallStarted = false;
        if (result != null && result.length() > 0) {
            contactsModelDetails = new ContactsModel(result);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    dialog.cancel();

                    setTextToUi(false);

                /*if (contactsModelDetails.getSupervisorEmail().length() > 2) {
                    supervisorMailImageView.setVisibility(View.VISIBLE);
                }
                if (contactsModelDetails.getcROEmail().length() > 2) {
                    croMailIcon.setVisibility(View.VISIBLE);
                }
                if (contactsModelDetails.getColourEmail().length() > 2) {
                    colorMailIcon.setVisibility(View.VISIBLE);
                }
                if (contactsModelDetails.getElectricalEmail().length() > 2) {
                    electricalMailIcon.setVisibility(View.VISIBLE);
                }
                if (contactsModelDetails.getSalesEmail().length() > 2) {
                    salesMailIcon.setVisibility(View.VISIBLE);
                }*/
                }
            });
            if (contactsModelDetails.isExceptionRaised()) {
                controller.setContactsModelDetails(null);
            } else {
                controller.setContactsModelDetails(contactsModelDetails);
            }

        } else {

            controller.setContactsModelDetails(null);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    dialog.cancel();
                }
            });
        }

    }

    public void setTextToUi(boolean empty) {
        if (empty) {
            supervisorNameTextVew.setText("--");
            croNameTextVew.setText("--");
            colorNameTextVew.setText("--");
            electricalNameTextVew.setText("--");
            salesNameTextVew.setText("--");
            staffNameTextVew.setText("--");
            supervisorEmailTextView.setText("--");
            croEmailTextView.setText("--");
            colorEmailTextView.setText("--");
            electricalEmailTextView.setText("--");
            salesEmailTextView.setText("--");
            supervisorMobileTextView.setText("--");
            croMobileTextView.setText("--");
            colorMobileTextView.setText("--");
            electricalMobileTextView.setText("--");
            salesMobileTextView.setText("--");
            supervisorNameTextVew.setText("--");

        } else {
            if (contactsModelDetails.getSiteSupervisor() != null && contactsModelDetails.getSiteSupervisor().trim().length() > 0) {
                supervisorNameTextVew.setText(contactsModelDetails.getSiteSupervisor());
            } else {
                supervisorNameTextVew.setText("--");
            }

            if (contactsModelDetails.getClientRelationsOfficer() != null && contactsModelDetails.getClientRelationsOfficer().trim().length() > 0) {
                croNameTextVew.setText(contactsModelDetails.getClientRelationsOfficer());
            } else {
                croNameTextVew.setText("--");
            }

            if (contactsModelDetails.getColorConsultant() != null && contactsModelDetails.getColorConsultant().trim().length() > 0) {
                colorNameTextVew.setText(contactsModelDetails.getColorConsultant());
            } else {
                colorNameTextVew.setText("--");
            }

            if (contactsModelDetails.getElectricalConsultant() != null && contactsModelDetails.getElectricalConsultant().trim().length() > 0) {
                electricalNameTextVew.setText(contactsModelDetails.getElectricalConsultant());
            } else {
                electricalNameTextVew.setText("--");
            }

            if (contactsModelDetails.getSalesConsultant() != null && contactsModelDetails.getSalesConsultant().trim().length() > 0) {
                salesNameTextVew.setText(contactsModelDetails.getSalesConsultant());
            } else {
                salesNameTextVew.setText("--");
            }

            if (contactsModelDetails.getStaffManager() != null && contactsModelDetails.getStaffManager().trim().length() > 0) {
                staffNameTextVew.setText(contactsModelDetails.getStaffManager());
            } else {
                staffNameTextVew.setText("--");
            }


            if (contactsModelDetails.getSupervisorEmail() != null && contactsModelDetails.getSupervisorEmail().trim().length() > 0) {
                supervisorEmailTextView.setText(contactsModelDetails.getSupervisorEmail());
                supervisorMailImageView.setVisibility(View.VISIBLE);
            } else {
                supervisorMailImageView.setVisibility(View.INVISIBLE);
                supervisorEmailTextView.setText("--");
            }

            if (contactsModelDetails.getcROEmail() != null && contactsModelDetails.getcROEmail().trim().length() > 0) {
                croEmailTextView.setText(contactsModelDetails.getcROEmail());
                croMailIcon.setVisibility(View.VISIBLE);
            } else {
                croMailIcon.setAlpha(0.4f);
                //croMailIcon.setVisibility(View.VISIBLE);
                croEmailTextView.setText("--");
            }

            if (contactsModelDetails.getColourEmail() != null && contactsModelDetails.getColourEmail().trim().length() > 0) {
                colorEmailTextView.setText(contactsModelDetails.getColourEmail());
                colorMailIcon.setVisibility(View.VISIBLE);
            } else {
                colorMailIcon.setAlpha(0.4f);
                //colorMailIcon.setVisibility(View.INVISIBLE);
                colorEmailTextView.setText("--");
            }

            if (contactsModelDetails.getElectricalEmail() != null && contactsModelDetails.getElectricalEmail().trim().length() > 0) {
                electricalEmailTextView.setText(contactsModelDetails.getElectricalEmail());
                electricalMailIcon.setVisibility(View.VISIBLE);

            } else {
                electricalMailIcon.setAlpha(0.4f);
               // electricalMailIcon.setVisibility(View.INVISIBLE);
                electricalEmailTextView.setText("--");
            }

            if (contactsModelDetails.getSalesEmail() != null && contactsModelDetails.getSalesEmail().trim().length() > 0) {
                salesEmailTextView.setText(contactsModelDetails.getSalesEmail());
                salesMailIcon.setVisibility(View.VISIBLE);
            } else {
                salesMailIcon.setAlpha(0.4f);
                //salesMailIcon.setVisibility(View.INVISIBLE);
                salesEmailTextView.setText("--");
            }


            if (contactsModelDetails.getSiteSupervisorPhone() != null && contactsModelDetails.getSiteSupervisorPhone().trim().length() > 0) {
                supervisorMobileTextView.setText(contactsModelDetails.getSiteSupervisorPhone());
                supervisorCallImageView.setVisibility(View.VISIBLE);
            } else {
                supervisorCallImageView.setAlpha(0.4f);
                //supervisorCallImageView.setVisibility(View.INVISIBLE);
                supervisorMobileTextView.setText("--");
            }

            if (contactsModelDetails.getcROPhone() != null && contactsModelDetails.getcROPhone().trim().length() > 0) {
                croMobileTextView.setText(contactsModelDetails.getcROPhone());
                croCallIcon.setVisibility(View.VISIBLE);
            } else {
                croCallIcon.setAlpha(0.4f);
                //croCallIcon.setVisibility(View.INVISIBLE);
                croMobileTextView.setText("--");
            }

            if (contactsModelDetails.getInteriorDesignerPhone() != null && contactsModelDetails.getInteriorDesignerPhone().trim().length() > 0) {
                colorMobileTextView.setText(contactsModelDetails.getInteriorDesignerPhone());
                colorCallIcon.setVisibility(View.VISIBLE);
            } else {
                colorCallIcon.setAlpha(0.4f);
                //colorCallIcon.setVisibility(View.INVISIBLE);
                colorMobileTextView.setText("--");
            }
            if (contactsModelDetails.getElectricalConsultantPhone() != null && contactsModelDetails.getElectricalConsultantPhone().trim().length() > 0) {
                electricalMobileTextView.setText(contactsModelDetails.getElectricalConsultantPhone());
                electricalCallIcon.setVisibility(View.VISIBLE);

            } else {
                electricalCallIcon.setAlpha(0.4f);
               // electricalCallIcon.setVisibility(View.INVISIBLE);
                electricalMobileTextView.setText("--");
            }

            if (contactsModelDetails.getNewHomeConsultantPhone() != null && contactsModelDetails.getNewHomeConsultantPhone().trim().length() > 0) {
                salesMobileTextView.setText(contactsModelDetails.getNewHomeConsultantPhone());
                salesCallIcon.setVisibility(View.VISIBLE);
            } else {
                salesCallIcon.setAlpha(0.4f);
                //salesCallIcon.setVisibility(View.INVISIBLE);
                salesMobileTextView.setText("--");
            }
        }

    }


    public void actionBarSettings() {
       /* ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();*/

       /* actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.custom_actionbar_transparent, null);
        actionBarView.setBackgroundResource(R.color.white);
        final ImageView backImageView = (ImageView) actionBarView.findViewById(R.id.backImageView);
        backImageView.setVisibility(View.GONE);
        final ImageView burbankLogoImageView = (ImageView) actionBarView.findViewById(R.id.burbankLogoImageView);
        burbankLogoImageView.setVisibility(View.GONE);
        TextView actionBarHeadingTextView = (TextView) actionBarView.findViewById(R.id.actionBarHeadingTextView);
        actionBarHeadingTextView.setVisibility(View.VISIBLE);
        actionBarHeadingTextView.setText("Contacts");
        TextView saveSettings = (TextView) actionBarView.findViewById(R.id.saveSettings);
        saveSettings.setVisibility(View.GONE);
        actionBar.setCustomView(actionBarView, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        Toolbar parent = (Toolbar) actionBarView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);*/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*final Dialog exitDialog = new Dialog(ContactsActivity.this);
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

    public void infoDialog() {
        final Dialog infoDialog = new Dialog(ContactsActivity.this);
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoDialog.setContentView(R.layout.custom_dialog);
        final Button dialogOkButton = (Button) infoDialog.findViewById(R.id.dialogOkButton);
        final TextView dialogMessageTextView = (TextView) infoDialog.findViewById(R.id.dialogMessageTextView);
        TextView dialogHeadingTextView = (TextView) infoDialog.findViewById(R.id.dialogHeadingTextView);
        dialogHeadingTextView.setText("My Place Validation");
        dialogMessageTextView.setText("Errror : Invalid details for selected job number.\nPlease select other job.");
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
            }
        });
        infoDialog.show();
    }

}
