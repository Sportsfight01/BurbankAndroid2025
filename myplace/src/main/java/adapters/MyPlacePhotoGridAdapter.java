package adapters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.jakewharton.picasso.OkHttp3Downloader;

import java.io.IOException;
import java.util.ArrayList;

import common.AppController;

import com.dmss.burbankappold.R;
import com.dmss.burbankappold.fragments.MyPhotosDateViseList;
import com.squareup.picasso.Picasso;

import models.MyDocOrPhotosDataSetQldOrSa;
import models.MyPhotosNewDateWiseDataSet;
import models.MyPlacePhotosDetailsDataSet;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class MyPlacePhotoGridAdapter extends BaseAdapter {

    Activity context;
    ArrayList<MyPlacePhotosDetailsDataSet> myPlacePhotosDetailsDataSets;
    int height;
    AppController controller;
    int selectedPosition;
    boolean regionVic;
    ArrayList<MyDocOrPhotosDataSetQldOrSa> QldOrSaPhotosList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();
    ArrayList<MyPhotosNewDateWiseDataSet> outerArrayNewDateWiseDataSets = new ArrayList<MyPhotosNewDateWiseDataSet>();
    String root;
    OkHttpClient client;
    Picasso picasso;

    public MyPlacePhotoGridAdapter(Activity context, ArrayList<MyPhotosNewDateWiseDataSet> outerArrayNewDateWiseDataSets, ArrayList<MyDocOrPhotosDataSetQldOrSa> QldOrSaPhotosList, int height, int slectedPosition, boolean vic) {
        this.context = context;
        this.QldOrSaPhotosList = QldOrSaPhotosList;
        this.height = height;
        controller = (AppController) context.getApplicationContext();
        this.selectedPosition = slectedPosition;
        this.regionVic = false;
        this.outerArrayNewDateWiseDataSets = outerArrayNewDateWiseDataSets;
        root = Environment.getExternalStorageDirectory().toString();

        String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
        byte[] message = sample.getBytes();
        final String encoded = Base64.encodeToString(message, Base64.NO_WRAP);
        client = new OkHttpClient.Builder()
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

        picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .build();
    }

    @Override
    public int getCount() {
        if (regionVic) {
            return myPlacePhotosDetailsDataSets.size();
        } else {
            return QldOrSaPhotosList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (regionVic) {
            return myPlacePhotosDetailsDataSets.get(position);
        } else {
            return QldOrSaPhotosList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (regionVic) {
            return myPlacePhotosDetailsDataSets.get(position).hashCode();
        } else {
            return QldOrSaPhotosList.get(position).hashCode();
        }
    }

    @Override
    public View getView(final int innerItemPosition, View convertView, ViewGroup parent) {

        final Holder holder = new Holder();

        RelativeLayout.LayoutParams paramss = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        View rowView = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.photos_grid_item, null);
        } else {
            rowView = convertView;
        }

        holder.photoGridImageView = rowView.findViewById(R.id.photoGridImageView);
        holder.progressBar = rowView.findViewById(R.id.progressBar);
        holder.photoGridImageView.setLayoutParams(paramss);
        holder.progressBar.setLayoutParams(paramss);
        holder.photoGridImageView.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.VISIBLE);

        if (regionVic) {
            final MyPlacePhotosDetailsDataSet myPlacePhotosDetailsDataSet = myPlacePhotosDetailsDataSets.get(innerItemPosition);
            Picasso.get().load(myPlacePhotosDetailsDataSet.getImagePath()).into(holder.photoGridImageView);

            holder.photoGridImageView.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
        } else {
            holder.photoGridImageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(QldOrSaPhotosList.get(innerItemPosition).getUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                    })
                    .into(holder.photoGridImageView);
        }

        holder.photoGridImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.setSelectedDateOfPhotos(selectedPosition);
                controller.setPhotoScrollToThePosition(innerItemPosition);
                controller.getAnalytics().photoTouchEvent();
                Fragment fragment = new MyPhotosDateViseList();
                FragmentManager fragmentManager = context.getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
            }
        });
        return rowView;
    }


    public class Holder {
        ImageView photoGridImageView;
        LinearLayout progressBar;
    }

    public void updateDownloadedList(MyDocOrPhotosDataSetQldOrSa dataSetQldOrSa, int position) {
        dataSetQldOrSa.setImageDownloaded(true);
        QldOrSaPhotosList.remove(position);
        QldOrSaPhotosList.add(position, dataSetQldOrSa);
        notifyDataSetChanged();
    }

    public boolean canSendInsideOrNot() {
        boolean sendInside = true;
        for (int i = 0; i < QldOrSaPhotosList.size(); i++) {
            if (!QldOrSaPhotosList.get(i).isImageDownloaded()) {
                sendInside = false;
                break;
            }
        }
        return sendInside;
    }

}