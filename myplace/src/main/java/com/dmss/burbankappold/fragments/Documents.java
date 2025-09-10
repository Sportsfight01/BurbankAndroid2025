package com.dmss.burbankappold.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.MyPlaceDocumentAdapter;
import common.AppController;
import common.Common;
import common.TransparentProgressDialog;
import common.Utils;

import com.dmss.burbankappold.R;

import interfaces.FragmentCallBack;
import interfaces.WebApiJobDetailsResponseCallBack;
import models.MyDocOrPhotosDataSetQldOrSa;
import models.MyDocumentsDataSetVIC;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class Documents extends Fragment implements WebApiJobDetailsResponseCallBack {
    ListView list;
    public static ArrayList<MyDocumentsDataSetVIC> VicMyDocuments = null;
    public ArrayList<MyDocOrPhotosDataSetQldOrSa> QldOrSaMyDocuments = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();

    MyPlaceDocumentAdapter VICAdapter = null, QldOrSaDocumentAdapter;
    AppController controller;
    TransparentProgressDialog pd;
    View rootView;
    //Boolean isLoginRequested = false;
    TextView documentsJobNumberTextView;

    LinearLayout jobNumberLayout;
    WebView documentImageView;
    FragmentCallBack fragmentCallBack;

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBarSettings();
        setContentView(R.layout.activity_my_photos);
        initializeUI();
    }*/

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_my_documents, null);
        controller = (AppController) getActivity().getApplicationContext();
        controller.getAnalytics().setScreen(getActivity(), "Documents_Screen");
        initializeUI();
        return rootView;
    }


    public void initializeUI() {
        TextView firstText = rootView.findViewById(R.id.firstText);
        firstText.setText("Documents");
        TextView secondText = rootView.findViewById(R.id.secondText);
        secondText.setVisibility(View.GONE);

        ImageView backImage = rootView.findViewById(R.id.back_image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        fragmentCallBack = (FragmentCallBack) getActivity();
        list = (ListView) rootView.findViewById(R.id.lVMyDocumentsId);
        documentImageView = (WebView) rootView.findViewById(R.id.documentImageView);
        documentImageView.setVisibility(View.GONE);
        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list.setVisibility(View.GONE);
                documentImageView.setVisibility(View.VISIBLE);
                *//*String doc="<iframe src='http://docs.google.com/viewer?url="+QldOrSaMyDocuments.get(position).getUrl()+"'width='100%' height='100%'style='border: none;'></iframe>";
                String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
                byte[] message = sample.getBytes();
                final String encoded = Base64.encodeToString(message, Base64.NO_WRAP);
                Map<String, String> extraHeaders = new HashMap<String, String>();
                extraHeaders.put("Authorization","Basic " + encoded);
                extraHeaders.put("ContractNumber",controller.getMy_Place_Details().getJobNumber());
                documentImageView.loadUrl(doc,extraHeaders);*//*


                documentImageView.getSettings().setJavaScriptEnabled(true);

                //---you need this to prevent the webview from
                // launching another browser when a url
                // redirection occurs---

                documentImageView.loadUrl(
                        "http://docs.google.com/gview?embedded=true&url=" + QldOrSaMyDocuments.get(position).getUrl());
                *//*OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request newRequest = chain.request().newBuilder()
                                        .addHeader("Authorization", "Basic " + encoded)
                                        .addHeader("ContractNumber", controller.getMy_Place_Details().getJobNumber())
                                        .build();
                                return chain.proceed(newRequest);
                            }
                        })
                        .build();

                Picasso picasso = new Picasso.Builder(getActivity())
                        .downloader(new OkHttp3Downloader(client))
                        .build();
                picasso.load(QldOrSaMyDocuments.get(position).getUrl())
                        .resize(200, 200).error(R.drawable.bg).into(documentImageView);*//*
            }
        });*/
        documentsJobNumberTextView = (TextView) rootView.findViewById(R.id.documentsJobNumberTextView);
        documentsJobNumberTextView.setText(controller.getMy_Place_Details().getJobNumber());
        jobNumberLayout = (LinearLayout) rootView.findViewById(R.id.jobNumberLayout);
        jobNumberLayout.setVisibility(View.VISIBLE);
        callForNewApi();
    }

    public void infoDialog() {
        final Dialog infoDialog = new Dialog(getActivity());
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoDialog.setContentView(R.layout.custom_dialog);
        final Button dialogOkButton = (Button) infoDialog.findViewById(R.id.dialogOkButton);
        final TextView dialogMessageTextView = (TextView) infoDialog.findViewById(R.id.dialogMessageTextView);
        TextView dialogHeadingTextView = (TextView) infoDialog.findViewById(R.id.dialogHeadingTextView);
        dialogHeadingTextView.setText("My Place Validation");
        {
            dialogMessageTextView.setText("Error : Invalid details for selected job number.\nPlease select other job.");
        }
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
            }
        });
        infoDialog.show();
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

    /*public void callApi(int i) {
        if (i == 1) {
            isLoginRequested = true;
            pd = Utils.getProgress(getActivity());
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String val = controller.webApiCall().postData_to_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.MyPlaceUserCheckUrl, getMyPlaceLoginJson());
                    onSuccessResult(val);
                }
            });
            t.start();

        } else {
            if ((controller.getConstructionId().length() > 0) && (controller.getOfficeId().length() > 0)) {
                if (Utils.isNetworkAvailable(getActivity())) {

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String s = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.myPlaceDocuments + "constructionTicketID=" + controller.getConstructionId() + "&officeTicketID=" + controller.getOfficeId() + "");
                            onSuccessResult(s);
                        }
                    });
                    t.start();
                }
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null) {
                            pd.cancel();
                        }
                    }
                });
                if (controller.getConstructionId().length() == 0) {
                    Utils.showToast(getActivity(), "Error: Construction Id not found against this Job Number", Common.errorCase);
                } else {
                    Utils.showToast(getActivity(), "Error: Office Ticket Id not found against this Job Number", Common.errorCase);
                }


            }
        }
    }*/

    public void callForNewApi() {
        if (Utils.isNetworkAvailable(getActivity())) {

            pd = Utils.getProgress(getActivity());
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    //isLoginRequested = false;
                    String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
                    byte[] message = sample.getBytes();
                    String encoded = Base64.encodeToString(message, Base64.NO_WRAP);
                    //String result = controller.webApiCall().getDataWithHeaders(Common.newMyPlacePhotosOrDocuments, encoded, controller.getMy_Place_Details().getJobNumber());
                    //String result = controller.webApiCall().getDataNewMyPlace(Common.newMyPlacePhotosOrDocuments, controller.getMy_Place_Details());
                    controller.webApiCall().getDataNewMyPlace(Common.newMyPlacePhotosOrDocuments, controller.getMy_Place_Details(), Documents.this);
                    //onSuccessResult(result);
                }
            });
            t.start();
        }
    }


    public void jsonParsing(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            QldOrSaMyDocuments = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();
            for (int i = 0; i < jsonArray.length(); i++) {
                MyDocOrPhotosDataSetQldOrSa myDocumentsDataSetQldOrSa = new MyDocOrPhotosDataSetQldOrSa(jsonArray.getJSONObject(i).toString(), getActivity());
                if (myDocumentsDataSetQldOrSa.getType().trim().equalsIgnoreCase("pdf")) {
                    QldOrSaMyDocuments.add(myDocumentsDataSetQldOrSa);
                }

            }
            setData();
        } catch (Exception ex) {
            ex.fillInStackTrace();
            if (pd != null) {
                pd.cancel();
            }
        }
    }

    public void setData() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                QldOrSaDocumentAdapter = new MyPlaceDocumentAdapter(getActivity(), QldOrSaMyDocuments, false);
                list.setAdapter(QldOrSaDocumentAdapter);
                fragmentCallBack.actionbarTextChange("My Documents ( " + Integer.toString(QldOrSaMyDocuments.size()) + " )");
                if (pd != null) {
                    pd.cancel();
                }
            }
        });
    }

    @Override
    public void onJobDetailsSuccessResult(String result) {
        /*if (isLoginRequested == true) {
            if ((result != null) && (result.length() > 0) && (result.equalsIgnoreCase("true"))) {
                isLoginRequested = false;
                callApi(2);
            } else {
                if (pd != null) {
                    pd.cancel();
                }
            }
        } else {*/
        if ((result != null) && (result.length() > 0)) {
            jsonParsing(result);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Utils.showToast(getActivity(), Common.somethingErrorMessage, Common.errorCase);
                    if (pd != null) {
                        pd.cancel();
                    }
                }
            });
        }
        /*}*/
    }

    @Override
    public void onJobDetailsErrorResult(String error) {

    }

}