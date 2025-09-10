package adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import common.AppController;

import com.dmss.burbankappold.R;
import com.dmss.burbankappold.fragments.ProgressOverView;

import models.AdministartionDataForVic;
import models.AdministrationDataForQldOrSA;
import models.NotificationModel;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class ProgressList_Adapter extends BaseAdapter {

    /***********
     * Declaration Used Variables
     *********/
    private final LayoutInflater inflater;
    ArrayList<AdministartionDataForVic> VICdata = new ArrayList<AdministartionDataForVic>();
    ArrayList<AdministrationDataForQldOrSA> QldOrSAData = new ArrayList<AdministrationDataForQldOrSA>();
    Activity act;
    boolean regionVIC;
    AppController controller;
    NotificationModel notificationModel;

    /**
     * CustomAdapter Constructor
     *
     * @param act     A variable of type Activity.
     * @param VICdata A variable of type ArrayList<ProgressViewModelClass>.
     **/
    public ProgressList_Adapter(Activity act, ArrayList<AdministartionDataForVic> VICdata) {
        inflater = LayoutInflater.from(act);
        this.VICdata = VICdata;
        this.regionVIC = true;
        this.controller = (AppController) act.getApplicationContext();
    }


    public ProgressList_Adapter(Activity act, ArrayList<AdministrationDataForQldOrSA> QldOrSAData, NotificationModel notificationModel, boolean regionVIC) {
        inflater = LayoutInflater.from(act);
        this.QldOrSAData = QldOrSAData;
        this.regionVIC = false;
        this.notificationModel = notificationModel;
        this.controller = (AppController) act.getApplicationContext();
    }

    /**
     * Retrieve the size of the array list passed to the adapter.
     *
     * @return A Integer data type.
     */
    @Override
    public int getCount() {
        if (regionVIC) {
            return VICdata.size();
        } else {
            return QldOrSAData.size();
        }

    }

    /**
     * Retrieve the item from array list passed to the adapter.
     *
     * @param position A variable of type Integer.
     * @return A Integer data type.
     */
    @Override
    public Object getItem(int position) {
        if (regionVIC) {
            return VICdata.get(position);
        } else {
            return QldOrSAData.get(position);
        }
    }

    /**
     * Retrieve the item id from array list passed to the adapter.
     *
     * @param position A variable of type Integer.
     * @return A Integer data type.
     */
    @Override
    public long getItemId(int position) {
        if (regionVIC) {
            return VICdata.get(position).hashCode();
        } else {
            return QldOrSAData.get(position).hashCode();
        }
    }

    /**
     * Depends upon data size, called for each row , Creates each ListView row
     *
     * @param position A variable of type Integer.
     * @param v        A variable of type View.
     * @param parent   A variable of type ViewGroup.
     * @return view of listView row
     **/
    @Override
    public View getView(int position, View v, ViewGroup parent) {
        View convertView = null;
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.custom_list_progressview, parent, false);
            final LinearLayout progressItemLayout = (LinearLayout) convertView.findViewById(R.id.progressItemLayout);

            progressItemLayout.post(new Runnable() {
                @Override
                public void run() {
                    int size = progressItemLayout.getHeight();
                }
            });

            holder.mTxtProgressItem = (TextView) convertView.findViewById(R.id.mTxtProgressItem);
            holder.mTxtProgressDate = (TextView) convertView.findViewById(R.id.mTxtProgressDate);
            holder.mImgStatus = (ImageView) convertView.findViewById(R.id.mImgStatus);
            holder.dateHeading = (TextView) convertView.findViewById(R.id.completionheading);

            /*holder.mTxtProgressItem.setTypeface(controller.getTypefaceProximoNova());
            holder.mTxtProgressDate.setTypeface(controller.getTypefaceProximoNova());
            holder.dateHeading.setTypeface(controller.getTypefaceProximoNova());*/


            if (regionVIC) {
                AdministartionDataForVic adminData = VICdata.get(position);
                holder.mTxtProgressItem.setText(adminData.getItemName());
                if (adminData.getDateCompleted().length() > 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                    SimpleDateFormat mdyFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        String dt = adminData.getDateCompleted().split("T")[0];
                        Date complitionDate = sdf.parse(dt);
                        holder.mTxtProgressDate.setText(mdyFormat.format(complitionDate));
                    } catch (Exception ex) {

                    }
                } else {
                    holder.mTxtProgressDate.setText("");
                }
                if (ProgressOverView.isTaskCompeted(adminData.getDateCompleted())) {
                    holder.mImgStatus.setImageResource(R.drawable.icon_prgcompleted);
                } else {
                    holder.mImgStatus.setImageResource(R.drawable.icon_prgpending);
                }
                holder.dateHeading.setVisibility(View.GONE);
            } else {
                AdministrationDataForQldOrSA administrationDataForQldOrSA = QldOrSAData.get(position);
                if (notificationModel != null && administrationDataForQldOrSA != null && administrationDataForQldOrSA.getName() != null) {
                    if (notificationModel.getHeading() != null && notificationModel.getHeading().trim().equalsIgnoreCase(administrationDataForQldOrSA.getName().trim())) {
                        progressItemLayout.setBackgroundColor(ContextCompat.getColor(controller.getContext(),R.color.headingGrey));
                        holder.mImgStatus.startAnimation(AnimationUtils.loadAnimation(controller.getContext(), R.anim.zoom));
                        /*RotateAnimation r;
                        r = new RotateAnimation(0.0f, -10.0f * 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        holder.mImgStatus.startAnimation(r);*/
                    }
                }


                holder.mTxtProgressItem.setText(administrationDataForQldOrSA.getName());
                if (administrationDataForQldOrSA.getDateActual().length() > 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                    SimpleDateFormat mdyFormat = new SimpleDateFormat("dd MMM yyyy");
                    try {
                        String dt = administrationDataForQldOrSA.getDateActual().split("T")[0];
                        Date complitionDate = sdf.parse(dt);
                        holder.mTxtProgressDate.setText(mdyFormat.format(complitionDate));
                    } catch (Exception ex) {

                    }
                } else {
                    holder.mTxtProgressDate.setText("");
                }
                if (ProgressOverView.isTaskCompeted(administrationDataForQldOrSA.getDateActual())) {
                    if (administrationDataForQldOrSA.getStatus().equalsIgnoreCase("Completed")) {
                        holder.dateHeading.setText("Completed");
                        holder.dateHeading.setVisibility(View.VISIBLE);
                        holder.mTxtProgressDate.setVisibility(View.VISIBLE);
                        holder.mImgStatus.setImageResource(R.drawable.icon_prgcompleted);
                    } else {
                        holder.dateHeading.setVisibility(View.GONE);
                        holder.mTxtProgressDate.setVisibility(View.GONE);
                        holder.mImgStatus.setImageResource(R.drawable.icon_prgpending);
                    }

                } else {
                    holder.dateHeading.setVisibility(View.GONE);
                    holder.mTxtProgressDate.setVisibility(View.GONE);
                    holder.mImgStatus.setImageResource(R.drawable.icon_prgpending);
                }
                /*if (administrationDataForQldOrSA.getStatus().equalsIgnoreCase("Completed")) {

                } else {
                    holder.dateHeading.setText(administrationDataForQldOrSA.getStatus());
                    holder.dateHeading.setVisibility(View.VISIBLE);
                }*/
            }


        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setTag(holder);
        return convertView;
    }

    private class ViewHolder {
        public TextView mTxtProgressItem, mTxtProgressDate, dateHeading;
        public int position;
        public ImageView mImgStatus;
    }
}
