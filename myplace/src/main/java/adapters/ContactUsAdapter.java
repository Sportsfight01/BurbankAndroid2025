package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.dmss.burbankappold.R;
import models.ContactUsModel;

/**
 * Created by Jaya.Krishna on 09-01-2018.
 */

public class ContactUsAdapter extends BaseAdapter {
    private ArrayList<ContactUsModel> contactsModels;
    Context context;

    public ContactUsAdapter(Context context, ArrayList<ContactUsModel> contactsModels) {
        super();
        this.contactsModels = contactsModels;
        this.context = context;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return contactsModels.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return contactsModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return contactsModels.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = convertView;
        ContactUsModel contactsModel = contactsModels.get(position);
        LayoutInflater lif = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        /***
         * Checking if View is null if not We will display View directly.
         */
        if (convertView == null) {
            /****** Inflate contacts_list_item.xml file for each row ( Defined below ) *******/
            view = lif.inflate(R.layout.contactus_notes_list_item, null);

        } else {
            view = convertView;
        }
        TextView notesHeadingTextView = (TextView) view.findViewById(R.id.notesHeadingTextView);
        TextView notesDateTextView = (TextView) view.findViewById(R.id.notesDateTextView);
        TextView notesDetailsTextView = (TextView) view.findViewById(R.id.notesDetailsTextView);
        notesHeadingTextView.setText(contactsModel.getSubject());
        notesDateTextView.setText("By "+contactsModel.getAuthorName()+" on "+contactsModel.getDisplayDate());
        notesDetailsTextView.setText(contactsModel.getBody());
        return view;
    }

}