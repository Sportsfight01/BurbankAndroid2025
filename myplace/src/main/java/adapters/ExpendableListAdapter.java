package adapters;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.List;

import common.JustifiedTextView;
import com.dmss.burbankappold.R;
import interfaces.ExpendableListCallBack;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class ExpendableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, String> _listDataChild;
    ExpendableListCallBack callBack;
    public ExpendableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, String> listChildData) {
        this._context = context;
        this._listDataChild = listChildData;
        this._listDataHeader = listDataHeader;
        callBack=(ExpendableListCallBack)context;
    }

    @Override
    public int getGroupCount() {
        return _listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return _listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return _listDataChild.get(_listDataHeader.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.faq_header, null);
        }
        TextView questionNo= convertView
                .findViewById(R.id.questionNo);
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);

        final LinearLayout ll_parent = convertView
                .findViewById(R.id.ll_parent);
        int pos=groupPosition+1;
       // questionNo.setText("Q"+pos+" :");
        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_parent.setBackgroundColor(ContextCompat.getColor(_context,R.color.fifty_opacity_orange));
            }
        });*/
        /*lblListHeader.setText(headerTitle.trim());*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            lblListHeader.setText(Html.fromHtml(headerTitle.trim(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            lblListHeader.setText(Html.fromHtml(headerTitle.trim()));
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.faq_content, null);
        }

        JustifiedTextView txtListChild = (JustifiedTextView) convertView.findViewById(R.id.lblListItem);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtListChild.setText(Html.fromHtml(childText, Html.FROM_HTML_MODE_COMPACT));
        } else {
            txtListChild.setText(Html.fromHtml(childText));
        }
        //txtListChild.setText(childText);
        /*txtListChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onClick(groupPosition);
            }
        });*/
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
