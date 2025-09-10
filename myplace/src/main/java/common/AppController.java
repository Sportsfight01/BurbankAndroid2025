package common;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.dmss.burbankappold.R;
import com.dmss.burbankappold.dashboard.PrefsHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import backend.WebApi;
import models.ContactsModel;
import models.FAQModels;
import models.FinanceDataSet;
import models.MyAppointmentsDataSet;
import models.MyPhotosMonthWiseDataSet;
import models.MyPhotosNewDateWiseDataSet;
import models.MyPlaceCredentials;
import models.SupportAndHelpModel;
import models.UserProfile;
import models.profile.MyPlaceDetail;
import models.profile.UserJobDetail;
import models.profile.UserJobProfile;


public class AppController extends MultiDexApplication {

    public static String BEDROOMS = "";
    public static String REGION = "";
    public static String JOBNUMBER = "";
    public static String SELECTEDJOBNUMBER = "SELECTEDJOBNUMBER";
    public static String PREVIOUSSELECTEDJOBNUMBER = "PREVIOUSSELECTEDJOBNUMBER";
    public static String CHNAGEJOBCLICK ="";
    public static int progres = 0;
    public static String AuthUserName="burbank_minad";
    public static String AuthPassword="401b09eab3c013d4ca54922bb802bec8fd5318192b0a75f201d8b3727429090fb337591abd3e44453b954555b7a0812e1081c39b740293f765eae731f5a65ed1";
    public static Bitmap profiepic;

    public AppController() {
    }

    public static Bitmap getProfiepic() {
        return profiepic;
    }

    public static void setProfiepic(Bitmap profiepic) {
        AppController.profiepic = profiepic;
    }

    public static String LoginEmailID = "";

    public static String getLoginEmailID() {
        return LoginEmailID;
    }

    public static void setLoginEmailID(String loginEmailID) {
        LoginEmailID = loginEmailID;
    }

    public static AppController controller;
    TextView dashBoardWelcomeText;
    String welcomeText;
    Validation validation;
    public static Animation fadeIn, blink, move, flipleftOut, flipRightIn;
    Typeface typeface, typeFaceBold, typefaceRegular, typefaceProximoNova;
    WebApi webApiInstance;
    Location location = null;
    String state = null;
    public boolean isLoggedInFromSocial = false;
    String profileInfo = null;
    public SharedPreferences prfs;
    public SharedPreferences.Editor edit;
    String emailId = "";
    public boolean isLoggedIn = false, emailNotMapped = false;
    int selectedJobPosition = 0;
    // UserProfile profile = null;
    String fingurePrintId = "";
    public static int lastLoggedInLocation = 0;
    public static boolean isSettingsPageCalled = false;
    public static boolean isHnlSettingsPageCalled = false;
    public static boolean isUserProfileShown = false;
    String constructionId = "", officeId = "";
    public static String defaultValue="defaultValue";
    String jobNumber;
    String myPlacedRegion = "";
    String lastLoggedEmailId = "";
    int progressSelectedPosition;
    UserProfile profile = null;
    int displayDialogType = 0;
    //Calendar calendar;
    int selectedDateOfPhotos = 0;
    ArrayList<MyPhotosNewDateWiseDataSet> QldOrSaPhotosList = new ArrayList<MyPhotosNewDateWiseDataSet>();
    MyPlaceCredentials my_Place_Details = null;
    boolean isUserLoggedInWithEmailId = false;
    String profilePicUrl = "";
    String myPlacePassword;
    String myPlaceBaseUrl = "";

    int selectedPhotoPosition, photoScrollToThePosition;
    String selectedPhoto;
    String selectedPhotoNotes, tempPhotoNotes = "";
    boolean cameFromFav = false;
    int selectedFavPhotoUrlInt;
    String loadedProgressResult = "", loadedPhotoResult = "";
    ArrayList<MyPhotosMonthWiseDataSet> myPhotosMonthWiseDataSets = new ArrayList<MyPhotosMonthWiseDataSet>();
    boolean fromNotifications = false;
    MyPhotosNewDateWiseDataSet newDateWiseDataSet = new MyPhotosNewDateWiseDataSet();
    boolean photoNotifications = false, progressNotifications = false, stageNotifications = false;

    public String lastUpdatedTime = "";
    MyAppointmentsDataSet appointmentsDataSet = null;
    ArrayList<MyAppointmentsDataSet> myAppointmentsDataSets = new ArrayList<MyAppointmentsDataSet>();
    ArrayList<FAQModels> faqModelsArrayList = new ArrayList<FAQModels>();
    String region = "";
    ContactsModel contactsModelDetails = null;
    String contactsModelString = null;
    SupportAndHelpModel supportAndHelpModel;
    String contactsJobNumber = "";
    String appointmentJobNumber = "";
    FinanceDataSet financeDataSet;
    public AnalyticsClass analytics;

    boolean showPcInspection = false;
    private static final String PREFS_NAME = "MyplaceSharedpreferenceDP";

    JSONObject maintenanceObject;

    public static int outerArraySize = 0;
    public static int outerSelectedPosition = 0;

    public UserJobProfile userJobProfile;
    public List<UserJobDetail> userJobDetail;
    private List<MyPlaceDetail> myPlaceDetail;
    public static String lastUpdatedDate;

    @Override
    public void onCreate() {
        super.onCreate();
        controller = this;
        initalizeAllElements();
    }


    public boolean isShowPcInspection() {
        return showPcInspection;
    }

    public void setShowPcInspection(boolean showPcInspection) {
        this.showPcInspection = showPcInspection;
    }

    public JSONObject getMaintenanceObject() {
        return maintenanceObject;
    }

    public void setMaintenanceObject(JSONObject maintenanceObject) {
        this.maintenanceObject = maintenanceObject;
    }

    public String getContactsJobNumber() {
        return contactsJobNumber;
    }

    public void setContactsJobNumber(String contactsJobNumber) {
        this.contactsJobNumber = contactsJobNumber;
    }

    public String getAppointmentJobNumber() {
        return appointmentJobNumber;
    }

    public void setAppointmentJobNumber(String appointmentJobNumber) {
        this.appointmentJobNumber = appointmentJobNumber;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public SupportAndHelpModel getSupportAndHelpModel() {
        return supportAndHelpModel;
    }

    public void setSupportAndHelpModel(SupportAndHelpModel supportAndHelpModel) {
        this.supportAndHelpModel = supportAndHelpModel;
    }

    public ArrayList<FAQModels> getFaqModelsArrayList() {
        return faqModelsArrayList;
    }

    public void setFaqModelsArrayList(ArrayList<FAQModels> faqModelsArrayList) {
        this.faqModelsArrayList = faqModelsArrayList;
    }

    public FinanceDataSet getFinanceDataSet() {
        return financeDataSet;
    }

    public void setFinanceDataSet(FinanceDataSet financeDataSet) {
        this.financeDataSet = financeDataSet;
    }

    public ArrayList<MyAppointmentsDataSet> getMyAppointmentsDataSets() {
        return myAppointmentsDataSets;
    }

    public void setMyAppointmentsDataSets(ArrayList<MyAppointmentsDataSet> myAppointmentsDataSets) {
        this.myAppointmentsDataSets = myAppointmentsDataSets;
    }

    public MyAppointmentsDataSet getAppointmentsDataSet() {
        return appointmentsDataSet;
    }

    public void setAppointmentsDataSet(MyAppointmentsDataSet appointmentsDataSet) {
        this.appointmentsDataSet = appointmentsDataSet;
    }

    public ContactsModel getContactsModelDetails() {
        return contactsModelDetails;
    }

    public void setContactsModelDetails(ContactsModel contactsModelDetails) {
        this.contactsModelDetails = contactsModelDetails;
    }

    public String getContactsModelString() {
        return contactsModelString;
    }

    public void setContactsModelString(String contactsModelString) {
        this.contactsModelString = contactsModelString;
    }

    public boolean isPhotoNotifications() {
        return photoNotifications;
    }

    public void setPhotoNotifications(boolean photoNotifications) {
        this.photoNotifications = photoNotifications;
    }

    public boolean isProgressNotifications() {
        return progressNotifications;
    }

    public void setProgressNotifications(boolean progressNotifications) {
        this.progressNotifications = progressNotifications;
    }

    public boolean isStageNotifications() {
        return stageNotifications;
    }

    public void setStageNotifications(boolean stageNotifications) {
        this.stageNotifications = stageNotifications;
    }

    public static Animation getFadeIn() {
        return fadeIn;
    }

    public static void setFadeIn(Animation fadeIn) {
        AppController.fadeIn = fadeIn;
    }

    public void setDashBoardWelcomeText(TextView dashBoardWelcomeText) {
        this.dashBoardWelcomeText = dashBoardWelcomeText;
    }

    public String getMyPlacePassword() {
        return myPlacePassword;
    }

    public void setMyPlacePassword(String myPlacePassword) {
        this.myPlacePassword = myPlacePassword;
    }

    public TextView getDashBoardWelcomeText() {
        return dashBoardWelcomeText;
    }

    public String getWelcomeText() {
        return welcomeText;
    }

    public void setWelcomeText(String welcomeText) {
        this.welcomeText = welcomeText;
    }

    public boolean isFromNotifications() {
        return fromNotifications;
    }

    public void setFromNotifications(boolean fromNotifications) {
        this.fromNotifications = fromNotifications;
    }

    public MyPhotosNewDateWiseDataSet getNewDateWiseDataSet() {
        return newDateWiseDataSet;
    }

    public void setNewDateWiseDataSet(MyPhotosNewDateWiseDataSet newDateWiseDataSet) {
        this.newDateWiseDataSet = newDateWiseDataSet;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized AppController getInstance() {
        if (controller == null)
            controller = new AppController();
        return controller;
    }
    public static boolean setPreference(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getPreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, controller.defaultValue);
    }
    public void initalizeAllElements() {
        LoginEmailID = "";

        outerArraySize = 0;
        outerSelectedPosition = 0;


        // calendar = Calendar.getInstance();
        validation = new Validation(getContext());
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        move = AnimationUtils.loadAnimation(this, R.anim.moving);

        typeface = Typeface.createFromAsset(getContext().getAssets(), "appfont.otf");
        typeFaceBold = Typeface.createFromAsset(getContext().getAssets(), "MyriadPro-Bold.otf");
        typefaceRegular = Typeface.createFromAsset(getContext().getAssets(), "MyriadPro-Regular.otf");
        typefaceProximoNova = Typeface.createFromAsset(getContext().getAssets(), "Proxima_Nova_Extrabold.otf");
        webApiInstance = new WebApi(getContext());
        prfs = PreferenceManager.getDefaultSharedPreferences(getContext());
        edit = prfs.edit();

        selectedJobPosition = prfs.getInt("selectedJobPosition", 0);
        lastUpdatedTime = prfs.getString("lastUpdatedTime", "");
        profileInfo = prfs.getString("profileInfo", "");
        emailId = prfs.getString("emailId", "");
        isLoggedIn = prfs.getBoolean("userlogedIn", false);
        fingurePrintId = prfs.getString("fingerPrintId", "");
        lastLoggedEmailId = prfs.getString("lastLogedEmailId", "");
        makeFolder(String.valueOf(Environment.getExternalStorageDirectory()), "/Burbank");
        Common.sdCardPath = Environment.getExternalStorageDirectory() + "/Burbank";
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (profileInfo.length() > 0) {
            profile = new UserProfile(profileInfo);
        }
        analytics = new AnalyticsClass(getContext());
//
        initializeBlinkAnimation();
        //permissions=new SelfCheckPermissions(this);
    }

    public AnalyticsClass getAnalytics() {
        return analytics;
    }

    public Typeface getTypefaceProximoNova() {
        return typefaceProximoNova;
    }

    public void setTypefaceProximoNova(Typeface typefaceProximoNova) {
        this.typefaceProximoNova = typefaceProximoNova;
    }

    public static void makeFolder(String path, String folder) {
        File directory = new File(path, folder);
        if (directory.exists() == false) {
            directory.mkdirs();
        }

    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
        edit.putString("lastUpdatedTime", lastUpdatedTime);
        edit.commit();
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setProfilePicUrl(String url) {
        profilePicUrl = url;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public void setUserLoggedInWithEmailId(boolean userLoggedInWithEmailId) {
        isUserLoggedInWithEmailId = userLoggedInWithEmailId;
    }

    public boolean isUserLoggedInWithEmailId() {
        return isUserLoggedInWithEmailId;
    }

    public String getConstructionId() {
        return constructionId;
    }

    public void setConstructionId(String constructionId) {
        this.constructionId = constructionId;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public int getDisplayDialogType() {
        return displayDialogType;
    }

    public void setDisplayDialogType(int displayDialogType) {
        this.displayDialogType = displayDialogType;
    }

    /*public Calendar getCalendar() {
        return calendar;
    }*/

    public int getSelectedDateOfPhotos() {
        return selectedDateOfPhotos;
    }

    public void setSelectedDateOfPhotos(int selectedDateOfPhotos) {
        this.selectedDateOfPhotos = selectedDateOfPhotos;
    }

    public int getPhotoScrollToThePosition() {
        return photoScrollToThePosition;
    }

    public void setPhotoScrollToThePosition(int photoScrollToThePosition) {
        this.photoScrollToThePosition = photoScrollToThePosition;
    }

    public ArrayList<MyPhotosNewDateWiseDataSet> getQldOrSaPhotosList() {
        return QldOrSaPhotosList;
    }

    public void setQldOrSaPhotosList(ArrayList<MyPhotosNewDateWiseDataSet> qldOrSaPhotosList) {
        QldOrSaPhotosList = qldOrSaPhotosList;
    }

    public AppController getController() {
        return controller;
    }

    public void setController(AppController controller) {
        this.controller = controller;
    }


    public void setLoggedInEmailId(String emailId) {
        lastLoggedEmailId = emailId;
        edit.putString("lastLogedEmailId", emailId);
        edit.commit();
    }

    public String getLastLoggedEmailId() {
        return lastLoggedEmailId;
    }

    /*public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }*/

    public int getProgressSelectedPosition() {
        return progressSelectedPosition;
    }

    public void setProgressSelectedPosition(int progressSelectedPosition) {
        this.progressSelectedPosition = progressSelectedPosition;
    }

    public Animation getFlipleftOut() {
        return flipleftOut;
    }

    public Animation getFlipRightIn() {
        return flipRightIn;
    }


    public void initializeBlinkAnimation() {
        blink = new AlphaAnimation(0.1f, 1.0f);
        blink.setDuration(500); //You can manage the blinking time with this parameter
        blink.setStartOffset(20);
        blink.setRepeatMode(Animation.REVERSE);
        blink.setRepeatCount(Animation.INFINITE);
    }

    public Boolean isUserProfileShown(String key) {

        return prfs.getBoolean(key, false);
    }

    public void setUserProfileShown(String key, Boolean value) {
        edit.putBoolean(key, value);
        edit.commit();
    }

    public Boolean isSalesConsultantShown(String key) {
        return prfs.getBoolean(key, false);
    }

    public void setSalesConsultantShown(String key, Boolean value) {
        edit.putBoolean(key, value);
        edit.commit();
    }

    public Boolean getFirstTimeLoginDone(String key) {
        return prfs.getBoolean(key, false);
    }

    public void setFirstTimeLogin(String key, Boolean value) {
        edit.putBoolean(key, value);
        edit.commit();
    }

    public Validation getValidation() {
        return validation;
    }

    public Context getContext() {
        return getApplicationContext();
    }


    public void setLocation(Location location) {
        this.location = location;
    }

    public void setLastLoggedInLocation(int lastLoggedInLocation) {

        this.lastLoggedInLocation = lastLoggedInLocation;
        edit.putInt("lastLoggedInLocation", lastLoggedInLocation);
        edit.commit();

    }

    public int getLastLoggedInLocation() {
        return lastLoggedInLocation;
    }

    public Location getLocation() {
        return location;
    }

//    public void setProfileInfo(String profileInfo) {
//        if (!this.profileInfo.equalsIgnoreCase(profileInfo)) {
//            this.profileInfo = profileInfo;
//            profile = new UserProfile(profileInfo);
//        }
//        edit.putString("profileInfo", profileInfo);
//        edit.commit();
//    }


    public String getFingerPrintID() {
        return fingurePrintId;
    }


//    public UserProfile getProfileInfo() {
//        if ((profileInfo.length() > 0) && (profile == null)) {
//            profile = new UserProfile(profileInfo);
//        }
//        return profile;
//    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
        edit.putString("emailId", emailId);
        edit.commit();
    }

    public String getEmailId() {
        return emailId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setLoggedInFromSocial(boolean val) {
        this.isLoggedInFromSocial = val;
    }

    public boolean getLoggedInFromSocial() {
        return isLoggedInFromSocial;
    }


    public void setUserLoggedIn(boolean val) {
        this.isLoggedIn = val;
        edit.putBoolean("userlogedIn", val);
        edit.commit();
    }

    public boolean isUserLoggedIn() {
        return isLoggedIn;
    }

    /*****************
     * Animations Instances
     **********************************/
    public Animation getFadeInAnimationInstance() {
        // return animation instance
        return fadeIn;
    }

    public Animation getBlinkAnimation() {

        return blink;
    }

    public Animation getMoveAnimation() {

        return move;
    }

    /*****************
     * TypeFace Instances
     **********************************/
    public Typeface getTypeface() {

        return typeface;
    }

    /*****************
     * TypeFace Instances
     **********************************/
    public Typeface getTypefaceBold() {

        return typeFaceBold;
    }

    public Typeface getTypefaceRegular() {

        return typefaceRegular;
    }

    public void setProfileInfo(String profileInfo) {
        edit.putString("profileInfo", profileInfo);
        Log.d("UpdatedProfile", profileInfo);
        edit.commit();
        profile = new UserProfile(prfs.getString("profileInfo", ""));
    }

    public UserProfile getUserProfile() {
        profile = new UserProfile(prfs.getString("profileInfo", ""));
        return profile;
    }

    public void setMy_Place_Details(MyPlaceCredentials my_Place_Details) {
        this.my_Place_Details = my_Place_Details;
    }

    public MyPlaceCredentials getMy_Place_Details() {
        return my_Place_Details;
    }

    /*****************
     * WebApi Instance Instances
     **********************************/

    public WebApi webApiCall() {
        return webApiInstance;
    }

    public boolean isEmailNotMapped() {
        return emailNotMapped;
    }

    public void setEmailNotMapped(boolean emailNotMapped) {
        this.emailNotMapped = emailNotMapped;
    }

    public void updateUserProfile(JSONObject jobb) {
        try {
            JSONObject job = new JSONObject(profileInfo);
            JSONArray jarray = job.getJSONArray("UserDetails");
            JSONObject userProfile = jarray.getJSONObject(0);
            userProfile.put("FirstName", jobb.isNull("FirstName") ? "" : jobb.getString("FirstName"));
            userProfile.put("MiddleName", jobb.isNull("MiddleName") ? "" : jobb.getString("MiddleName"));
            userProfile.put("LastName", jobb.isNull("LastName") ? "" : jobb.getString("LastName"));
            userProfile.put("Email", jobb.isNull("Email") ? "" : jobb.getString("Email"));
            userProfile.put("Mobile", jobb.isNull("Mobile") ? "" : jobb.getString("Mobile"));
            userProfile.put("FullName", jobb.isNull("FullName") ? "" : jobb.getString("FullName"));
            setProfileInfo(job.toString());
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
    }

   /* public String getMyPlaceBaseUrl() {
        return myPlaceBaseUrl;
    }

    public void setMyPlaceBaseUrl(String myPlaceBaseUrl) {
        this.myPlaceBaseUrl = myPlaceBaseUrl;
    }*/


    /*public void setNotesForPhoto(String key, String notes) {
        key = controller.getMy_Place_Details().getUsername() + "_" + controller.getMy_Place_Details().getJobNumber() + "_" + key;
        edit.putString(key, notes);
        edit.commit();
    }

    public String getPhotoNotes(String key) {
        key = controller.getMy_Place_Details().getUsername() + "_" + controller.getMy_Place_Details().getJobNumber() + "_" + key;
        String notes = prfs.getString(key, "");
        return notes;
    }


    public void setFavForPhoto(String key, boolean fav) {
        key = controller.getMy_Place_Details().getUsername() + "_" + controller.getMy_Place_Details().getJobNumber() + "_" + key;
        edit.putBoolean(key, fav);
        edit.commit();
    }

    public boolean getPhotoFav(String key) {
        key = controller.getMy_Place_Details().getUsername() + "_" + controller.getMy_Place_Details().getJobNumber() + "_" + key;
        boolean fav = prfs.getBoolean(key, false);
        return fav;
    }*/

    public int getSelectedPhotoPosition() {
        return selectedPhotoPosition;
    }

    public void setSelectedPhotoPosition(int selectedPhotoPosition) {
        this.selectedPhotoPosition = selectedPhotoPosition;
    }

    public String getSelectedPhoto() {
        return selectedPhoto;
    }

    public void setSelectedPhoto(String selectedPhoto) {
        this.selectedPhoto = selectedPhoto;
    }

    public String getSelectedPhotoNotes() {
        return selectedPhotoNotes;
    }

    public void setSelectedPhotoNotes(String selectedPhotoNotes) {
        this.selectedPhotoNotes = selectedPhotoNotes;
    }

    public String getTempPhotoNotes() {
        return tempPhotoNotes;
    }

    public void setTempPhotoNotes(String tempPhotoNotes) {
        this.tempPhotoNotes = tempPhotoNotes;
    }

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    public boolean isCameFromFav() {
        return cameFromFav;
    }

    public void setCameFromFav(boolean cameFromFav) {
        this.cameFromFav = cameFromFav;
    }

    public int getSelectedFavPhotoUrlInt() {
        return selectedFavPhotoUrlInt;
    }

    public void setSelectedFavPhotoUrlInt(int selectedFavPhotoUrlInt) {
        this.selectedFavPhotoUrlInt = selectedFavPhotoUrlInt;
    }


    public String getLoadedProgressResult() {
        return loadedProgressResult;
    }

    public void setLoadedProgressResult(String loadedProgressResult) {
        this.loadedProgressResult = loadedProgressResult;
    }

    public String getLoadedPhotoResult() {
        return loadedPhotoResult;
    }

    public void setLoadedPhotoResult(String loadedPhotoResult) {
        this.loadedPhotoResult = loadedPhotoResult;
    }

    public int getSelectedJobPosition() {
        selectedJobPosition = prfs.getInt("selectedJobPosition", 0);
        return selectedJobPosition;
    }

    public void setSelectedJobPosition(int selectedJobPosition) {
        edit.putInt("selectedJobPosition", selectedJobPosition);
        edit.commit();
    }

    public ArrayList<MyPhotosMonthWiseDataSet> getMyPhotosMonthWiseDataSets() {
        return myPhotosMonthWiseDataSets;
    }

    public void setMyPhotosMonthWiseDataSets(ArrayList<MyPhotosMonthWiseDataSet> myPhotosMonthWiseDataSets) {
        this.myPhotosMonthWiseDataSets = myPhotosMonthWiseDataSets;
    }

    public UserJobProfile getUserJobProfile() {
        return userJobProfile;
    }

    public void setUserJobProfile(UserJobProfile userJobProfile) {
        this.userJobProfile = userJobProfile;
    }

    public List<UserJobDetail> getUserJobDetail() {
        return userJobDetail;
    }

    public void setUserJobDetail(List<UserJobDetail> userJobDetail) {
        this.userJobDetail = userJobDetail;
    }

    public List<MyPlaceDetail> getMyPlaceDetail() {
        return myPlaceDetail;
    }

    public void setMyPlaceDetail(List<MyPlaceDetail> myPlaceDetail) {
        this.myPlaceDetail = myPlaceDetail;
    }

}
