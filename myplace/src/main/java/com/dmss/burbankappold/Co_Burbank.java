package com.dmss.burbankappold;

import android.app.Dialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.CoBurbank_List_Adapter;
import common.AppController;
import common.Common;
import common.Receiver;
import common.TransparentProgressDialog;
import common.Utils;
import interfaces.Co_Burbank_Delete_Reinvite_Callback;
import interfaces.WebApiResponseCallback;
import models.Co_Burbank_Model;


/**
 * Created by Ashish.Kumar on 13-06-2017.
 */

public class Co_Burbank extends BaseActivity implements View.OnClickListener, WebApiResponseCallback, Co_Burbank_Delete_Reinvite_Callback {
    ListView coburbankListView;
    CoBurbank_List_Adapter coBurbank_list_adapter;
    private ArrayList<Co_Burbank_Model> coBurbankList = new ArrayList<Co_Burbank_Model>();
    TextView coBurbankInviteButton;
    Receiver myBroadcastReceiver;
    AppController controller;
    TextView jobNumber;
    TransparentProgressDialog pd;
    boolean isCalledFromCallBack = false;
    int inviteCall = 1, deleteApiCall = 2, getCoBurbankList = 3;
    int apiCall;
    boolean isFirstTimeRequested = false;
    int position = 0;
    Dialog exitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_co_burbank);
        changeStatusBarColor();
        controller = (AppController)getApplicationContext();
        controller.getAnalytics().setScreen(Co_Burbank.this,"Share_with_Partners_Screen");
        actionBarSettings();
        initializeUIElements();
        if (Utils.isNetworkAvailable(Co_Burbank.this)) {
            pd = Utils.getProgress(Co_Burbank.this);
            apiCall = getCoBurbankList;
            isFirstTimeRequested = true;
            controller.webApiCall().postData(Common.getCoBurbankUrl, getJson(""), this);
        }
    }

    public String getJson(String value) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (apiCall == getCoBurbankList) {
                jsonObject.put("JobNumber", controller.getMy_Place_Details().getJobNumber());
                jsonObject.put("UserId", controller.getUserProfile().getUserDetails().get(0).getId());
            } else if (apiCall == inviteCall) {
                jsonObject.put("JobNumber", controller.getMy_Place_Details().getJobNumber());
                jsonObject.put("UserId", controller.getUserProfile().getUserDetails().get(0).getId());
                jsonObject.put("InvitationToEmail", value);
                jsonObject.put("Email", value);
                jsonObject.put("AddByPrimary", getPrimaryStatus());
                jsonObject.put("CanReInvite", coBurbankList.get(position).isCanReInvite());
                isCalledFromCallBack = false;
            } else if (apiCall == deleteApiCall) {
                jsonObject.put("JobNumber", coBurbankList.get(position).getJobNumber());
                jsonObject.put("UserId", coBurbankList.get(position).getUserId());
                jsonObject.put("CoBurbank", coBurbankList.get(position).isCoBurbank());
                jsonObject.put("Email", coBurbankList.get(position).getEmail());
                jsonObject.put("DeletedByUserId", controller.getUserProfile().getUserDetails().get(0).getId());

            }
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return jsonObject.toString();
    }

    public boolean getPrimaryStatus() {
        boolean status = false;
        for (int i = 0; i < coBurbankList.size(); i++) {
            if (coBurbankList.get(i).isPrimaryApplicant()) {
                if (coBurbankList.get(i).getEmail().equalsIgnoreCase(controller.getUserProfile().getUserDetails().get(0).getEmail())) {
                    status = true;
                    break;
                }
            }
        }
        return status;
    }

    public void onResume() {
        super.onResume();
        registerReceiver(myBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(myBroadcastReceiver);
    }

    public void initializeUIElements() {
        myBroadcastReceiver = new Receiver();
        jobNumber = (TextView) findViewById(R.id.jobNumber);
        coBurbankInviteButton = (TextView) findViewById(R.id.coBurbankInviteButton);
        coBurbankInviteButton.setOnClickListener(this);
        coburbankListView = (ListView) findViewById(R.id.listView);
        jobNumber.setText(controller.getMy_Place_Details().getJobNumber() + "(" + controller.getMy_Place_Details().getRegion() + ")");
    }

    @Override
    public void onClick(View view) {
        isCalledFromCallBack = false;
        if (getPrimaryStatus() == false) {
            controller.getAnalytics().moreFeaturesShareWithPartnerReferButtonTouchEvent();
        } else {
            controller.getAnalytics().moreFeaturesShareWithPartnerInviteButtonTouchEvent();
        }
        inviteDialog("");
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
        first.setText("Share with partner");
        TextView second = findViewById(R.id.secondText);
        second.setVisibility(View.GONE);


       /* ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.custom_actionbar_transparent, null);
        actionBarView.setBackgroundResource(R.color.black);
        final ImageView backImageView = (ImageView) actionBarView.findViewById(R.id.backImageView);
        backImageView.setVisibility(View.VISIBLE);
        final ImageView burbankLogoImageView = (ImageView) actionBarView.findViewById(R.id.burbankLogoImageView);
        burbankLogoImageView.setVisibility(View.GONE);
        TextView actionBarHeadingTextView = (TextView) actionBarView.findViewById(R.id.actionBarHeadingTextView);
        actionBarHeadingTextView.setVisibility(View.VISIBLE);
        actionBarHeadingTextView.setText("Share with partner");
        TextView saveSettings = (TextView) actionBarView.findViewById(R.id.saveSettings);
        saveSettings.setVisibility(View.GONE);
        actionBar.setCustomView(actionBarView, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        Toolbar parent = (Toolbar) actionBarView.getParent();
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
    public void onSuccessResult(String result) {

        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.w("coburbank output", result);
                if (jsonObject.getBoolean(Common.Status_Key) == true) {
                    if (exitDialog != null) {
                        exitDialog.cancel();
                    }

                    JSONArray data = jsonObject.getJSONArray(Common.Result_Key);
                    if (data.length() > 0) {
                        coBurbankList.clear();
                    }
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObj = data.getJSONObject(i);
                        coBurbankList.add(new Co_Burbank_Model(jsonObj));
                    }
                    if (coBurbankList.size() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                coBurbank_list_adapter = new CoBurbank_List_Adapter(Co_Burbank.this, coBurbankList);
                                coburbankListView.setAdapter(coBurbank_list_adapter);
                                if (coBurbankList.size() < 5) {
                                    coBurbankInviteButton.setVisibility(View.VISIBLE);
                                } else {
                                    coBurbankInviteButton.setVisibility(View.GONE);
                                }
                                if (getPrimaryStatus() == false) {
                                    coBurbankInviteButton.setText("Refer");
                                } else {
                                    coBurbankInviteButton.setText("Invite");
                                }

                            }
                        });
                        if (isFirstTimeRequested == true) {
                            isFirstTimeRequested = false;
                        } else {
                            Utils.showToast(Co_Burbank.this, jsonObject.getString(Common.Message), Common.sucessCase);
                        }
                    } else {
                        Utils.showToast(Co_Burbank.this, jsonObject.getString(Common.Message), Common.errorCase);
                    }

                } else {
                    Utils.showToast(Co_Burbank.this, jsonObject.getString(Common.Message), Common.errorCase);
                }
            } catch (Exception ex) {
                ex.fillInStackTrace();
                Utils.showToast(Co_Burbank.this, "Exception.", Common.errorCase);
            }
        } else {
            Utils.showToast(Co_Burbank.this, Common.somethingErrorMessage, Common.errorCase);
        }
        if (pd != null) {
            pd.cancel();
        }
    }

    @Override
    public void onErrorResult(String error) {
        Utils.showToast(Co_Burbank.this, Common.somethingErrorMessage, Common.errorCase);
    }

    public void inviteDialog(String email) {
        exitDialog = new Dialog(Co_Burbank.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitDialog.setContentView(R.layout.co_burbank_custom_dialog);
        final EditText emailId = (EditText) exitDialog.findViewById(R.id.coBurbankEmailEditText);
        final TextView dialogOkButton = (TextView) exitDialog.findViewById(R.id.coBurbankOkButton);
        final TextView dialogCancelButton = (TextView) exitDialog.findViewById(R.id.coBurbankCancelButton);
        final TextView laterTextView = (TextView) exitDialog.findViewById(R.id.coBurbankLaterTextView);
        dialogCancelButton.setTypeface(controller.getTypeface());
        dialogOkButton.setTypeface(controller.getTypeface());
        exitDialog.setCancelable(false);
        emailId.setText(email);

        if (email.length() > 0) {
            emailId.setEnabled(false);
        }
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailId.getText().length() > 0) {
                    if (controller.getValidation().validateEmail(emailId, Co_Burbank.this)) {
                        if (Utils.isNetworkAvailable(Co_Burbank.this)) {
                            if (isInvitedSelf(emailId.getText().toString()) == false) {
                                if (isInvitingUserAlreadyExistingUser(emailId.getText().toString()) == false) {
                                    apiCall = inviteCall;
                                    boolean val = false;
                                    if ((isCalledFromCallBack == true)) {
                                        if ((coBurbankList.get(position).isCanInvite() == true) || (coBurbankList.get(position).isCanReInvite() == true)) {
                                            val = true;
                                        }
                                    }
                                    controller.webApiCall().postData(Common.getInvite_ReInvite_Url(val), getJson(emailId.getText().toString()), Co_Burbank.this, Utils.getProgress(Co_Burbank.this));
                                } else {
                                    Utils.showToast(Co_Burbank.this, emailId.getText().toString() + "  already exist. For More details please check the status in the Co-burbank list.", Common.errorCase);
                                }
                            } else {
                                Utils.showToast(Co_Burbank.this, "You cannot send invitation to self.", Common.errorCase);
                            }
                        }
                    } else {
                        Utils.showToast(Co_Burbank.this, "Please enter valid email id", Common.errorCase);
                    }
                } else {
                    Utils.showToast(Co_Burbank.this, "Please enter email id", Common.errorCase);
                }
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

    public boolean isInvitedSelf(String emailId) {
        boolean status = false;
        if (controller.getUserProfile().getUserDetails().get(0).getEmail().equalsIgnoreCase(emailId.trim())) {
            status = true;
        }
        return status;
    }

    boolean isInvitingUserAlreadyExistingUser(String emailId) {
        boolean status = false;
        if ((isCalledFromCallBack == false)) {
            for (int i = 0; i < coBurbankList.size(); i++) {
                if (coBurbankList.get(i).getEmail().trim().equalsIgnoreCase(emailId.trim())) {
                    status = true;
                    break;
                }
            }
        }
        return status;
    }

    @Override
    public void delete(int position) {
        if (Utils.isNetworkAvailable(Co_Burbank.this)) {
            apiCall = deleteApiCall;
            this.position = position;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    exitDialog();
                }
            });
        }
    }

    @Override
    public void reInvite(final int position) {
        this.position = position;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isCalledFromCallBack = true;
                inviteDialog(coBurbankList.get(position).getEmail());
            }
        });

    }

    public void exitDialog() {
        final Dialog exitDialog = new Dialog(Co_Burbank.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitDialog.setContentView(R.layout.custom_dialog);
        TextView dialogHeadingTextView = (TextView) exitDialog.findViewById(R.id.dialogHeadingTextView);
        final Button dialogOkButton = (Button) exitDialog.findViewById(R.id.dialogOkButton);
        final Button dialogCancelButton = (Button) exitDialog.findViewById(R.id.dialogCancelButton);
        final TextView dialogMessageTextView = (TextView) exitDialog.findViewById(R.id.dialogMessageTextView);
        dialogMessageTextView.setText("Are you sure you want to delete.");
        dialogHeadingTextView.setText("Alert");
        dialogHeadingTextView.setTypeface(controller.getTypeface());
        dialogOkButton.setText("Yes");
        dialogCancelButton.setText("No");
        LinearLayout cancelButtonLayout = (LinearLayout) exitDialog.findViewById(R.id.cancelButtonLayout);
        cancelButtonLayout.setVisibility(View.VISIBLE);
        dialogOkButton.setVisibility(View.VISIBLE);
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
                controller.webApiCall().postData(Common.getDeleteCoBurbankUrl, getJson(""), Co_Burbank.this, Utils.getProgress(Co_Burbank.this));
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

}