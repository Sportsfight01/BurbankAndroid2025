package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.dmss.burbankappold.R;
import models.MyPlaceJobDetails;

/**
 * Created by jaya.krishna on 08-06-2017.
 */

public class JobsSpinnerAdapter extends BaseAdapter {
    private ArrayList<MyPlaceJobDetails> myPlaceJobDetailses;
    Context context;

    public JobsSpinnerAdapter(Context context, ArrayList<MyPlaceJobDetails> myPlaceJobDetailses) {
        super();
        this.myPlaceJobDetailses = myPlaceJobDetailses;
        this.context = context;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return myPlaceJobDetailses.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return myPlaceJobDetailses.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return myPlaceJobDetailses.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        MyPlaceJobDetails myPlaceJobDetails = myPlaceJobDetailses.get(position);
        LayoutInflater lif = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        /***
         * Checking if View is null if not We will display View directly.
         */
        View view = convertView;
        if (convertView == null) {
            /****** Inflate contacts_list_item.xml file for each row ( Defined below ) *******/
            view = lif.inflate(R.layout.jobnumber_spinner_item, null);

        }
        TextView jobNumbersSpinnerTextView = (TextView) view.findViewById(R.id.jobNumbersSpinnerTextView);
        ImageView spinnerSelectedImageView = (ImageView) view.findViewById(R.id.spinnerSelectedImageView);
        jobNumbersSpinnerTextView.setText(myPlaceJobDetails.getJobNo() + " (" + myPlaceJobDetails.getRegion() + ") ");

        if (myPlaceJobDetailses.size() > 1 && myPlaceJobDetails.isSelected()) {
            spinnerSelectedImageView.setVisibility(View.VISIBLE);
        } else {
            spinnerSelectedImageView.setVisibility(View.GONE);
        }


        return view;
    }

}