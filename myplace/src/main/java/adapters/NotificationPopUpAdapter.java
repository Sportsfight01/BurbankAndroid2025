package adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import common.AppController;
import com.dmss.burbankappold.R;
import interfaces.Notification_Callback;
import models.Co_Burbank_Model;

/**
 * Created by Ashish.Kumar on 15-06-2017.
 */

public class NotificationPopUpAdapter extends BaseAdapter {
    ArrayList<Co_Burbank_Model> notificationList;
    Activity act;
    AppController controller;
    LayoutInflater inflater;
    Notification_Callback callback;
    public NotificationPopUpAdapter(ArrayList<Co_Burbank_Model> notificationList, Activity act) {
        this.notificationList = notificationList;
        this.act = act;
        controller=(AppController) act.getApplicationContext();
        inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        callback=(Notification_Callback)act;
    }
    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        final Co_Burbank_Model model =notificationList.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.nitifiaction_row, null);
            holder.message = (TextView) convertView.findViewById(R.id.message);

            holder.reject = (Button) convertView.findViewById(R.id.reject);
            holder.accept = (Button) convertView.findViewById(R.id.accept);
            holder.bottom=(View) convertView.findViewById(R.id.bottomView);

        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.message.setText(model.getStatusMessage());
        holder.message.setTypeface(controller.getTypeface());
        holder.accept.setTypeface(controller.getTypeface());
        holder.reject.setTypeface(controller.getTypeface());
        if(notificationList.size()==1)
        {
            holder.bottom.setBackgroundColor(act.getResources().getColor(R.color.white));
            if(model.isCoBurbank())
            { holder.bottom.setVisibility(View.VISIBLE);}else{
                holder.bottom.setVisibility(View.GONE);
            }

        }else{
            holder.bottom.setBackgroundColor(act.getResources().getColor(R.color.lightGrey));
            holder.bottom.setVisibility(View.VISIBLE);
        }
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.isCoBurbank()==false) {
                    callback.onAccept(position);
                }
            }
        });
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.isCoBurbank()==false) {
                    callback.onReject(position);
                }
            }
        });
        if(model.isCoBurbank())
        {
            holder.reject.setVisibility(View.GONE);
            holder.accept.setText("Co Burbank");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                holder.accept.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.grey_btn_fullcolor));
            } else {
                holder.accept.setBackground(ContextCompat.getDrawable(act, R.drawable.grey_btn_fullcolor));
            }
        }else{
            holder.reject.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                holder.accept.setBackgroundDrawable(ContextCompat.getDrawable(act, R.drawable.orange_btn_fullcolor));
            } else {
                holder.accept.setBackground(ContextCompat.getDrawable(act, R.drawable.orange_btn_fullcolor));
            }
        }
        convertView.setTag(holder);
        return convertView;
    }

    public class Holder {
View bottom;
        TextView message;
        Button reject,accept;
    }

}
