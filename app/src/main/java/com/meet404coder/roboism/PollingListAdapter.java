package com.meet404coder.roboism;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PollingListAdapter extends BaseAdapter {

    private Context context;
    private String name[];
    private String desc[];
    private LayoutInflater inflater;

    public PollingListAdapter(Context context, String name[], String desc[]) {
        this.context = context;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public int getCount() {
        return name.length;
    }

    @Override
    public Object getItem(int position) {
        return name[position];

    }

    @Override
    public long getItemId(int position) {
        return position;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listView = inflater.inflate(R.layout.listview_polling_item, null);
        }
        ImageView icon = (ImageView) listView.findViewById(R.id.list_img);
        TextView txt_name = (TextView) listView.findViewById(R.id.list_name);
        TextView txt_desc = (TextView) listView.findViewById(R.id.list_desc);

        txt_name.setText(name[position]);
        String opts[] = desc[position].split(":");
        String OptionsFormatted = "";
        for(int i=0;i<opts.length;i++){
            OptionsFormatted += "("+(i+1)+") "+opts[i]+"\n";
        }
        txt_desc.setText(OptionsFormatted);

        return listView;
    }
}
