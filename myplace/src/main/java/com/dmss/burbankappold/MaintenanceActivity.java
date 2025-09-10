package com.dmss.burbankappold;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import common.AppController;
import common.Utils;

public class MaintenanceActivity extends AppCompatActivity {

    TextView welcomeDescMTextView, dateMTextView, maintenanceTextView, maintenanceDescTextView;
    String title, message;
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);
        AppController controller = (AppController) getApplicationContext();
        JSONObject jsonObject = controller.getMaintenanceObject();
        welcomeDescMTextView = (TextView) findViewById(R.id.welcomeDescMTextView);
        dateMTextView = (TextView) findViewById(R.id.dateMTextView);
        maintenanceTextView = (TextView) findViewById(R.id.maintenanceTextView);
        maintenanceDescTextView = (TextView) findViewById(R.id.maintenanceDescTextView);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        mainLayout.bringToFront();
        actionBarSettings();
        try {
            title = jsonObject.isNull("Title") ? "" : jsonObject.getString("Title");
            message = jsonObject.isNull("Message") ? "" : jsonObject.getString("Message");
            String startDate = jsonObject.isNull("StartDateTime") ? "" : jsonObject.getString("StartDateTime");
            String endDate = jsonObject.isNull("EndDateTime") ? "" : jsonObject.getString("EndDateTime");

            /*SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
            SimpleDateFormat mdyFormat = new SimpleDateFormat("dd/MM/yyyy");

            Date startDateFormat = sdf.parse(startDate);
            Date endDateFormat = sdf.parse(endDate);
            String displayStart = mdyFormat.format(startDateFormat);
            String displayEnd = mdyFormat.format(endDateFormat);*/
            message = message + "\n\n\nStart Date - " + startDate + "\nEnd Date - " + endDate;

        } catch (JSONException e) {
            e.printStackTrace();
            title = "Our App is currently undergoing maintenance.";
            message="";
        } catch (Exception ex) {

        }


        dateMTextView.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date()));
        if (controller.getUserProfile() != null && (controller.getUserProfile().getUserDetails().size() > 0) && (controller.getUserProfile().getUserDetails().get(0).getFullName() != null)) {
            welcomeDescMTextView.setText("Welcome " + Utils.getCamelCase(controller.getUserProfile().getUserDetails().get(0).getFullName()));
        } else {
            welcomeDescMTextView.setText("Welcome ");
        }
        maintenanceTextView.setText(title);
        maintenanceDescTextView.setText(message);

        dateMTextView.setTypeface(controller.getTypeface());
        maintenanceTextView.setTypeface(controller.getTypefaceRegular());
        maintenanceDescTextView.setTypeface(controller.getTypefaceRegular());
        welcomeDescMTextView.setTypeface(controller.getTypefaceBold());
    }


    public void actionBarSettings() {
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_actionbar_transparent, null);
        view.setBackgroundResource(R.color.white);
        final ImageView backImageView = (ImageView) view.findViewById(R.id.backImageView);
        backImageView.setVisibility(View.GONE);
        final ImageView burbankLogoImageView = (ImageView) view.findViewById(R.id.burbankLogoImageView);
        burbankLogoImageView.setVisibility(View.VISIBLE);
        TextView actionBarHeadingTextView = (TextView) view.findViewById(R.id.actionBarHeadingTextView);
        actionBarHeadingTextView.setVisibility(View.GONE);
        actionBarHeadingTextView.setText("My Appointments");
        TextView saveSettings = (TextView) view.findViewById(R.id.saveSettings);
        saveSettings.setVisibility(View.GONE);
        actionBar.setCustomView(view, new  ActionBar.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM |  ActionBar.DISPLAY_SHOW_HOME);
    }
}
