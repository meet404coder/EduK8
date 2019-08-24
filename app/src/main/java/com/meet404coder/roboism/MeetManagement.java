package com.meet404coder.roboism;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class MeetManagement extends AppCompatActivity {

    ListView listView;
    String date[]      ;
    String time_from[]      ;
    String time_to[]      ;
    String venue[]      ;
    String rem[]      ;
    String al_min[]      ;
    String al_max[]      ;
    String issued_by[]      ;
    String agenda[]      ;

    List<MeetData> meetDataList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeetManagement.this,CallMeet.class));
            }
        });

        meetDataList.clear();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(Config.MeetsRef);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                meetDataList.clear();
                for(DataSnapshot ds1:dataSnapshot.getChildren()) {
                    for(DataSnapshot ds:ds1.getChildren()) {
                        MeetData meetData = ds.getValue(MeetData.class);
                        if (!meetDataList.contains(meetData)) {
                            meetDataList.add(meetData);
                        }
                    }
                }

                //Done reading meets data, Proceed to use this info
                DoneLoadingMeetData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    void DoneLoadingMeetData(){
        listView = (ListView) findViewById(R.id.meet_list_view_meetmgmt);
        MeetingListAdapter gridAdapter = new MeetingListAdapter(MeetManagement.this,meetDataList);
        listView.setAdapter(gridAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        break;
                    }
                }

                //Statements that you want to be executed no matter what has been selected
            }
        });
    }

}
