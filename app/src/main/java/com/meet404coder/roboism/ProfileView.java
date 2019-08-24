package com.meet404coder.roboism;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileView extends AppCompatActivity {

    public int year, month, day;
    public long server_epoch;
    int timeUpdatedFlag = 0,creditsReadFlag = 0 ;


    TextView tv_name, pers_desc, tv_credits, tv_hostelname, tv_roomno;
    Button bttn_CreditDetails;

    UserProfile userData;
//    MessOffData messOffDataObj;


    ProgressDialog progressDialog;
    public static float totalCredits;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(Config.UserDatabaseRef);
    DatabaseReference mMessOffRef = FirebaseDatabase.getInstance().getReference(Config.MessOffDatabaseRef);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        userData = new UserProfile();
//        messOffDataObj = new MessOffData();

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String uid = mAuth.getCurrentUser().getUid();

        tv_name = (TextView) findViewById(R.id.list_name);
        pers_desc = (TextView) findViewById(R.id.list_desc);
        tv_credits = (TextView) findViewById(R.id.list_desc2);
        tv_hostelname = (TextView) findViewById(R.id.list_name3);
        tv_roomno = (TextView) findViewById(R.id.list_desc3);

        bttn_CreditDetails = (Button)findViewById(R.id.list_btn_creditdesc);

        progressDialog = new ProgressDialog(ProfileView.this);
        progressDialog.setTitle("Just a moment...");
        progressDialog.setMessage("Please wait while we fetch your data...");
        progressDialog.setCancelable(false);

        progressDialog.show();

        readserverfortime();

        ChildEventListener childEventListenerForCredits = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                totalCredits =0;
                int keyCount = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equalsIgnoreCase("uid")) {
//                        messOffDataObj.uid = ds.getValue().toString();
                        keyCount++;
                    } else if (ds.getKey().equalsIgnoreCase("date")) {
//                        messOffDataObj.date = ds.getValue().toString();
                        keyCount++;
                    } else if (ds.getKey().equalsIgnoreCase("messOfData")) {
//                        messOffDataObj.messOfData = ds.getValue().toString();
                        keyCount++;
                    } else if (ds.getKey().equalsIgnoreCase("extras")) {
//                        messOffDataObj.extras = ds.getValue().toString();
                        keyCount++;
                    } else if (ds.getKey().equalsIgnoreCase("creditPending")) {
//                        messOffDataObj.creditPending = ds.getValue().toString();
                        keyCount++;
                    } else if (ds.getKey().equalsIgnoreCase("status")) {
//                        messOffDataObj.status = ds.getValue().toString();
                        keyCount++;
                    }

                    if (keyCount == 5) {
                        keyCount = 0;

                        /*
                        System.out.println(">>>>>>>*************<<<<<<<<<<<<");
                        System.out.println(">>>>>>><<<<<<<<"+messOffDataObj.date+"\n************");
                        for(int i=0;i<messOffDataList.size();i++) {
                            System.out.println(">>>>>> "+messOffDataList.get(i).date);
                        }
                        System.out.println(">>>>>>>************<<<<<<<<<<<<");

                        System.out.println(messOffDataObj.creditPending + "\n" +
                                messOffDataObj.status + "\n" +
                                messOffDataObj.uid + "\n" +
                                messOffDataObj.messOfData + "\n" +
                                messOffDataObj.extras + "\n" +
                                messOffDataObj.date);
                                */

//                        try {
//
//                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
////                            Date fetchedMessOffDate = simpleDateFormat.parse(messOffDataObj.date);
//                            Date today = new Date(server_epoch);
//
//                            if (fetchedMessOffDate.before(today)) {
////                                messOffDataObj.status = Config.DatePassed;
////                                mMessOffRef.child(uid).child(messOffDataObj.date.replace("/", "-")).setValue(messOffDataObj);
//                            }
//                        } catch (ParseException e) {
//
//                        }

                    }
                }

                creditsReadFlag++;
                if(creditsReadFlag == 2) {
                    tv_credits.setText("Total Credits: "+totalCredits+"");
                    progressDialog.dismiss();
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
                HandleErrorReadingData();
            }
        };

        mMessOffRef.child(uid).addChildEventListener(childEventListenerForCredits);


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userData = dataSnapshot.getValue(UserProfile.class);
                if (userData.uid.equals(uid)) {
                    tv_name.setText(userData.name);
                    tv_hostelname.setText(userData.hostel);
                    tv_roomno.setText("Room No.: " + userData.room);
                    pers_desc.setText("Admission No.: " + userData.admno
                            + "\nMobile No.: " + userData.mobile
                            + "\nEmail Id: " + userData.email);

                    Toast.makeText(ProfileView.this, "Data Fetched! Updated Personal Profile", Toast.LENGTH_LONG).show();

                    mMessOffRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    creditsReadFlag++;
                    if(creditsReadFlag == 2) {
                        tv_credits.setText("Total Credits: " + totalCredits);
                        progressDialog.dismiss();
                    }
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
                progressDialog.dismiss();
                Toast.makeText(ProfileView.this, "Error Reading Data!", Toast.LENGTH_LONG).show();
                finish();
            }
        };

        mRef.addChildEventListener(childEventListener);


        bttn_CreditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent ViewDetails = new Intent(ProfileView.this,CreditListPopulate.class);
//                startActivity(ViewDetails);
            }
        });

    }

    void HandleErrorReadingData() {
        Toast.makeText(ProfileView.this, "Error Reading Data!", Toast.LENGTH_LONG).show();
        final Dialog dialog = new Dialog(ProfileView.this);
        dialog.setContentView(R.layout.dialog_internet_error);
        dialog.setTitle("Attention !");
        dialog.show();
    }

    public void readserverfortime() {

        DatabaseReference tref = FirebaseDatabase.getInstance().getReference(Config.serverTimeRef);

        tref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                server_epoch = (Long) snapshot.getValue();
                Date today = new Date(server_epoch);
                day = today.getDay();
                month = today.getMonth();
                year = today.getYear();

                timeUpdatedFlag = 1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                HandleErrorReadingData();
            }
        });

        tref.setValue(ServerValue.TIMESTAMP);
    }
}
