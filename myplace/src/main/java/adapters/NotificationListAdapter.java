package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.dmss.burbankappold.R;
import models.NotificationModel;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class NotificationListAdapter extends BaseAdapter {
    private ArrayList<NotificationModel> notificationModels;
    Context context;

    public NotificationListAdapter(Context context, ArrayList<NotificationModel> notificationModels) {
        super();
        this.notificationModels = notificationModels;
        this.context = context;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return notificationModels.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return notificationModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return notificationModels.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = convertView;
        NotificationModel notificationModel = notificationModels.get(position);
        LayoutInflater lif = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        /***
         * Checking if View is null if not We will display View directly.
         */
        if (convertView == null) {
            /****** Inflate contacts_list_item.xml file for each row ( Defined below ) *******/
            view = lif.inflate(R.layout.notification_list_item, null);

        } else {
            view = convertView;
        }
        TextView notificationDataTextView = (TextView) view.findViewById(R.id.notificationDataTextView);
        TextView notificationTimeTextView = (TextView) view.findViewById(R.id.notificationTimeTextView);
        LinearLayout notificationLayout = (LinearLayout) view.findViewById(R.id.notificationLayout);
        ImageView notificationImageView = (ImageView) view.findViewById(R.id.notificationImageView);

      /*  if (notificationModel.isRead()) {
            notificationLayout.setBackgroundResource(R.color.white);
            notificationTimeTextView.setTextColor(context.getResources().getColor(R.color.headingGrey));
        } else {
            notificationLayout.setBackgroundResource(R.color.bg_color);
            notificationTimeTextView.setTextColor(context.getResources().getColor(R.color.bg_text_color));
        }*/

        String displayString = "";
        if (notificationModel.isPhoto()) {
            displayString = "Check out the new photos added on " + notificationModel.getDate();
            notificationImageView.setImageResource(R.drawable.image);
        } else {
            if (notificationModel.isStage()) {
                displayString = "\"" + notificationModel.getHeading() + "\" changed from \"" + notificationModel.getOldStage() + "\" to \" " + notificationModel.getCurrentStage() + "\" on " + notificationModel.getDate() + ".";
            } else {
                displayString = "\"" + notificationModel.getHeading() + "\" Completed on " + notificationModel.getDate() + ".";
            }
            notificationImageView.setImageResource(R.drawable.notification_filled);
        }
        notificationTimeTextView.setText(notificationModel.getDaysTag());
        notificationDataTextView.setText(displayString);

        return view;
    }

}
