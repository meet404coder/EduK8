package com.meet404coder.roboism;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CreditDetailsListAdapter extends BaseAdapter {

    private Context context;
    private List<MessOffData> messOffDataList;

    private LayoutInflater inflater;

    public CreditDetailsListAdapter(Context context, List<MessOffData> messOffData) {
        this.context = context;
        this.messOffDataList = messOffData;
    }

    @Override
    public int getCount() {
        return messOffDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return messOffDataList.get(position).toString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;

        MessOffData messOffData = messOffDataList.get(position);

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listView = inflater.inflate(R.layout.listitem_db_creditdetails, null);
        }
        TextView txt_date = (TextView) listView.findViewById(R.id.list_date);
        TextView txt_amt = (TextView) listView.findViewById(R.id.list_amt);
        TextView txt_messOffData = (TextView) listView.findViewById(R.id.list_mess_off_data);


        txt_date.setText(messOffData.date.toString());
        txt_amt.setText("+"+messOffData.creditPending.toString());

        String  messOffDataString="[ ";

        int fb = 0, fl = 0, fs = 0, fd = 0;
        fb = Integer.parseInt("" + messOffData.messOfData.toString().charAt(0));
        fl = Integer.parseInt("" + messOffData.messOfData.toString().charAt(1));
        fs = Integer.parseInt("" + messOffData.messOfData.toString().charAt(2));
        fd = Integer.parseInt("" + messOffData.messOfData.toString().charAt(3));

        if(fb == 1){
            messOffDataString += "B ";
        }if(fl == 1){
            messOffDataString += "L ";
        }if(fs == 1){
            messOffDataString += "S ";
        }if(fd == 1){
            messOffDataString += "D";
        }

        messOffDataString += "]";

        txt_messOffData.setText(messOffDataString);

        return listView;
    }
}
