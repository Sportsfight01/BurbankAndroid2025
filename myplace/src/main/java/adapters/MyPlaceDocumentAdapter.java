package adapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import common.AppController;
import common.TransparentProgressDialog;
import common.Utils;
import com.dmss.burbankappold.R;
import models.MyDocOrPhotosDataSetQldOrSa;
import models.MyDocumentsDataSetVIC;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class MyPlaceDocumentAdapter extends BaseAdapter {


    Activity act;
    ArrayList<MyDocumentsDataSetVIC> VICData;
    ArrayList<MyDocOrPhotosDataSetQldOrSa> QldOrSaData;
    private static LayoutInflater inflater = null;
    boolean regionVic = false;
    TransparentProgressDialog pd;
    AppController controller;

    public MyPlaceDocumentAdapter(Activity act, ArrayList<MyDocumentsDataSetVIC> data/*, TransparentProgressDialog pd*/) {
        // TODO Auto-generated constructor stub
        this.act = act;
        this.VICData = data;

        inflater = (LayoutInflater) act.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        regionVic = true;
        this.pd = pd;
    }

    public MyPlaceDocumentAdapter(Activity act, ArrayList<MyDocOrPhotosDataSetQldOrSa> data, boolean vic) {
        // TODO Auto-generated constructor stub
        this.act = act;
        this.QldOrSaData = data;
this.controller = (AppController) act.getApplicationContext();
        inflater = (LayoutInflater) act.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        regionVic = false;
    }

    @Override
    public int getCount() {
        if (regionVic) {
            return VICData.size();
        } else {
            return QldOrSaData.size();
        }

    }

    @Override
    public Object getItem(int position) {
        if (regionVic) {
            return VICData.get(position);
        } else {
            return QldOrSaData.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (regionVic) {
            return VICData.get(position).hashCode();
        } else {
            return QldOrSaData.get(position).hashCode();
        }
    }

    public class Holder {
        TextView name, details;
        ImageView icon;
        LinearLayout row;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.personaldocumentrow, null);
        holder.name = (TextView) rowView.findViewById(R.id.name);
        holder.details = (TextView) rowView.findViewById(R.id.fileDetails);
        holder.icon = (ImageView) rowView.findViewById(R.id.fileIcon);
        holder.row = (LinearLayout) rowView.findViewById(R.id.roww);


        if (regionVic) {
            final MyDocumentsDataSetVIC details = VICData.get(position);
            holder.name.setText(details.getTitle() + ".pdf");
            holder.details.setText("Uploaded on:" + details.getUploadedOn());
        } else {
            final MyDocOrPhotosDataSetQldOrSa documentsDataSetQldOrSa = QldOrSaData.get(position);
            holder.name.setText(documentsDataSetQldOrSa.getTitle() + ".pdf");
            /*10/4/2017 1:15:26 PM*/
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            SimpleDateFormat sdfString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa");

            String dateString = documentsDataSetQldOrSa.getDocDate();
            String finalString = "";
            try {
                Date complionDate = sdf.parse(dateString);
                finalString = sdfString.format(complionDate);
            } catch (ParseException e) {
                e.printStackTrace();
                dateString = "";
            }
            holder.details.setText("Uploaded on:" + finalString);
        }

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (regionVic) {
                    openFileAccordingToType(VICData.get(position).getPath(), *//*act,*//* VICData.get(position).getExtension());
                } else {
                    openFileAccordingToType(QldOrSaData.get(position).getUrl(), *//*act,*//* "pdf");
                }*/
                controller.getAnalytics().documentsViewedEvent();
                pdf(QldOrSaData.get(position).getUrlInt(),QldOrSaData.get(position).getUrl());
            }
        });
        return rowView;
    }


    /*public void openFileAccordingToType(String s,*//* Activity act,*//* String ext) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        if ((ext.trim().toLowerCase().equalsIgnoreCase(".png")) || (ext.trim().toLowerCase().equalsIgnoreCase(".jpg")) || (ext.trim().toLowerCase().equalsIgnoreCase(".jpeg"))) {
            intent.setData(Uri.parse(s));

        } else if (ext.trim().toLowerCase().equalsIgnoreCase(".pdf")) {
            intent.setDataAndType(Uri.parse(s), "application/pdf");

        } else if (ext.trim().toLowerCase().equalsIgnoreCase(".doc")) {
            intent.setDataAndType(Uri.parse(s), "application/*");
        }

        act.startActivity(intent);
    }*/


    public String convertTime(String time) {
        long val = Long.parseLong(time);
        Date date = new Date(val);
        Format format = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        return format.format(date);
    }

    public void pdf(int urlInt,String pdfUrl){
        Intent intent = new Intent();
        intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
        act.startActivity(intent);
        /*File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MyPlaceDocs");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        String jobNumber = ((AppController) act.getApplicationContext()).getMy_Place_Details().getJobNumber();
        File pdfFile = new File(mediaStorageDir.getPath() + File.separator , "Doc_"+jobNumber+"_"+Integer.toString(urlInt)+".pdf");
        if (!pdfFile.exists()) {
            try{
                pdfFile.createNewFile();
                new DownloadFile(pdfUrl , pdfFile).execute("","");
            }catch (IOException e){
                e.printStackTrace();
            }

        }else{
            Uri path = Uri.fromFile(pdfFile);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try{
                act.startActivity(pdfIntent);
            }catch(ActivityNotFoundException e){
                Toast.makeText(act, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    private class DownloadFile extends AsyncTask<String, Void, Boolean> {
        String pdfUrl;
        File pdfFile;
        public DownloadFile(String pdfUrl , File pdfFile){
            this.pdfFile = pdfFile;
            this.pdfUrl = pdfUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = Utils.getProgress(act);

        }

        @Override
        protected Boolean doInBackground(String... strings) {

            /*FileDownloader downloader = new FileDownloader();
            downloader.downloadFile(pdfUrl, pdfFile);*/

            try {

                URL url = new URL(pdfUrl);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                //urlConnection.setRequestMethod("GET");
                //urlConnection.setDoOutput(true);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);
                int totalSize = urlConnection.getContentLength();

                byte[] buffer = new byte[1024 * 1024];
                int bufferLength = 0;
                while((bufferLength = inputStream.read(buffer))>0 ){
                    fileOutputStream.write(buffer, 0, bufferLength);
                }
                fileOutputStream.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }


        }

        @Override
        protected void onPostExecute(Boolean downloadSuccess) {
            super.onPostExecute(downloadSuccess);
            if(downloadSuccess){
                Uri path = Uri.fromFile(pdfFile);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                try{
                    act.startActivity(pdfIntent);
                }catch(ActivityNotFoundException e){
                    Toast.makeText(act, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                }
            }
            pd.cancel();
        }
    }

}
