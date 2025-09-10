package adapters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.dmss.burbankappold.ZoomImageActivity;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import common.AppController;
import common.MyPlaceDataBase;

import com.dmss.burbankappold.R;
import com.dmss.burbankappold.fragments.MyPhotosAddNotes;

import common.TransparentProgressDialog;
import interfaces.FragmentCallBack;
import models.MyDocOrPhotosDataSetQldOrSa;

/**
 * Created by jaya.krishna on 06-06-2017.
 */

public class MyPlacePhotoDateViseListAdapter extends BaseAdapter {

    Context context;
    //ArrayList<MyPlacePhotosDetailsDataSet> myPlacePhotosDetailsDataSets;
    int height;
    AppController controller;
    ArrayList<MyDocOrPhotosDataSetQldOrSa> QldOrSaPhotosList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();
    boolean newApi;
    FragmentCallBack fragmentCallBack;
    boolean isFav;
    MyPlaceDataBase myPlaceDataBase;
    View emptyView;
    Picasso picasso;
    Activity activity;
    ProgressDialog dialog2;

    TransparentProgressDialog dialog;

    String root;
    /*public MyPlacePhotoDateViseListAdapter(Context context, ArrayList<MyPlacePhotosDetailsDataSet> myPlacePhotosDetailsDataSets, int height, AppController controller, boolean isFav, View view) {
        this.context = context;
        this.myPlacePhotosDetailsDataSets = myPlacePhotosDetailsDataSets;
        this.height = height;
        this.controller = controller;
        this.newApi = false;
        this.isFav = isFav;
        fragmentCallBack = (FragmentCallBack) context;
        myPlaceDataBase = new MyPlaceDataBase(context);
        emptyView = view;
    }*/

    public MyPlacePhotoDateViseListAdapter(Activity activity, Context context, ArrayList<MyDocOrPhotosDataSetQldOrSa> QldOrSaPhotosList, int height, AppController controller, boolean newApi, boolean isFav, View view, Picasso picasso) {
        this.context = context;
        this.QldOrSaPhotosList = QldOrSaPhotosList;
        this.height = height;
        this.newApi = true;
        this.controller = controller;
        this.isFav = isFav;
        fragmentCallBack = (FragmentCallBack) context;
        myPlaceDataBase = new MyPlaceDataBase(context);
        emptyView = view;
        this.picasso = picasso;
        this.activity = activity;
        dialog2 = new ProgressDialog(activity);
        dialog2.setMessage("Doing something, please wait.");
        root = Environment.getExternalStorageDirectory().toString();
    }


    @Override
    public int getCount() {
        //if (newApi) {
        return QldOrSaPhotosList.size();
        /*} else {
            return myPlacePhotosDetailsDataSets.size();
        }*/
    }

    @Override
    public Object getItem(int position) {
        //if (newApi) {
        return QldOrSaPhotosList.get(position);
        /*} else {
            return myPlacePhotosDetailsDataSets.get(position);
        }*/
    }

    @Override
    public long getItemId(int position) {
        //if (newApi) {
        return QldOrSaPhotosList.get(position).hashCode();
        /*} else {
            return myPlacePhotosDetailsDataSets.get(position).hashCode();
        }*/
    }

    @Override
    public View getView(final int outerItemPosition, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        View rowView = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.photo_datevise_list_adapter_item, null);
        } else {
            rowView = convertView;
        }

        holder.photoDateViseImageView = rowView.findViewById(R.id.photoDateViseImageView);
        holder.photoDateViseImageView.setLayoutParams(params);
        holder.zoomPhotoImageView = rowView.findViewById(R.id.zoomPhotoImageView);
        holder.favPhotoImageView = rowView.findViewById(R.id.favPhotoImageView);
        holder.notesSaveTextView = rowView.findViewById(R.id.notesSaveTextView);
        holder.notesTextView = rowView.findViewById(R.id.photoNotesTextView);
        holder.photoDescTextView = rowView.findViewById(R.id.photoDescTextView);
        holder.progress = rowView.findViewById(R.id.progress);

      /*  float radius = context.getResources().getDimension(R.dimen._10dp);
        binding.thumbnail.setShapeAppearanceModel(binding.thumbnail.getShapeAppearanceModel()
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, radius)
                .build());*/

        /*if (newApi) {
            if (isFav) {
                holder.notesSaveTextView.setVisibility(View.GONE);
            }*/
        if (QldOrSaPhotosList.get(outerItemPosition).getNotes().length() > 0) {
            holder.notesSaveTextView.setText("Edit");
            holder.notesTextView.setVisibility(View.VISIBLE);
            holder.notesTextView.setText(QldOrSaPhotosList.get(outerItemPosition).getNotes());
        } else {
            holder.notesSaveTextView.setText("Add");
            holder.notesTextView.setVisibility(View.GONE);
        }

        if (QldOrSaPhotosList.get(outerItemPosition).isFav()) {
            holder.favPhotoImageView.setImageResource(R.drawable.fav_selected);
        } else {
            holder.favPhotoImageView.setImageResource(R.drawable.fav_orange_borber);
        }
        holder.photoDescTextView.setText(QldOrSaPhotosList.get(outerItemPosition).getDisplayTime());
        holder.zoomPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ZoomImageActivity.class);
                intent.putExtra("SelectedImage", QldOrSaPhotosList.get(outerItemPosition).getUrl());
                intent.putExtra("SelectedImageUrlInt", QldOrSaPhotosList.get(outerItemPosition).getUrlInt());
                activity.startActivity(intent);
            }
        });

        holder.photoDateViseImageView.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(QldOrSaPhotosList.get(outerItemPosition).getUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                        holder.progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progress.setVisibility(View.GONE);
                        return false;
                    }

                })
                .into(holder.photoDateViseImageView);


        /*

        picasso.load(QldOrSaPhotosList.get(outerItemPosition).getUrl())
                .resize(200, 200).into(holder.photoDateViseImageView);*/
       /* File currentPic = new File(root + "/Burbank Photos/" + Integer.toString(QldOrSaPhotosList.get(outerItemPosition).getUrlInt()) + ".jpg");
        if (currentPic.exists()) {
            *//*picasso.load(currentPic).into(holder.photoDateViseImageView);*//*
            Glide.with(context)
                    .load(currentPic.getAbsolutePath())
                    .into(holder.photoDateViseImageView);
            Log.e("Photos", "Grid 2 " + outerItemPosition + "    " + currentPic.getAbsolutePath());
        } else {
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    File currentPic = new File(root + "/Burbank Photos/" + Integer.toString(QldOrSaPhotosList.get(outerItemPosition).getUrlInt()) + ".jpg");
                    try {

                        File myDir = new File(root + "/Burbank Photos");

                        if (!myDir.exists()) {
                            myDir.mkdirs();
                        }

                        String name = Integer.toString(QldOrSaPhotosList.get(outerItemPosition).getUrlInt()) + ".jpg";
                        myDir = new File(myDir, name);
                        FileOutputStream out = new FileOutputStream(myDir);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                        out.flush();
                        out.close();
                        Glide.with(context)
                                .load(currentPic.getAbsolutePath())
                                .into(holder.photoDateViseImageView);
                        //picasso.load(currentPic).into(holder.photoDateViseImageView);
                    } catch (Exception e) {
                        // some action

                        if (currentPic.exists()) {
                            currentPic.delete();
                        }
                        picasso.load(QldOrSaPhotosList.get(outerItemPosition).getUrl())
                                .into(holder.photoDateViseImageView);
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    picasso.load(QldOrSaPhotosList.get(outerItemPosition).getUrl())
                            .into(holder.photoDateViseImageView);
                }


                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            picasso.load(QldOrSaPhotosList.get(outerItemPosition).getUrl())
                    .into(target);
        }*/

        holder.favPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (newApi) {*/
                boolean fav = QldOrSaPhotosList.get(outerItemPosition).isFav();

                myPlaceDataBase.setFavQldSa(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), QldOrSaPhotosList.get(outerItemPosition).getUrlInt(), !fav);
                if (isFav) {
                    controller.getAnalytics().favPhotosFavSymbolTouchEvent();
                    QldOrSaPhotosList.remove(outerItemPosition);
                    if (QldOrSaPhotosList.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                        fragmentCallBack.actionbarTextChange("My Favourites");
                    } else {

                        fragmentCallBack.actionbarTextChange("My Favourites ( " + Integer.toString(QldOrSaPhotosList.size()) + " )");
                    }
                } else {
                    controller.getAnalytics().photosFavSymbolTouchEvent();
                    QldOrSaPhotosList.get(outerItemPosition).setFav(!fav);
                    if (QldOrSaPhotosList.get(outerItemPosition).isFav()) {
                        holder.favPhotoImageView.setImageResource(R.drawable.fav_selected);
                    } else {
                        holder.favPhotoImageView.setImageResource(R.drawable.fav_orange_borber);
                    }
                }
                notifyDataSetChanged();

            }
        });

        holder.notesSaveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    controller.getAnalytics().favPhotosAddButtonTouchEvent();
                    controller.setCameFromFav(true);

                } else {
                    controller.getAnalytics().photosAddButtonTouchEvent();
                    controller.setCameFromFav(false);
                }
                /*if (newApi) {*/
                controller.setSelectedFavPhotoUrlInt(QldOrSaPhotosList.get(outerItemPosition).getUrlInt());
                controller.setSelectedPhotoNotes(QldOrSaPhotosList.get(outerItemPosition).getNotes());
                controller.setSelectedPhoto(Integer.toString(QldOrSaPhotosList.get(outerItemPosition).getUrlInt()));
                /*} else {
                    controller.setSelectedPhotoNotes(myPlacePhotosDetailsDataSets.get(outerItemPosition).getNotes());
                    controller.setSelectedPhoto(myPlacePhotosDetailsDataSets.get(outerItemPosition).getImagePath());
                    controller.setSelectedFavPhotoUrlInt(myPlacePhotosDetailsDataSets.get(outerItemPosition).getId());
                }*/
                controller.setSelectedPhotoPosition(outerItemPosition);
                fragmentCallBack.actionbarTextChange("Add Notes");
                Fragment fragment = new MyPhotosAddNotes();
                FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
            }
        });
        System.gc();

        return rowView;

    }


    public class Holder {
        PhotoView photoDateViseImageView;
        CardView cardView;
        ImageView zoomPhotoImageView, favPhotoImageView;
        TextView notesSaveTextView, notesTextView, photoDescTextView;
        ProgressBar progress;
    }

    public void positionOnClick(String url) {
        String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
        byte[] message = sample.getBytes();
        final String encoded = Base64.encodeToString(message, Base64.NO_WRAP);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View mView = inflater.inflate(R.layout.zoomable_image_dailog, null);
        PhotoView photoView = (PhotoView) mView.findViewById(R.id.zoomImageView);
        dialog.show();
        picasso.load(url).into(photoView, new Callback() {
            @Override
            public void onSuccess() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
            @Override
            public void onError(Exception e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }


        });

        ImageView imageView = (ImageView) mView.findViewById(R.id.zoomCloseImageView);
        mBuilder.setView(mView);
        photoView.bringToFront();
        imageView.bringToFront();
        final AlertDialog mDialog = mBuilder.create();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });
        mDialog.show();
    }
}