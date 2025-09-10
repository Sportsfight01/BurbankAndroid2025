package com.dmss.burbankappold;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import common.AppController;
import common.Common;
import common.CustomEditText;
import common.CustomImageView;
import common.TransparentProgressDialog;
import common.Utils;
import interfaces.WebApiResponseCallback;

public class MyPlaceSettingActivity extends BaseActivity implements WebApiResponseCallback, View.OnClickListener {
    TextView logoutMyPlaceTextView;
    AppController controller;

    TextView userName, emailId, resetPassword, versionCode,
            photosTextView, stageTextView, constructionTextView;
    String photosText, stageText, constructionText;
    String fullName;
    boolean is_New_Photo_NotificationActivated = false;
    boolean is_ChangesInConstructionStatus_NotificationActivated = false;
    boolean is_StageCompletedNotificationActivated = false;
    ImageView photoNotification, constructionNotification, stageNotification;
    Button save;
    WebApiResponseCallback callback;
    //boolean isUpdateSettingRequested;
    LinearLayout terms, privacy;
    public final int permissionReadCamera = 1;
    final public static int SELECT_FILE = 02;
    TextView editImage;
    CustomImageView profilePic;
    boolean isImageCaptured = false;
    // boolean isUpdateProfilePicRequested=false;
    AlertDialog alertDialog;
    TextView editProfile;
    Dialog profileDialog;
    int apiCall = 0;
    int updateSettings = 1, updateProfilePic = 2, updateProfile = 3, getNotification = 4;
    LinearLayout aboutBurbank;
    //String lastUpdatedTime = "";
    //String profilePicString = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_place_setting);
        changeStatusBarColor();
        controller = (AppController) getApplicationContext();
        controller.getAnalytics().setScreen(MyPlaceSettingActivity.this, "Settings_Screen");
        callback = this;
        actionBarSettings();
        initializeUIElements();
        int permissionCheck = ContextCompat.checkSelfPermission(MyPlaceSettingActivity.this, Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MyPlaceSettingActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        permissionReadCamera);
            }
        }
    }

    public void initializeUIElements() {
        TextView save = findViewById(R.id.save);
        save.setVisibility(View.VISIBLE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkAvailable(MyPlaceSettingActivity.this)) {
                    apiCall = updateSettings;
                    controller.webApiCall().postData(Common.updateMyPlaceNotificationSetting, getUpdateSettingsJson(), callback, Utils.getProgress(MyPlaceSettingActivity.this));
                }
            }
        });
        TextView firstText = findViewById(R.id.firstText);
        TextView secondText = findViewById(R.id.secondText);
        secondText.setVisibility(View.GONE);
        firstText.setText("Settings");

        ImageView backImage = findViewById(R.id.back_image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        aboutBurbank = (LinearLayout) findViewById(R.id.about);
        terms = (LinearLayout) findViewById(R.id.terms);
        privacy = (LinearLayout) findViewById(R.id.privacy);
        editProfile = (TextView) findViewById(R.id.editProfile);
        editImage = (TextView) findViewById(R.id.editBtn);
        profilePic = (CustomImageView) findViewById(R.id.imageView2);
        logoutMyPlaceTextView = (TextView) findViewById(R.id.logoutMyPlaceTextView);
        photoNotification = (ImageView) findViewById(R.id.photoNotificationImg);
        constructionNotification = (ImageView) findViewById(R.id.constructionNotificationImg);
        stageNotification = (ImageView) findViewById(R.id.stageNotificationImg);
        userName = (TextView) findViewById(R.id.userName);
        emailId = (TextView) findViewById(R.id.userEmailId);
        resetPassword = (TextView) findViewById(R.id.resetPassword);
        versionCode = (TextView) findViewById(R.id.versionCode);
        photosTextView = (TextView) findViewById(R.id.photosTextView);
        stageTextView = (TextView) findViewById(R.id.stageTextView);
        constructionTextView = (TextView) findViewById(R.id.constructionTextView);

        logoutMyPlaceTextView.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
        editProfile.setOnClickListener(this);


        userName.setText(controller.getUserProfile().getUserDetailses().get(0).getFullName());
        emailId.setText(controller.getUserProfile().getUserDetailses().get(0).getEmail());


        //lastUpdatedTime = controller.getLastUpdatedTime();

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionCode.setText(version);
            //versionCode.setText("0.5");
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        aboutBurbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.getAnalytics().settingsWhatIsBurbankAppForwardArrowTouchEvent();
                Intent in = new Intent(MyPlaceSettingActivity.this, AboutBurbank.class);
                startActivity(in);
            }
        });
        photoNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.getAnalytics().settingsPhotosAddedOnOffIconTouchEvent();
                if (is_New_Photo_NotificationActivated) {
                    is_New_Photo_NotificationActivated = false;
                    photoNotification.setImageResource(R.drawable.check_unfill);
                } else {
                    is_New_Photo_NotificationActivated = true;
                    photoNotification.setImageResource(R.drawable.check_fill);
                }
            }
        });
        constructionNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.getAnalytics().settingsStagesChangesOnOffIconTouchEvent();
                if (is_ChangesInConstructionStatus_NotificationActivated) {
                    is_ChangesInConstructionStatus_NotificationActivated = false;
                    constructionNotification.setImageResource(R.drawable.check_unfill);
                } else {
                    is_ChangesInConstructionStatus_NotificationActivated = true;
                    constructionNotification.setImageResource(R.drawable.check_fill);
                }
            }
        });
        stageNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.getAnalytics().settingsStageCompletionAddedOnOffIconTouchEvent();
                if (is_StageCompletedNotificationActivated) {
                    is_StageCompletedNotificationActivated = false;
                    stageNotification.setImageResource(R.drawable.check_unfill);
                } else {
                    is_StageCompletedNotificationActivated = true;
                    stageNotification.setImageResource(R.drawable.check_fill);
                }
            }
        });
        if (Utils.isNetworkAvailable(MyPlaceSettingActivity.this)) {
            apiCall = getNotification;
            controller.webApiCall().postData(Common.getMyPlaceNotificationSetting, getJson(), this, Utils.getProgress(MyPlaceSettingActivity.this));
        }
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.getAnalytics().settingsTermsOfUseForwardArrowTouchEvent();
                Intent in = new Intent(MyPlaceSettingActivity.this, Terms_Conditions.class);
                startActivity(in);
            }
        });
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.getAnalytics().settingsPrivacyPolicyForwardArrowTouchEvent();
                Intent in = new Intent(MyPlaceSettingActivity.this, Privacy.class);
                startActivity(in);
            }
        });
        editImage.setOnClickListener(this);
        profilePic.setOnClickListener(this);
    }

    public String getJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id", controller.getUserProfile().getUserDetailses().get(0).getId());
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return jsonObject.toString();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                Common.tempPath = Common.imageUri.getPath();
                isImageCaptured = true;
                showDialog();
            } else if (resultCode == RESULT_CANCELED) {

                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == SELECT_FILE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                Common.tempPath = c.getString(columnIndex);
                c.close();
                isImageCaptured = true;
                showDialog();

            } else {
                Toast.makeText(this, " This Image cannot be stored .please try with some other Image. ", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public String getUpdateProfileJson(String path) {

        JSONObject job = new JSONObject();
        try {
            job.put("UserId", controller.getUserProfile().getUserDetails().get(0).getId());
            job.put("ImageContent", getBase64(path));
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return job.toString();
    }


    public static String getBase64(String path) {
        Bitmap bm = BitmapFactory.decodeFile(path);
        Bitmap newBitmap = Bitmap.createScaledBitmap(bm, 500, 500, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedString = Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("String Base64 :", encodedString);
        return encodedString;


    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyPlaceSettingActivity.this)
                .setTitle("Update Profile Pic");
        final FrameLayout frameView = new FrameLayout(MyPlaceSettingActivity.this);
        builder.setView(frameView);
        alertDialog = builder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.uploadprofilepic, frameView);
        ImageView image = (ImageView) dialoglayout.findViewById(R.id.pic);
        Button upload = (Button) dialoglayout.findViewById(R.id.upload);
        upload.setTypeface(controller.getTypeface());
        final Uri uri = Uri.fromFile(new File(Common.tempPath));
        Picasso.get().load(uri)
                .resize(500, 500).centerCrop().into(image);
        upload.setTypeface(controller.getTypeface());
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageCaptured == true) {
                    apiCall = updateProfilePic;
                    TransparentProgressDialog pd = Utils.getProgress(MyPlaceSettingActivity.this);
                    controller.webApiCall().postData(Common.updateProfilePicUrl, getUpdateProfileJson(Common.tempPath), MyPlaceSettingActivity.this, pd);

                }
            }
        });
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == logoutMyPlaceTextView.getId()) {
            controller.getAnalytics().settingsLogoutButtonTouchEvent();
            controller.setUserLoggedIn(false);
            controller.setLoggedInFromSocial(false);
            controller.setMy_Place_Details(null);
            controller.setProfilePicUrl("");
            controller.setProfileInfo("");
            controller.setSelectedJobPosition(0);
            Intent in = new Intent(MyPlaceSettingActivity.this, FirstClass.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(in);
            overridePendingTransition(0, 0);
            finish();
        } else if (view.getId() == resetPassword.getId()) {
            controller.getAnalytics().settingsResetPasswordTouchEvent();
            Intent in = new Intent(MyPlaceSettingActivity.this, UpdatePassword.class);
            startActivity(in);
        } else if ((view.getId() == R.id.editBtn) || (view.getId() == R.id.imageView2)) {
            Utils.selectImageDialog(MyPlaceSettingActivity.this);
        } else if (view.getId() == editProfile.getId()) {
            controller.getAnalytics().settingsEditIconTouchEvent();
            displayProfileDialog();
        }

    }

    public void actionBarSettings() {
       /* ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);*/
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_actionbar_transparent, null);
        view.setBackgroundResource(R.color.black);
        final ImageView backImageView = (ImageView) view.findViewById(R.id.backImageView);
        backImageView.setVisibility(View.VISIBLE);
        final ImageView burbankLogoImageView = (ImageView) view.findViewById(R.id.burbankLogoImageView);
        burbankLogoImageView.setVisibility(View.GONE);
        TextView actionBarHeadingTextView = (TextView) view.findViewById(R.id.actionBarHeadingTextView);
        actionBarHeadingTextView.setVisibility(View.VISIBLE);
        actionBarHeadingTextView.setText("Settings");
        TextView saveSettings = (TextView) view.findViewById(R.id.saveSettings);
        saveSettings.setVisibility(View.VISIBLE);
       /* actionBar.setCustomView(view, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));*/
       /* Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(0, 0);*/
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkAvailable(MyPlaceSettingActivity.this)) {
                    apiCall = updateSettings;
                    controller.webApiCall().postData(Common.updateMyPlaceNotificationSetting, getUpdateSettingsJson(), callback, Utils.getProgress(MyPlaceSettingActivity.this));
                }
            }
        });

    }

    @Override
    public void onSuccessResult(String result) {
        if (result != null) {
            try {
                final JSONObject job = new JSONObject(result);
                Log.d("Status", job.toString());
                boolean status = job.getBoolean("Status");
                final String message = job.getString("Message");
                if (status) {
                    if (apiCall == updateProfilePic) {
                        final Uri uri = Uri.fromFile(new File(Common.tempPath));
                        Utils.showToast(MyPlaceSettingActivity.this, message, Common.sucessCase);
                        if (alertDialog != null) {
                            alertDialog.cancel();
                        }
                        JSONObject jsonObject = job.getJSONObject(Common.Result_Key);
                        final String profilePicString = jsonObject.isNull("ProfilePicPath") ? "" : jsonObject.getString("ProfilePicPath");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new DownloadImageTask()
                                        .execute(profilePicString);
                            }
                        });


                    } else if (apiCall == updateSettings) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showToast(MyPlaceSettingActivity.this, message, Common.sucessCase);
                                controller.setPhotoNotifications(is_New_Photo_NotificationActivated);
                                controller.setProgressNotifications(is_ChangesInConstructionStatus_NotificationActivated);
                                controller.setStageNotifications(is_StageCompletedNotificationActivated);
                            }
                        });
                    } else if (apiCall == getNotification) {
                        JSONObject jsonObject = job.getJSONObject("Result");
                        final String profilePicString = jsonObject.isNull("ProfilePicPath") ? "" : jsonObject.getString("ProfilePicPath");
                        JSONArray jsonArray = jsonObject.getJSONArray("NotificationTypes");
                        controller.setLastUpdatedTime(jsonObject.getString("UpdatedDate"));
                        final String userProfileName = jsonObject.isNull("UserName") ? "" : jsonObject.getString("UserName");
                        final String emailProfileId = jsonObject.isNull("Email") ? "" : jsonObject.getString("Email");

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (userName != null) {
                                    userName.setText(userProfileName);
                                }
                                if (emailId != null) {
                                    emailId.setText(emailProfileId);
                                }
                                if (profilePic != null) {
                                    new DownloadImageTask().execute(profilePicString);
                                }

                            }
                        });
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final JSONObject notification = jsonArray.getJSONObject(i);
                            switch (i) {
                                case 0:
                                    is_New_Photo_NotificationActivated = notification.isNull("IsUserOpted") ? false : notification.getBoolean("IsUserOpted");
                                    controller.setPhotoNotifications(is_New_Photo_NotificationActivated);
                                    photosText = notification.isNull("Name") ? "" : notification.getString("Name");
                                    break;
                                case 1:
                                    is_StageCompletedNotificationActivated = notification.isNull("IsUserOpted") ? false : notification.getBoolean("IsUserOpted");
                                    controller.setStageNotifications(is_StageCompletedNotificationActivated);
                                    stageText = notification.isNull("Name") ? "" : notification.getString("Name");
                                    break;
                                case 2:
                                    is_ChangesInConstructionStatus_NotificationActivated = notification.isNull("IsUserOpted") ? false : notification.getBoolean("IsUserOpted");
                                    controller.setProgressNotifications(is_ChangesInConstructionStatus_NotificationActivated);
                                    constructionText = notification.isNull("Name") ? "" : notification.getString("Name");
                                    break;

                            }

                        }
                        runOnUiThread(() -> {
                            if (photosText != null && photosText.length() > 0) {
                                photosTextView.setText(photosText);
                            }

                            if (stageText != null && stageText.length() > 0) {
                                stageTextView.setText(stageText);
                            }

                            if (constructionText != null && constructionText.length() > 0) {
                                constructionTextView.setText(constructionText);
                            }
                            if (userName.length() > 0) {
                                controller.getDashBoardWelcomeText().setText("Welcome " + Utils.getCamelCase(userProfileName));
                            }

                        });


                        updateSettings();
                    } else if (apiCall == updateProfile) {
                        final JSONObject jsonObject = job.getJSONObject(Common.Result_Key);
                        if (profileDialog != null) {
                            profileDialog.cancel();
                        }
                        controller.setUserProfileShown(controller.getUserProfile().getUserDetails().get(0).getEmail().trim(), true);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                controller.getUserProfile().getUserDetails().get(0).updateProfile(jsonObject);
                                controller.updateUserProfile(jsonObject);
                                userName.setText(controller.getUserProfile().getUserDetails().get(0).getFullName());
                                emailId.setText(controller.getUserProfile().getUserDetails().get(0).getEmail());
                                controller.getDashBoardWelcomeText().setText("Welcome " + Utils.getCamelCase(controller.getUserProfile().getUserDetails().get(0).getFullName()));
                            }
                        });
                        Utils.showToast(MyPlaceSettingActivity.this, job.getString(Common.Message), Common.sucessCase);

                    }
                } else {
                    Utils.showToast(MyPlaceSettingActivity.this, message, Common.errorCase);
                }
            } catch (Exception ex) {
                ex.fillInStackTrace();
            }

        } else {

        }
    }

    public String getUpdateSettingsJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserName", controller.getUserProfile().getUserDetailses().get(0).getFullName());
            jsonObject.put("Email", controller.getUserProfile().getUserDetailses().get(0).getEmail());
            jsonObject.put("UserId", controller.getUserProfile().getUserDetailses().get(0).getId());
            JSONArray jsonArray = new JSONArray();
            for (int i = 1; i < 4; i++) {
                JSONObject notication = new JSONObject();
                notication.put("NotificationTypeId", i);
                notication.put("IsActive", true);
                switch (i) {
                    case 1:
                        notication.put("IsUserOpted", is_New_Photo_NotificationActivated);
                        jsonArray.put(0, notication);
                        break;
                    case 2:
                        notication.put("IsUserOpted", is_StageCompletedNotificationActivated);
                        jsonArray.put(1, notication);
                        break;
                    case 3:
                        notication.put("IsUserOpted", is_ChangesInConstructionStatus_NotificationActivated);
                        jsonArray.put(2, notication);
                        break;
                }


            }
            jsonObject.put("NotificationTypes", jsonArray);


        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return jsonObject.toString();
    }

    public void updateSettings() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (is_New_Photo_NotificationActivated == true) {
                    photoNotification.setImageResource(R.drawable.check_fill);
                } else {
                    photoNotification.setImageResource(R.drawable.check_unfill);
                }

                if (is_ChangesInConstructionStatus_NotificationActivated == true) {
                    constructionNotification.setImageResource(R.drawable.check_fill);
                } else {
                    constructionNotification.setImageResource(R.drawable.check_unfill);
                }
                if (is_StageCompletedNotificationActivated == true) {
                    stageNotification.setImageResource(R.drawable.check_fill);
                } else {
                    stageNotification.setImageResource(R.drawable.check_unfill);
                }
                /*if (profilePicString.length() > 0) {

                    Picasso.with(MyPlaceSettingActivity.this).load(profilePicString).memoryPolicy(MemoryPolicy.NO_CACHE).resize(120, 120).centerCrop().placeholder(R.drawable.icon_user_login).into(profilePic);

                }*/
            }
        });
    }

    @Override
    public void onErrorResult(String error) {

    }

    /**
     * this method will be called when we want to display profiledialog
     *
     * @return void.
     */
    public void displayProfileDialog() {
        //try {
        profileDialog = new Dialog(MyPlaceSettingActivity.this);
        profileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileDialog.setCanceledOnTouchOutside(false);
        profileDialog.setContentView(R.layout.set_profile_dialog);
        TextView heading = (TextView) profileDialog.findViewById(R.id.textView);
        heading.setTypeface(controller.getTypeface());
        heading.setText("Edit Profile");
        final CustomEditText firstNameEditText = (CustomEditText) profileDialog.findViewById(R.id.firstNameEditText);
        firstNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        firstNameEditText.setHint("First Name");
        firstNameEditText.setFont(controller.getTypeface());
        firstNameEditText.setImage(R.drawable.icon_user);

        firstNameEditText.setLayoutBackground(R.drawable.grey_border_edittext);
        firstNameEditText.setSelection(firstNameEditText.getText().length());
        final CustomEditText lastNameEditText = (CustomEditText) profileDialog.findViewById(R.id.lastNameEditText);
        lastNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        lastNameEditText.setHint("Last Name");
        lastNameEditText.setFont(controller.getTypeface());
        lastNameEditText.setImage(R.drawable.icon_user);

        lastNameEditText.setLayoutBackground(R.drawable.grey_border_edittext);
        lastNameEditText.setSelection(firstNameEditText.getText().length());
        final CustomEditText phoneNumberEditText = (CustomEditText) profileDialog.findViewById(R.id.phoneNumberEditText);
        phoneNumberEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneNumberEditText.setHint("Mobile Number");
        phoneNumberEditText.setFont(controller.getTypeface());
        phoneNumberEditText.setImage(R.drawable.icon_mobile);
        firstNameEditText.setText(controller.getUserProfile().getUserDetails().get(0).getFirstName());
        lastNameEditText.setText(controller.getUserProfile().getUserDetails().get(0).getLastName());
        phoneNumberEditText.setText(controller.getUserProfile().getUserDetails().get(0).getMobile());
        if (firstNameEditText.getText().length() > 0) {
            firstNameEditText.setSelection(firstNameEditText.getText().length());
        }
        if (phoneNumberEditText.getText().length() > 0) {
            phoneNumberEditText.setSelection(phoneNumberEditText.getText().length());
        }
        if (lastNameEditText.getText().length() > 0) {
            lastNameEditText.setSelection(lastNameEditText.getText().length());
        }

        phoneNumberEditText.setLayoutBackground(R.drawable.grey_border_edittext);

        phoneNumberEditText.customEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Method for validating the input data
                    validatingInputProfileDetails(firstNameEditText, lastNameEditText, phoneNumberEditText);
                }
                return false;
            }
        });

        Button skipProfileButton = (Button) profileDialog.findViewById(R.id.skipProfileButton);
        skipProfileButton.setTypeface(controller.getTypeface());
        skipProfileButton.setText("Cancel");
        Button saveProfileButton = (Button) profileDialog.findViewById(R.id.saveProfileButton);
        saveProfileButton.setTypeface(controller.getTypeface());
        final CheckBox fingerPrintCheckBox = (CheckBox) profileDialog.findViewById(R.id.fingerPrintCheckBox);
        skipProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileDialog.dismiss();
            }
        });

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatingInputProfileDetails(firstNameEditText, lastNameEditText, phoneNumberEditText);
            }
        });
        profileDialog.show();
        /*} catch (Exception ex) {
            ex.fillInStackTrace();
        }*/
    }

    /**
     * this method will be called when we want to validate all edittext fields
     *
     * @return void.
     */
    public void validatingInputProfileDetails(CustomEditText firstNameEditText, CustomEditText lastNameEditText, CustomEditText phoneNumberEditText) {
        //Checking whether network is available or not.
        if (Utils.isNetworkAvailable(MyPlaceSettingActivity.this)) {
            //Validating the first name given by the user.
            if (!controller.getValidation().isNotNull(firstNameEditText)) {
                Utils.showToast(MyPlaceSettingActivity.this, "First name should not be empty", Common.errorCase);
            } else if (!controller.getValidation().isNotNull(lastNameEditText)) //Validating the last name given by the user.
            {
                Utils.showToast(MyPlaceSettingActivity.this, "Last name should not be empty", Common.errorCase);
            } else if (controller.getValidation().validPhoneLength(MyPlaceSettingActivity.this,phoneNumberEditText)) //Validating the mobile number given by the user.
            {
                JSONObject jsonObject = new JSONObject();
                try {
                    fullName = firstNameEditText.getText().toString().trim() + " " + lastNameEditText.getText().toString().trim();
                    jsonObject.put("Email", controller.getUserProfile().getUserDetailses().get(0).getEmail());
                    jsonObject.put("FirstName", firstNameEditText.getText().toString().trim());
                    jsonObject.put("LastName", lastNameEditText.getText().toString().trim());
                    jsonObject.put("FullName", fullName);
                    jsonObject.put("Mobile", phoneNumberEditText.getText().toString().trim());
                    jsonObject.put("Id", controller.getUserProfile().getUserDetails().get(0).getId());
                    jsonObject.put("Region", controller.getUserProfile().getUserDetails().get(0).getRegion());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                apiCall = updateProfile;
                controller.webApiCall().postData(Common.updateUser_Url, jsonObject.toString(), this, Utils.getProgress(MyPlaceSettingActivity.this));
            }
        }
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask() {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(final Bitmap result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    profilePic.setImageBitmap(result);
                    // Stuff that updates the UI

                }
            });
        }
    }

}
