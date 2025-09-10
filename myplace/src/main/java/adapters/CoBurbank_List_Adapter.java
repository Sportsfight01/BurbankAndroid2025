package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import common.AppController;
import com.dmss.burbankappold.R;
import interfaces.Co_Burbank_Delete_Reinvite_Callback;
import models.Co_Burbank_Model;

/**
 * Created by Ashish.Kumar on 13-06-2017.
 */

public class CoBurbank_List_Adapter extends BaseAdapter {
    Activity act;
    ArrayList<Co_Burbank_Model> data;
    Co_Burbank_Delete_Reinvite_Callback callBack;
    private static LayoutInflater inflater = null;
    AppController controller;

    public CoBurbank_List_Adapter(Activity act, ArrayList<Co_Burbank_Model> data) {
        // TODO Auto-generated constructor stub
        this.act = act;
        this.data = data;
        controller = (AppController) act.getApplicationContext();
        inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        callBack = (Co_Burbank_Delete_Reinvite_Callback) act;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {

        TextView userName, userEmail,details;
        TextView primary, accept;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        final Co_Burbank_Model model = data.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_share_with_partner, null);
            holder.userName = (TextView) convertView.findViewById(R.id.userName);
            holder.userEmail = (TextView) convertView.findViewById(R.id.userEmail);
            holder.details = (TextView) convertView.findViewById(R.id.userdesc);
            holder.primary = (TextView) convertView.findViewById(R.id.tv_primary);
            holder.accept = (TextView) convertView.findViewById(R.id.tv_accept);

        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.userName.setText(model.getName());
        if ((model.getName() == null) || (model.getName().equalsIgnoreCase("null"))) {
            holder.userName.setVisibility(View.GONE);
        } else {
            holder.userName.setVisibility(View.VISIBLE);
            holder.userName.setText(model.getName());
        }

        if (model.isPrimaryApplicant()) {
            holder.primary.setText("Primary Applicant");
            holder.userEmail.setText(model.getEmail());
            if (model.getStatusMessage() != null){
                holder.details.setText(model.getStatusMessage());
            }
            holder.primary.setBackground(ContextCompat.getDrawable(act, R.drawable.circuler_drawable_partner));
        } else if (model.isCoBurbank()) {
            holder.primary.setText("Partner");
            holder.userEmail.setText(model.getEmail());
            if (model.getStatusMessage() != null){
                holder.details.setText(model.getStatusMessage());
            }
            holder.primary.setBackground(ContextCompat.getDrawable(act, R.drawable.circuler_drawable_partner));

        } else if (model.isRejected()) {
            holder.primary.setText("Rejected");
            holder.userEmail.setText(model.getEmail());
            holder.details.setText(model.getStatusMessage());
            holder.primary.setBackground(ContextCompat.getDrawable(act, R.drawable.circuler_drawable_partner));
        } else if (model.isCanInvite()) {
            holder.primary.setBackground(ContextCompat.getDrawable(act, R.drawable.circuler_invite_drawable));
            holder.userEmail.setText(model.getEmail());
            holder.primary.setText("Invite");
            holder.details.setText(model.getStatusMessage());
        } else if (model.isCanReInvite()) {
            holder.primary.setBackground(ContextCompat.getDrawable(act, R.drawable.circuler_invite_drawable));
            holder.primary.setText("Re-Invite");
            holder.userEmail.setText(model.getEmail());
            holder.details.setText(model.getStatusMessage());
        } else if (model.isInvited()) {
            holder.primary.setBackground(ContextCompat.getDrawable(act, R.drawable.circuler_drawable_partner));
            holder.primary.setText("Invited");
            holder.userEmail.setText(model.getEmail());
            holder.details.setText(model.getStatusMessage());
        } else if (model.isReffered()) {
            holder.primary.setBackground(ContextCompat.getDrawable(act, R.drawable.circuler_drawable_partner));
            holder.primary.setText("Referred");
            holder.userEmail.setText(model.getEmail());
            holder.details.setText(model.getStatusMessage());
        }

        if (model.isCanDelete()) {
            holder.accept.setVisibility(View.VISIBLE);
            holder.accept.setText("Delete");
            holder.accept.setBackground(ContextCompat.getDrawable(act, R.drawable.circuler_delete_drawable));
            holder.accept.setTypeface(controller.getTypeface());
        } else {
            holder.accept.setVisibility(View.GONE);
        }


        convertView.setTag(holder);
        holder.primary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((model.isCanReInvite() || (model.isCanInvite()))) {

                    controller.getAnalytics().moreFeaturesShareWithPartnerInviteButtonTouchEvent();
                    callBack.reInvite(position);
                }
            }
        });
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.isCanDelete()) {
                    controller.getAnalytics().moreFeaturesShareWithPartnerDeleteButtonTouchEvent();
                    callBack.delete(position);
                }
            }
        });
        return convertView;
    }
}
