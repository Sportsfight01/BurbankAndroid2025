package common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.widget.EditText;
import android.widget.Toast;

import com.dmss.burbankappold.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jaya.krishna on 14-12-2016.
 */

public class Validation {
    private static Pattern pattern;
    private static Matcher matcher;
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,3})$";
    private static final String PASSWORD_REGEX = "[_A-Za-z0-9]{8,16}";
    private static final String USERNAME_REGEX = "[_A-Za-z0-9]{6,16}";
    private static final String PHONE_REGEX = "[0-9]{10}";
    Context context;

    public Validation(Context context) {
        this.context = context;
    }


    /***
     * validate password with given password regular expression
     */
//    public boolean validatePassword(CustomEditText password) {
//        boolean matches = false;
//        if (isNotNull(password)) {
//            String pwd = password.getText().toString().trim();
//            pattern = Pattern.compile(PASSWORD_REGEX);
//            matcher = pattern.matcher(pwd);
//            matches = matcher.matches();
//            if (!matches) {
//                if (pwd.length() < 8) {
//                    Toast.makeText(context, "Password should be at least 8 characters ", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(context, "Please enter valid password", Toast.LENGTH_SHORT).show();
//                }
//            }
//        } else {
//            Toast.makeText(context, "Password should not be empty", Toast.LENGTH_SHORT).show();
//        }
//        return matches;
//    }

    /***
     * validate password with given password regular expression
     */
    public boolean validatePassword(EditText password) {
        boolean matches = false;
        if (isNotNull(password)) {
            String pwd = password.getText().toString().trim();
            pattern = Pattern.compile(PASSWORD_REGEX);
            matcher = pattern.matcher(pwd);
            matches = matcher.matches();
            if (!matches) {
                if (pwd.length() < 8) {
                    Toast.makeText(context, "Password should be at least 8 characters ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Please enter valid password", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(context, "Password should not be empty", Toast.LENGTH_SHORT).show();
        }
        return matches;
    }

    /***
     * validate user name with given user name regular expression
     */
//    public boolean validateUsername(CustomEditText username) {
//        boolean matches = false;
//        if (isNotNull(username)) {
//            String uName = username.getText().toString().trim();
//            pattern = Pattern.compile(USERNAME_REGEX);
//            matcher = pattern.matcher(uName);
//            matches = matcher.matches();
//            if (!matches) {
//                if (uName.length() < 6) {
//                    Toast.makeText(context, "Username should be at least 6 characters ", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(context, "Please enter valid username", Toast.LENGTH_SHORT).show();
//                }
//            }
//        } else {
//            Toast.makeText(context, "Username should not be empty", Toast.LENGTH_SHORT).show();
//        }
//
//        return matches;
//
//    }

    /***
     * validate email string with given email regular expression
     */
//    public boolean validateEmail(CustomEditText email,Activity act) {
//        if (isNotNull(email)) {
//            pattern = Pattern.compile(EMAIL_REGEX);
//            matcher = pattern.matcher(email.getText().toString());
//            if (matcher.matches()) {
//                return true;
//            } else {
//                Utils.showToast(act, "Please enter valid email Id", Common.errorCase);
//
//            }
//        } else {
//            Utils.showToast(act, "Please enter email Id", Common.errorCase);
//
//        }
//        return false;
//    }

    /***
     * validate email string with given email regular expression
     */
    public boolean validateEmail(EditText email, Activity act) {
        if (isNotNull(email)) {
            pattern = Pattern.compile(EMAIL_REGEX);
            matcher = pattern.matcher(email.getText().toString());
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }
    public boolean validateEmail(CustomEditText email, Activity act) {
        if (isNotNull(email)) {
            pattern = Pattern.compile(EMAIL_REGEX);
            matcher = pattern.matcher(email.getText().toString().trim());
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    public boolean validateEmail(String email, Activity act) {
        if (isNotNull(email)) {
            pattern = Pattern.compile(EMAIL_REGEX);
            matcher = pattern.matcher(email.trim());
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }
    public boolean validateString(EditText txt, Activity act) {
        if (isStringNotNull(txt)) {
                return true;

        } else {
            Utils.showToast(act, "Please enter data", Common.errorCase);

        }
        return false;
    }
    public boolean validateString(CustomEditText txt,Activity act) {
        if (isStringNotNull(txt)) {
            return true;

        } else {
            Utils.showToast(act, "Please enter data", Common.errorCase);

        }
        return false;
    }
    /**
     * check whether string contains value or not
     */
    public boolean isNotNull(CustomEditText txt) {
        try {

            if ((txt != null) && (txt.getText().toString().trim().length() > 0)) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.fillInStackTrace();
            return false;

        }
    }

    public boolean isNotNull(String txt) {
        try {

            if ((txt != null) && (txt.trim().length() > 0)) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.fillInStackTrace();
            return false;

        }
    }

    /**
     * check whether string contains value or not
     */
    public boolean isNotNull(EditText txt) {
        try {

            if ((txt != null) && (txt.getText().toString().trim().length() > 0)) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.fillInStackTrace();
            return false;

        }
    }

    /**
     * check whether string contains value or not
     */
    public boolean isStringNotNull(EditText txt) {
        try {

            if ((txt != null) && (txt.getText().toString().trim().length() > 0)) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.fillInStackTrace();
            return false;

        }
    }
    public boolean isStringNotNull(CustomEditText txt) {
        try {

            if ((txt != null) && (txt.getText().toString().trim().length() > 0)) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.fillInStackTrace();
            return false;

        }
    }
    /***
     * validate phone string with given phone regular expression
     */
    public boolean validPhone(CustomEditText phone) {
        boolean matches = false;
        if (isNotNull(phone)) {
            String phoneNumber = phone.getText().toString().trim();
            pattern = Pattern.compile(PHONE_REGEX);
            matcher = pattern.matcher(phoneNumber);
            matches = matcher.matches();
            if (!matches) {
                if (phoneNumber.length() < 10) {
                    Toast.makeText(context, "Phone number should be at least 10 characters ", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(context, "Phone number should not be empty", Toast.LENGTH_SHORT).show();
        }
        return matches;
    }

    /***
     * validate phone string with given phone regular expression
     */
    public boolean validPhoneLength(Activity mContext,CustomEditText phone) {
        boolean matches = false;
        if (isNotNull(phone)) {
            String phoneNumber = phone.getText().toString().trim();
            if(phoneNumber.length() >= 10 && phoneNumber.length() <= 12){
                matches = true;
            }else{
                Common.showMessageAlert( mContext,"Phone number should contain minimum 10 digits");
//                Toast.makeText(context, "Phone number should be at minimum 10 and maximum 12 characters ", Toast.LENGTH_SHORT).show();
            }
        } else {
            Common.showMessageAlert( mContext,"Please enter phone number");

//            Toast.makeText(context, "Phone number should not be empty", Toast.LENGTH_SHORT).show();
        }
        return matches;
    }
}
