package common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dmss.burbankappold.R;
import com.dmss.burbankappold.ResetPassword;
import com.dmss.burbankappold.dashboard.HomeActivity;

public class Common {
    public static String emailId;
//    public static final String LOCAL_BASE_URL = "http://10.6.45.14:8085/";
//    public static final String INFO_IMAGE_BASE_URL = "http://10.6.45.14:8085/getattachment/";
    public static final String LOCAL_BASE_URL = "https://www.burbank.com.au/";
    public static final String INFO_IMAGE_BASE_URL = "https://www.burbank.com.au/getattachment/";
    public static final String BASEURL_api_v2="https://www.burbank.com.au/api-v2/api/";
    public static int errorCase = 1;
    public static int sucessCase = 2;
    public static int internet_Error = 3;
    //public static String BaseUrl = "http://test.burbank.com.au/CentralLoginSystem/api/";
    //public static String BaseUrl = "https://www.burbank.com.au/CentralLoginSystem/api/";
    //http://test.burbank.com.au/CentralLogin.WebApi/api
    //public static String myPlaceBaseUrl = "http://test.burbank.com.au/myplace-new/api/";
    //public static String myPlaceBaseUrlVic = "https://www.burbank.com.au/victoria/myplace/api/";
    public static String myPlaceBaseUrlVic = "https://www.burbank.com.au/myplace/api/";
    //public static String myPlaceLocalBaseUrlVic = "http://192.168.100.92:8989/api/";

    public static String myPlaceBaseUrlQldOrSa = "https://www.burbank.com.au/myplace/api/";
    //public static String myPlaceBaseUrlSa = "https://www.burbank.com.au/south-australia/myplace/api/";

//    public static String myPlaceBaseUrlQldOrSa = "http://10.6.45.14:8085/myplace/api/";


    //public static String BaseUrl = "https://www.burbank.com.au/CentralLoginSystem/api/";
    public static String BaseUrl = "https://www.burbank.com.au/CentralLogin.WebApi/api/";
    public static String ClickHomeBaseUrl = "https://nationalclickhome.burbankgroup.com.au/clickhome3webservice/ClickHome.myhome/v2/";
    public static String ClickHomeLoginBaseUrl = "https://clickhomedev.burbankgroup.com.au/clickhome3webservice_DEV/MyHome/V3/";
    public static String ClickHomeBaseUrlLive = "https://nationalclickhome.burbankgroup.com.au/clickhome3webservice/myhome/v3/";
    public static String ClickHomeV2BaseUrlLive = "https://nationalclickhome.burbankgroup.com.au/clickhome3webservice/V2/";
    public static String ClickHomeBaseV3UrlLive = "https://nationalclickhome.burbankgroup.com.au/clickhome3webservice/MyHome/V3/";

    public static String newMyPlaceClientLogin =        ClickHomeBaseUrl+"clientlogin";
    public static String newMyPlaceProgress =           ClickHomeBaseUrl+"jobsteps/";
    public static String newMyPlaceProgressDetails =    ClickHomeBaseUrl+"job";
    public static String newMyPlacePhotosOrDocuments =  ClickHomeBaseUrl+"documents";
    public static String newMyPlaceDownload =           ClickHomeBaseUrl+"clientdoc/57822";
    public static String newMyPlaceContactUs =          ClickHomeBaseUrl+"notes";
    public static String newMyPlaceAppointment =        ClickHomeBaseUrl+"jobsteps";

    public static String IsUserPresent_JobNumberUrl = BaseUrl + "login/getuserByJob";
    public static String IsUserPresent_Url = BaseUrl + "login/getUserDetails";
    public static String setPassWord_Url = BaseUrl + "login/VerifyAddNewRegisteredUser";
    public static String verifyEmailSetPassword_Url = BaseUrl + "login/VerifyPasscodeAddMyPlaceUser";
    public static String verifyEmailAddUser_Url = BaseUrl + "login/VerifyAddNewRegisteredUser";
    public static String setResendOtp_Url = BaseUrl + "login/ResendPasscode";
    public static String validateMyPlacePassword_Url = BaseUrl + "login/ValidateMyPlaceDetails";
    public static String centralUserLogin_Url = BaseUrl + "login/AuthenticateCentralUserLogin";
    public static String forgetPasswordUrl=BaseUrl + "login/ForgotPassword";
    public static String updatePasswordUrl=BaseUrl + "login/UpdatePassword";
    public static String validateEmailPassword_Url = BaseUrl + "login/ValidateEmailSendPassCode";
    public static String updateUser_Url = BaseUrl + "login/UpdateCentralLoginUser";
    public static String resetUserPassword_Url = BaseUrl + "login/ResetPassword";
    public static String addJob_Url = BaseUrl + "login/AddJob";
    public static String getCoBurbankUrl = BaseUrl + "coburbank/GetCoBurbanks";
    public static String addCoBurbankUrl = BaseUrl + "coburbank/AddCoBurbank";
    public static String sendInviteUrl = BaseUrl + "coburbank/SendInvitation";
    public static String updateProfilePicUrl = BaseUrl + "userProfile/UpdateProfilePic";

    public static String getJobNotificationUrl = BaseUrl + "coburbank/GetCoBurbankNotifications";
    public static String getAcceptInvitationUrl = BaseUrl + "coburbank/AcceptInvitation";
    public static String getRejectInvitationUrl = BaseUrl + "coburbank/RejectInvitation";
    public static String getDeleteCoBurbankUrl = BaseUrl + "coburbank/Delete";
    public static String maintenanceUrl = BaseUrl+ "config/getall";
    public static String faq_Url = "https://www.burbank.com.au/myplace/AngularScripts/FAQ/Services/faq.json";
    public static String GetAppVersion = "myplace/GetAppVersion";

    /*public static String loginUrl = BaseUrl + "Auth/login";
    public static String socialLoginUrl = BaseUrl + "login/RegisterLoginSocial";
    public static String forgotPasswordUrl = BaseUrl + "login/forgotpassword";
    public static String UpdateNewProfile = "login/updatedetails";
    public static String UpdatePassword_OTP = BaseUrl + "login/updatepassword";
    public static String ResetPassword = "login/resetpwd";
    public static String NewHomesList = "home/newhomelist/";
    public static String ByGroupNameHomesList = "home/newhomegroups/";
    public static String NewHomeDetailList = BaseUrl + "home/newhomedetail/";
    public static String DashBoardEnquiry = BaseUrl + "home/sendhomeenquiry";
    public static String GetInputValues = BaseUrl + "/home/getinputvalues/";
    public static String homeLandDetail = BaseUrl + "/home/homelanddetail/";
    public static String resendOtp = BaseUrl + "login/ResendOTP";
    public static String validateEmailId = BaseUrl + "login/ValidateUserEmail";
    public static String ByGroupNameHomesListOfParticularUser = BaseUrl + "home/newhomeslist";
    public static String HnLList = BaseUrl + "home/homelandlist";
    public static String HnlDetailViewUrl = BaseUrl + "home/homelanddetail";
    public static String favSelectionUrl = BaseUrl + "home/favourite";
    public static String NewHomeList = BaseUrl + "home/newhomeslist";

    public static String DisplayHomeFiltersUrl = BaseUrl + "home/getAllDisplayLocationFilters/";
    public static String DisplayHomeByRegionsUrl = BaseUrl + "home/getAllDisplayLocations/";
    public static String DisplayHomeByIdUrl = BaseUrl + "home/getDisplayLocationById/";
    public static String DisplayHomeByForSaleByRegionsUrl = BaseUrl + "home/getAllDisplayHomesforSale/";
    public static String DisplayHomeByForSaleByIdUrl = BaseUrl + "home/getDisplayHomeforSaleById/";

    public static String MyPlaceUserCheckUrl = "http://test.burbank.com.au/myplace-new/api/login/CheckUserlogin/";
    public static String MyPlaceGetUserDetailsUrl = "http://test.burbank.com.au/myplace-new/api/login/getLoggedinUser/";
    public static String MyPlaceContractUrl = "http://test.burbank.com.au/myplace-new/api/contract/GetContract?";
    public static String MyPlaceGetAllPhotos = "http://test.burbank.com.au/myplace-new/api/photos/GetAllPhotosByConstructionId/";


    public static String myPlaceLogin = "http://test.burbank.com.au/myplace-new/api/login/CheckUserlogin/";
    public static String myPlaceGetProfileDetails = "http://test.burbank.com.au/myplace-new/api/login/getLoggedinUser/";
    public static String myPlaceGetAdministration = "http://test.burbank.com.au/myplace-new/api/progress/GetAdminProgress?";
    public static String myPlaceGetAllPhotos = "http://test.burbank.com.au/myplace-new/api/photos/GetAllPhotos?";
    public static String myPlaceGetFinance = "http://test.burbank.com.au/myplace-new/api/finance/GetFinance?financialTicketId=";
    public static String myPlaceContractDetails = "http://test.burbank.com.au/myplace-new/api/contract/GetContract?";
    public static String myPlaceDocuments = "http://test.burbank.com.au/myplace-new/api/documents/GetAllDocuments?";
    public static String getMyPlaceNotificationSetting = "http://test.burbank.com.au/CentralLoginSystem/api/userProfile/getUserProfile";
    public static String updateMyPlaceNotificationSetting = " http://test.burbank.com.au/CentralLoginSystem/api/userProfile/UpdateUserProfile";


    public static String myPlaceContractDetails = "contract/GetContract?";
    public static String myPlaceGetAllPhotos = "photos/GetAllPhotos?";
    public static String myPlaceLogin = "login/CheckUserlogin/";

    */

    public static String MyPlaceUserCheckUrl = "login/CheckUserlogin";
    public static String MyPlaceGetUserDetailsUrl = "login/getLoggedinUser";
    public static String MyPlaceContractUrl = "contract/GetContract?";
    public static String MyPlaceGetAllPhotos = "photos/GetAllPhotosByConstructionId/";
    public static String MyPlaceGetContactsDetails = "survey/GetClientInfoForContract?jobNumber=";
    public static String MyPlaceGetSupportDetails = "contact/GetContactDetails?jobNumber=";
    public static String MyPlaceGetContactDetails = "survey/GetClientInfoForContractNumber?jobNumber=";

    public static String please_enter_password = "Please enter password";


    public static String myPlaceGetProfileDetails = "login/getLoggedinUser/";
    public static String myPlaceGetAdministration = "progress/GetAdminProgress?";
    public static String myPlaceGetFinance = "finance/GetFinanceDetails?financialTicketId=";
    public static String myPlaceDocuments = "documents/GetAllDocuments?";
    public static String getMyPlaceNotificationSetting = BaseUrl + "userProfile/getUserProfile";
    public static String updateMyPlaceNotificationSetting = BaseUrl + "userProfile/UpdateUserProfile";




    public static String Password_Reset_Key = "isPasswordReset";
    public static String Status_Key = "Status";
    public static String Sucess_Key = "Success";
    public static String NewUser_Key = "IsNewUser";
    public static String MinPrice = "MinPrice";
    public static String MaxPrice = "MaxPrice";
    public static String FigurePrintID = "FigurePrintID";
    public static String Result_Key = "Result";
    public static String Message = "Message";
    public static String SucessMssage = "SucessMssage";

    public static String Id = "Id";
    public static String FirstName = "FirstName";
    public static String MiddleName = "MiddleName";
    public static String LastName = "LastName";
    public static String Name = "Name";
    public static String FullName = "FullName";
    public static String Image = "Image";
    public static String Email = "Email";
    public static String CountryCode = "CountryCode";
    public static String Mobile = "Mobile";
    public static String Password = "Password";
    public static String Region = "Region";
    public static String OTP = "OTP";
    public static String StartDate = "StartDate";
    public static String EndDate = "EndDate";
    public static String UserGuid = "UserGuid";
    public static String isPasswordReset = "isPasswordReset";
    public static String isEnquirySent = "isEnquirySent";

    public static String isSalesConsultantAsign = "isSalesConsultantAsign";
    public static String SalesPerson = "SalesPerson";
    public static String isActive = "isActive";
    public static String CreatedOn = "CreatedOn";
    public static String UpdatedOn = "UpdatedOn";
    public static String storey = "Storey";
    public static boolean isPwdReset = false;
    public static String[] regions = {"Victoria (VIC)", "Queensland(QLD)", "South Australia(SA)", "NSW/ACT"};
    public static String[] regionsCode = {"VIC", "QLD", "SA", "NSW/ACT"};
    public static String[] region = {"Victoria", "Queensland", "South Australia", "New South Wales /\nAustralian Capital Territory"};

    public static String Single_Storey_Key = "SingleStorey";
    public static String Double_Storey_Key = "DoubleStorey";

    public static String Single_Bed__Key = "SingleBed";
    public static String Double_Bed__Key = "DoubleBed";
    public static String Triple_Bed__Key = "TripleBed";
    public static String Four_Bed__Key = "FourBed";
    public static String FourPlus_Bed__Key = "FourPlusBed";
    public static String FivePlus_Bed__Key = "FivePlusBeds";

    public static String Single_Bath_Key = "SingleBath";
    public static String Double_Bath_Key = "DoubleBath";
    public static String Triple_Bath_Key = "TripleBath";

    public static String First_Collection_Key = "FirstCollection";
    public static String Second_Collection_Key = "SecondCollection";

    public static String BlockWidth_Min_Key = "Min_Width";
    public static String BlockWidth_Max_Key = "Max_Width";

    public static String Min_Price_Key = "MinPrice";
    public static String Max_Price_Key = "MaxPrice";

    public static String BlockWidth_Min_Position = "Min_Position";
    public static String BlockWidth_Max_Position = "Max_Position";

    public static String Min_Price_Position = "MinPricePosition";
    public static String Max_Price_Position = "MaxPricePosition";
    public static int HouseDetails = 2;
    public static int DashBoard = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static Uri imageUri = null;
    public static String sdCardPath;
    public static String folderName;
    public static String tempPath;
    public static int selectedPhotoPosition;
public static String somethingErrorMessage="Something went wrong,Please try later.";
    public static String FAQ = "{\n" +
            "   \"Region\":\"VIC\",\n" +
            "   \"FAQs\":[\n" +
            "      {\n" +
            "         \"Id\":1,\n" +
            "         \"Question\":\"WHEN WILL I RECEIVE THE PAYMENT?\",\n" +
            "         \"Answer\":\"Payment is made to your lender as part of the first progress payment of your Building Contract.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":2,\n" +
            "         \"Question\":\"WHAT IS A CONTRACT OF SALE?\",\n" +
            "         \"Answer\":\" A document prepared by an estate agent or solicitor outlining particulars of the sale of a property. It clarifies details such as price, settlement, finance and any special conditions.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":3,\n" +
            "         \"Question\":\"WHAT IS A VENDOR STATEMENT?\",\n" +
            "         \"Answer\":\"Also known as a Section 32, this is attached to the Contract of Sale and provides the buyer with information about the sale of the property. As it is important to check this document thoroughly, it is a good idea to have your solicitor peruse it before an offer to a Vendor is signed.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":4,\n" +
            "         \"Question\":\" WHAT IS A BUILDING CONTRACT?\",\n" +
            "         \"Answer\":\"A document between you and your builder which includes details of start and completion dates, new home specifications, contract price and progress payments.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":5,\n" +
            "         \"Question\":\"WHAT DOES COOLING-OFF MEAN?\",\n" +
            "         \"Answer\":\"A “Cooling-Off period” occurs within 3 business days from the date of contract signing allowing the potential buyer to withdraw from the deal. A cooling-off period of 5 business days applies to building contracts over $5000.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":6,\n" +
            "         \"Question\":\"WHAT IS VCAT?\",\n" +
            "         \"Answer\":\"The Victorian Civil and Administrative Tribunal (VCAT) is a low cost forum facilitating dispute hearings regarding consumer matters, including domestic building works, where both parties are unable to reach agreement.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":7,\n" +
            "         \"Question\":\"WHAT IS STAMP DUTY?\",\n" +
            "         \"Answer\":\"A State Government tax imposed on the sale of real estate (includes both established homes and land purchases) and determined by the sale value. Your solicitor or lender can calculate the stamp duty payable when buying a property.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":8,\n" +
            "         \"Question\":\" WHAT ARE COVENANTS?\",\n" +
            "         \"Answer\":\"Restrictions placed on your land which set the requirements for the size of your new home, including the style or type of materials used. These will be outlined in the Section 32 Vendor Statement. Protecting your investment, Covenants regulate the standard of homes in an estate. Most new estates also have specific Developer Guidelines, which the buyer must comply with. Your builder can assist you with these when selecting your new home.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":9,\n" +
            "         \"Question\":\"WHAT IS RESCODE?\",\n" +
            "         \"Answer\":\" A Victorian Government Code which sets out the development standards for housing, land subdivisions and town planning.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":10,\n" +
            "         \"Question\":\"WHAT IS A PLANNING PERMIT?\",\n" +
            "         \"Answer\":\"A statement specifying a particular use or development may proceed on a specific piece of land.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":11,\n" +
            "         \"Question\":\"HOW DO I KNOW IF I NEED TO APPLY FOR A PLANNING PERMIT?\",\n" +
            "         \"Answer\":\"The best way to find out whether you need a Planning Permit is to contact your local Council. Generally, this will be required when subdividing land or demolishing a house. Depending on the Council, you may require one to build a new home.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":12,\n" +
            "         \"Question\":\"WHAT IS A SOIL TEST?\",\n" +
            "         \"Answer\":\"This is carried out on your block to determine the soil conditions. By drilling a series of holes and analyzing the contents, soil conditions can be determined to enable an engineer to design the footings of your new home.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":13,\n" +
            "         \"Question\":\"WHAT IS A SITE CLASSIFICATION?\",\n" +
            "         \"Answer\":\"This is determined by the engineer from assessment of the soil test results.The classes include:\nS - Slightly reactive \nM - Moderately reactive\nH - Highly reactive\n E - Extremely reactive\n P - Problem sites\n Classes S,M,H and E generally refer to sites with clay soils and how reactive the soil is to changes in moisture content which can\\r\\n impact on the footings/slab.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":14,\n" +
            "         \"Question\":\"WHAT ARE PIERS?\",\n" +
            "         \"Answer\":\"These are support mechanisms usually made from poured concrete under the slab of a home. Often used where there is ‘fill’ on site. Engineers determine if these are required in the design of your footings.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":15,\n" +
            "         \"Question\":\"WHAT IS SITE FALL?\",\n" +
            "         \"Answer\":\"The amount of slope on your block, determined by a series of contour lines shown on a feature survey.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":16,\n" +
            "         \"Question\":\"WHAT DOES CUT AND FILL MEAN?\",\n" +
            "         \"Answer\":\"The method used to provide a level building area on a sloping site where part of the surface is cut away and used to provide fill on the area of the slope below it.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":17,\n" +
            "         \"Question\":\"WHAT IS A FEATURE SURVEY AND WHO CONDUCTS THIS?\",\n" +
            "         \"Answer\":\"A licenced surveyor will visit the site and prepare a Feature Survey by locating features particular to the site, including fences, trees, pits, adjacent buildings, ground level and contours. The survey will also determine the site fall/slope of the land. This is displayed as a series of contour lines at different levels. Your builder orders this information.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":18,\n" +
            "         \"Question\":\"WHAT IS A RE-ESTABLISHMENT SURVEY?\",\n" +
            "         \"Answer\":\"This survey is an accurate identification of a property boundary. Your builder will sometimes order this from a licenced surveyor before the construction of your home begins.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":19,\n" +
            "         \"Question\":\"WHAT IS AN EASEMENT?\",\n" +
            "         \"Answer\":\" A section of land registered on the Certificate of Title providing Council or utility providers right of access to your property. Often pipes such as sewer or storm water are in the easement. You may not build a house or other permanent structure over an easement without consent.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":20,\n" +
            "         \"Question\":\"WHAT IS A SITING?\",\n" +
            "         \"Answer\":\"Your proposed new home is placed onto your block of land. Your sales consultant will site your home to scale, taking care to comply with the regulatory requirements, including building envelopes and developer guidelines.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":21,\n" +
            "         \"Question\":\"WHAT IS A BUILDING ENVELOPE?\",\n" +
            "         \"Answer\":\"A designated area on your land within which all building work must be contained. A building envelope is registered on your title by the Council and will be shown in the Plan of Subdivision attached to the Vendor Statement.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":22,\n" +
            "         \"Question\":\"WHAT IS A SETBACK?\",\n" +
            "         \"Answer\":\"The minimum allowable distance from the front boundary of the block to the front of your home.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":23,\n" +
            "         \"Question\":\"WHAT ARE SITE COSTS?\",\n" +
            "         \"Answer\":\"Costs which arise from placing a home  onto your land, including service connection costs. These include leveling of the building area, connection to sewer and storm water,removal of trees, piering under the slab, connection to power etc.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":24,\n" +
            "         \"Question\":\"WHAT IS A CROSSOVER?\",\n" +
            "         \"Answer\":\"The kerb opening to your lot installed by the Council to allow vehicle access to the property. It is important to check the location of your crossover when siting your new home.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":25,\n" +
            "         \"Question\":\"WHAT IS A FAÇADE?\",\n" +
            "         \"Answer\":\"The front or face of a house.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":26,\n" +
            "         \"Question\":\"WHAT DOES ROOF PITCH MEAN?\",\n" +
            "         \"Answer\":\"The angle of a sloping roof, usually expressed in degrees, eg. 22 degree pitch.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":27,\n" +
            "         \"Question\":\"WHAT IS A VARIATION ORDER?\",\n" +
            "         \"Answer\":\" An alteration to a standard design or building specification.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":28,\n" +
            "         \"Question\":\"WHAT IS DEVELOPER APPROVAL?\",\n" +
            "         \"Answer\":\"Developer Approval of your building plans is applied for by your builder on your behalf. The developer will check your new home complies with all estate requirements.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":29,\n" +
            "         \"Question\":\"WHAT IS A BUILDING PERMIT?\",\n" +
            "         \"Answer\":\"A building permit must be granted before the construction of your new home commences. This ensures the construction plans and engineering comply with all relevant building regulations and have been approved by a registered building surveyor.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":30,\n" +
            "         \"Question\":\"WHAT IS AN OCCUPANCY PERMIT?\",\n" +
            "         \"Answer\":\"Also called a Certificate of Occupancy, this document is issued by a Building Surveyor after final inspection of your new home. It certifies your home is ready to move into.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":31,\n" +
            "         \"Question\":\"WHAT DOES 'REVERSE BUILD' MEAN\",\n" +
            "         \"Answer\":\"Sometimes, to help our customers move into their brand new home as soon as possible, we may choose to 'reverse build' their home.\n For instance - if there is a temporary shortage of bricklayers - we might choose to start building a home from 'the inside out'. We fully wrap the home to protect it against all the elements while we wait for the brickies. In the meantime, the plasterers and fix-out carpenters work on making the inside of the home beautiful. \n So don't be surprised if this happens to your home! It's a trick of the trade a lot of us builders practice when we know you're keen to move in as soons as possible. Want more info? Feel free to get in touch directly with your Site Supervisor.\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"Id\":32,\n" +
            "         \"Question\":\"HELPFUL LINKS\",\n" +
            "         \"Answer\":\"http://www.firsthome.gov.au' target='_blank'>www.firsthome.gov.au\nhttp://www.sro.vic.gov.au\nhttp://www.npfs.com.au\nhttp://www.burbank.com.au\nhttp://www.mbav.com.au\nhttp://www.reiv.com.au\nhttp://www.vcat.com.au\nhttp://www.buildingcommission.com.au\"\n" +
            "      }\n" +
            "   ]\n" +
            "}";

    public static String getInvite_ReInvite_Url(boolean val) {
        if (val == true) {
            return Common.sendInviteUrl;

        } else {
            return Common.addCoBurbankUrl;
        }
    }
    public static void showChangePhaseMessageAlert(Activity mContext) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                SpannableString title = new SpannableString("MyPlace");
                title.setSpan(
                        new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        0,
                        title.length(),
                        0
                );
                SpannableString message = new SpannableString("Do you want to change phase?");
                message.setSpan(
                        new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        0,
                        title.length(),
                        0
                );
                builder.setMessage(message);
                builder.setTitle(title);
                builder.setCancelable(false);
                builder.setPositiveButton(Html.fromHtml("<font color=" + mContext.getResources().getColor(R.color.appColor) + ">Yes</font>"), (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.dismiss();
                    Intent i = new Intent(mContext, HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(i);
                });
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });

                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
                Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
                layoutParams.weight = 10;
                btnPositive.setLayoutParams(layoutParams);
                btnNegative.setLayoutParams(layoutParams);
            }
        });
    }
    public static void showMessageAlert(Activity mContext, String message) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(message);
                builder.setTitle(mContext.getString(R.string.app_name));
                builder.setCancelable(false);
                builder.setPositiveButton(Html.fromHtml("<font color=" + mContext.getResources().getColor(R.color.appColor) + ">OK</font>"), (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.dismiss();
                });
       /* builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });*/

                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
            }
        });
    }

}
