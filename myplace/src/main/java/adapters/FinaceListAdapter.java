package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.dmss.burbankappold.R;
import models.ContractFlowDataset;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class FinaceListAdapter extends BaseAdapter {
    Activity act;
    ArrayList<ContractFlowDataset> data;
    private static LayoutInflater inflater = null;
    String contractValue;
    double summ = 0.0;

    public FinaceListAdapter(Activity act, ArrayList<ContractFlowDataset> data, String contractValue) {
        // TODO Auto-generated constructor stub
        this.act = act;
        this.data = data;
        this.contractValue = contractValue;
        inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        LinearLayout table;
        TextView header, approvedVariationName, approvedVariationValue, adjustedContractName, adjustedContractValue;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        DecimalFormat df2 = new DecimalFormat(".##");
        rowView = inflater.inflate(R.layout.financerow, null);
        holder.header = (TextView) rowView.findViewById(R.id.header);
        holder.approvedVariationName = (TextView) rowView.findViewById(R.id.approvedVariationname);
        holder.approvedVariationValue = (TextView) rowView.findViewById(R.id.approvedVariationvalue);
        holder.adjustedContractName = (TextView) rowView.findViewById(R.id.adjustedcontractname);
        holder.adjustedContractValue = (TextView) rowView.findViewById(R.id.adjustedcontractvalue);
        holder.table = (LinearLayout) rowView.findViewById(R.id.table);
        holder.header.setText(data.get(position).getHeaderName());

        switch (position) {
            case 0:
                double sum = 0.00;

                for (int i = 0; i < data.get(position).getContractFlow().size(); i++) {
                    View view = inflater.inflate(R.layout.financetablerow, null);
                    TextView name = (TextView) view.findViewById(R.id.name);
                    TextView value = (TextView) view.findViewById(R.id.value);
                    name.setText(data.get(position).getContractFlow().get(i).getName());
                    Double val = Double.parseDouble(data.get(position).getContractFlow().get(i).getValue());
                    value.setText("$" + String.format("%,.2f", val));
                    //value.setText("$"+data.get(position).getContractFlow().get(i).getValue());
                    sum = sum + Double.parseDouble(data.get(position).getContractFlow().get(i).getValue());
                    holder.table.addView(view);
                }
                double newContractValue = Double.parseDouble(contractValue) + sum;
                String numberAsString = String.format("%,.2f", sum);
                String test = df2.format(sum);
                holder.approvedVariationValue.setText("$" + String.format("%,.2f", sum));
                holder.adjustedContractValue.setText("$" + String.format("%,.2f", newContractValue));

                //holder.approvedVariationValue.setText("$"+sum);
                //holder.adjustedContractValue.setText("$"+newContractValue);
                break;
            case 1:
                summ = 0.00;
                holder.approvedVariationName.setText("Total Amount Claimed : ");
                holder.adjustedContractValue.setVisibility(View.GONE);
                holder.adjustedContractName.setVisibility(View.GONE);
                for (int i = 0; i < data.get(position).getContractFlow().size(); i++) {
                    View view = inflater.inflate(R.layout.financetablerow, null);
                    TextView name = (TextView) view.findViewById(R.id.name);
                    TextView value = (TextView) view.findViewById(R.id.value);
                    name.setText(data.get(position).getContractFlow().get(i).getName());
                    Double val1 = Double.parseDouble(data.get(position).getContractFlow().get(i).getValue());
                    value.setText("$" + String.format("%,.2f", val1));
                    // value.setText("$"+data.get(position).getContractFlow().get(i).getValue());
                    summ = summ + Double.parseDouble(data.get(position).getContractFlow().get(i).getValue());
                    holder.table.addView(view);
                }

                holder.approvedVariationValue.setText("$" + String.format("%,.2f", summ));
                //holder.approvedVariationValue.setText("$"+summ);
                break;
            case 2:
                holder.adjustedContractValue.setVisibility(View.VISIBLE);
                holder.adjustedContractName.setVisibility(View.VISIBLE);
                holder.approvedVariationName.setText("Total Amount Received : ");
                holder.adjustedContractName.setText("Balance Due : ");
                double receiptSum = 0.0;
                for (int i = 0; i < data.get(position).getContractFlow().size(); i++) {
                    View view = inflater.inflate(R.layout.financetablerow, null);
                    TextView name = (TextView) view.findViewById(R.id.name);
                    TextView value = (TextView) view.findViewById(R.id.value);
                    name.setText(data.get(position).getContractFlow().get(i).getName());

                    Double val2 = Double.parseDouble(data.get(position).getContractFlow().get(i).getValue());
                    value.setText("$" + String.format("%,.2f", val2));

                    //value.setText("$"+data.get(position).getContractFlow().get(i).getValue());
                    receiptSum = receiptSum + Double.parseDouble(data.get(position).getContractFlow().get(i).getValue());
                    holder.table.addView(view);
                }
                double val = summ - receiptSum;

                holder.approvedVariationValue.setText("$" + String.format("%,.2f", receiptSum));
                holder.adjustedContractValue.setText("$" + String.format("%,.2f", val));

                // holder.approvedVariationValue.setText("$"+receiptSum);
                // holder.adjustedContractValue.setText("$"+val);
                break;
        }

        return rowView;
    }
}

