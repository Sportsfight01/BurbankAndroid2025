package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.dmss.burbankappold.R;
import models.MyPhotosNewDateWiseDataSet;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class MyPlacePhotosListAdapter extends BaseAdapter {

    Activity context;
    ArrayList<MyPhotosNewDateWiseDataSet> newDateWiseDataSets = new ArrayList<MyPhotosNewDateWiseDataSet>();
    int height;
    boolean newApi = false;


    public MyPlacePhotosListAdapter(Activity context, ArrayList<MyPhotosNewDateWiseDataSet> newDateWiseDataSets, int height, boolean newApi) {
        this.context = context;
        this.newDateWiseDataSets = newDateWiseDataSets;
        this.height = height;
        this.newApi = newApi;
    }

    @Override
    public int getCount() {

        return newDateWiseDataSets.size();

    }

    @Override
    public Object getItem(int position) {

        return newDateWiseDataSets.get(position);

    }

    @Override
    public long getItemId(int position) {

        return newDateWiseDataSets.get(position).hashCode();

    }

    @Override
    public View getView(final int outerItemPosition, View convertView, ViewGroup parent) {

        final Holder holder = new Holder();
        View rowView = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.photos_list_item, null);
        } else {
            rowView = convertView;
        }
        holder.photoDateTextView = (TextView) rowView.findViewById(R.id.photoDateTextView);
        holder.photoMonthTextView = (TextView) rowView.findViewById(R.id.photoMonthTextView);
        holder.photoDayTextView = (TextView) rowView.findViewById(R.id.photoDayTextView);
        holder.photoGridView = (GridView) rowView.findViewById(R.id.photoGridView);


        final MyPhotosNewDateWiseDataSet newDateWiseDataSet = newDateWiseDataSets.get(outerItemPosition);
        int numberOfRows = 0;
        int size = newDateWiseDataSet.getQldOrSaPhotosList().size();
        if (size % 4 == 0) {
            numberOfRows = size / 4;
        } else {
            numberOfRows = (size / 4) + 1;
        }
        LinearLayout.LayoutParams paramss = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height * numberOfRows);
        holder.photoGridView.setLayoutParams(paramss);

        MyPlacePhotoGridAdapter myPlacePhotoGridAdapter = new MyPlacePhotoGridAdapter(context, newDateWiseDataSets, newDateWiseDataSet.getQldOrSaPhotosList(), height, outerItemPosition, false);
        holder.photoGridView.setAdapter(myPlacePhotoGridAdapter);

        holder.photoDateTextView.setText(newDateWiseDataSet.getDate());
        holder.photoMonthTextView.setText(newDateWiseDataSet.getDay());
        holder.photoDayTextView.setText(newDateWiseDataSet.getDay());


        return rowView;

    }


    public class Holder {
        TextView photoDateTextView, photoMonthTextView, photoDayTextView;
        GridView photoGridView;
    }
}