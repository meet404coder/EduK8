package com.meet404coder.roboism;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MeetingListAdapter extends BaseAdapter {

    private Context context;
    private List<MeetData> meetData;
    private LayoutInflater inflater;

    public MeetingListAdapter(Context context, List<MeetData> meetData) {
        this.context =   context;
        this.meetData = meetData;

    }

    @Override
    public int getCount() {
        return meetData.size();
    }

    @Override
    public Object getItem(int position) {
        return meetData.get(position);

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
            listView = inflater.inflate(R.layout.listview_meeting_item, null);
        }

        final int pos = position;

        final TextView txt_date       = (TextView) listView.findViewById(R.id.list_date           );
        final TextView txt_time_from  = (TextView) listView.findViewById(R.id.list_time_from      );
        final TextView txt_time_to    = (TextView) listView.findViewById(R.id.list_time_to        );
        final TextView txt_venue      = (TextView) listView.findViewById(R.id.list_venue          );
        final TextView txt_agenda     = (TextView) listView.findViewById(R.id.list_agenda         );
        final TextView txt_rem        = (TextView) listView.findViewById(R.id.list_remarks        );
        final TextView txt_al_min     = (TextView) listView.findViewById(R.id.list_access_lvl_min );
        final TextView txt_al_max     = (TextView) listView.findViewById(R.id.list_access_lvl_max );
        final TextView txt_al_iss_by  = (TextView) listView.findViewById(R.id.list_issued_by      );

        txt_date     .setText(""+meetData.get(position).meet_date);
        txt_time_from.setText(""+meetData.get(position).meet_time_from);
        txt_time_to  .setText(""+meetData.get(position).meet_time_to);
        txt_venue    .setText(""+meetData.get(position).meet_venue);
        txt_agenda   .setText(""+meetData.get(position).agenda);
        txt_rem      .setText(""+meetData.get(position).remarks);
        txt_al_min   .setText(""+meetData.get(position).min_acc_lvl);
        txt_al_max   .setText(""+meetData.get(position).max_acc_lvl);

        DatabaseReference memberProfileRef = FirebaseDatabase.getInstance().getReference(Config.MemberProfileRef);
        memberProfileRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserProfile tempprofile = dataSnapshot.getValue(UserProfile.class);

                //System.out.println(">>>>Adapter Profile Name:" + tempprofile.toString());
                if(tempprofile.uid.equals(meetData.get(pos).uid)){
                    txt_al_iss_by.setText(tempprofile.name);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //System.out.println(">>>>ADAPTER DATA: "+meetData.get(pos).meetID);

        if(meetData.get(pos).status.equalsIgnoreCase("Done")){
            listView.setBackgroundColor(Color.parseColor("#50FF0000"));
        }

        return listView;
    }
}
