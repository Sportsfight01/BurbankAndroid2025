package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.dmss.burbankappold.R;
import models.ContactsModel;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class ContactsListAdapter extends BaseAdapter {
    private ArrayList<ContactsModel> contactsModels;
    Context context;

    public ContactsListAdapter(Context context, ArrayList<ContactsModel> contactsModels) {
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
        ContactsModel contactsModel = contactsModels.get(position);
        LayoutInflater lif = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        /***
         * Checking if View is null if not We will display View directly.
         */
        if (convertView == null) {
            /****** Inflate contacts_list_item.xml file for each row ( Defined below ) *******/
            view = lif.inflate(R.layout.contacts_list_item, null);

        } else {
            view = convertView;
        }
        TextView contactNameTextView = (TextView) view.findViewById(R.id.contactNameTextView);
        TextView contactNumber = (TextView) view.findViewById(R.id.contactNumber);
        contactNameTextView.setText(contactsModel.getContactName());
        contactNumber.setText(contactsModel.getContactNumber());

        return view;
    }

}