package common;

import android.app.Activity;
import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsClass {


    private final FirebaseAnalytics firebaseAnalytics;
    Context context;

    public AnalyticsClass(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        this.context = context;
    }

    public void setScreen(Activity activity, String screenName){
        firebaseAnalytics.setCurrentScreen(activity,screenName,null);
    }
    
    public void landingScreenLoadingEvent(){
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.landingScreenLoading,null);
    }

    public void landingScreenNextButtonTouchEvent() {
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.landingScreenNextButtonTouch,null);
    }

    public void  loginScreenLoadingEvent() {
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.loginScreenLoading,null);
    }

    public void  loginScreenLoginButtonTouchEvent() {
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.loginScreenLoginButtonTouch,null);
    }

    public void  loginScreenUseTouchIdTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.loginScreenUseTouchIdTouch,null);
    }

    public void  loginScreenBackButtonTouchEvent() {
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.loginScreenBackButtonTouch,null);
    }

    public void  loginScreenForgotPasswordButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.loginScreenForgotPasswordButtonTouch,null);
    }

    public void notificationScreenLoadingEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.notificationScreenLoading,null);
    }

    public void notificationScreenBellIconTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.notificationScreenBellIconTouch,null);
    }

    public void notificationsScreenNotificationsTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.notificationsScreenNotificationsTouch,null);
    }

    public void dashboardLoadingEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardLoading,null);
    }

    public void dashboardInvitationButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardInvitationButtonTouch,null);
    }

    public void dashboardDropdownJobArrowTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardDropdownJobArrowTouch,null);
    }

    public void dashboardPlusButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardPlusButtonTouch,null);
    }

    public void dashboardProgressIconButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardProgressIconButtonTouch,null);
    }

    public void dashboardPhotosIconButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardPhotosIconButtonTouch,null);
    }

    public void dashboardDetailsIconButtonTouchEvent() {
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardDetailsIconButtonTouch,null);
    }

    public void dashboardFinanceIconButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardFinanceIconButtonTouch,null);
    }

    public void dashboardSupportHelpButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardSupportHelpButtonTouch,null);
    }

    public void dashboardMoreButtonTouchEvent() {
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardMoreButtonTouch,null);
    }

    public void dashboardMyAppointmentsIconTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardMyAppointmentsIconTouch,null);
    }

    public void dashboardHistoryLogIconTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardHistoryLogIconTouch,null);
    }

    public void dashboardContactsIconTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardContactsIconTouch,null);
    }

    public void dashboardContactsEmailIconTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardContactsEmailIconTouch,null);
    }

    public void dashboardContactsCallIconTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardContactsCallIconTouch,null);
    }

    public void myProgressAdministrationStageBreakDownButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.myProgressAdministrationStageBreakDownButtonTouch,null);
    }

    public void myProgressFrameStageBreakDownButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.myProgressFrameStageBreakDownButtonTouch,null);
    }

    public void myProgressLockupStageBreakDownButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.myProgressLockupStageBreakDownButtonTouch,null);
    }

    public void myProgressFixoutStageBreakDownButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.myProgressFixoutStageBreakDownButtonTouch,null);
    }

    public void myProgressFinishingStageBreakDownButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.myProgressFinishingStageBreakDownButtonTouch,null);
    }

    public void photoTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.photoTouch,null);
    }

    public void photosFavSymbolTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.photosFavSymbolTouch,null);
    }

    public void photosAddButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.photosAddButtonTouch,null);
    }

    public void photosBackButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.photosBackButtonTouch,null);
    }

    public void dashboardDetailsIconTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardDetailsIconTouch,null);
    }

    public void dashboardFinanceIconTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardFinanceIconTouch,null);
    }

    public void dashboardSupportHelpEmailButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardSupportHelpEmailButtonTouch,null);
    }

    public void dashboardSupportHelpCallButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardSupportHelpCallButtonTouch,null);
    }

    public void dashboardSupportHelpCloseIconTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardSupportHelpCloseIconTouch,null);
    }

    public void dashboardMoreFeaturesButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardMoreFeaturesButtonTouch,null);
    }

    public void dashboardFavPhotosButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardFavPhotosButtonTouch,null);
    }

    public void dashboardShareWithPartnersButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardShareWithPartnersButtonTouch,null);
    }

    public void dashboardFAQButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardFAQButtonTouch,null);
    }

    public void dashboardBackButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.dashboardBackButtonTouch,null);
    }

    public void favPhotoTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.favPhotoTouch,null);
    }

    public void favPhotosFavSymbolTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.favPhotosFavSymbolTouch,null);
    }

    public void favPhotosAddButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.favPhotosAddButtonTouch,null);
    }

    public void favPhotosBackButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.favPhotosBackButtonTouch,null);
    }

    public void documentsIconTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.documentsIconTouch,null);
    }

    public void documentsViewedEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.documentsViewed,null);
    }

    public void moreFeaturesShareWithPartnerInviteButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.moreFeaturesShareWithPartnerInviteButtonTouch,null);
    }

    public void moreFeaturesShareWithPartnerReferButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.moreFeaturesShareWithPartnerReferButtonTouch,null);
    }

    public void moreFeaturesShareWithPartnerDeleteButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.moreFeaturesShareWithPartnerDeleteButtonTouch,null);
    }

    public void settingsSaveButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.settingsSaveButtonTouch,null);
    }

    public void settingsEditIconTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.settingsEditIconTouch,null);
    }

    public void settingsResetPasswordTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.settingsResetPasswordTouch,null);
    }

    public void settingsPhotosAddedOnOffIconTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.settingsPhotosAddedOnOffIconTouch,null);
    }

    public void settingsStageCompletionAddedOnOffIconTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.settingsStageCompletionAddedOnOffIconTouch,null);
    }

    public void settingsStagesChangesOnOffIconTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.settingsStagesChangesOnOffIconTouch,null);
    }

    public void settingsWhatIsBurbankAppForwardArrowTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.settingsWhatIsBurbankAppForwardArrowTouch,null);
    }

    public void settingsTermsOfUseForwardArrowTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.settingsTermsOfUseForwardArrowTouch,null);
    }

    public void settingsPrivacyPolicyForwardArrowTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.settingsPrivacyPolicyForwardArrowTouch,null);
    }

    public void settingsLogoutButtonTouchEvent() {
        
        firebaseAnalytics.logEvent(AnalyticsEventsDeclarations.settingsLogoutButtonTouch,null);
    }
}
