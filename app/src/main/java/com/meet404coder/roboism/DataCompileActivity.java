package com.meet404coder.roboism;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataCompileActivity extends AppCompatActivity {

    public int year, month, day;
    public long server_epoch;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(Config.MessOffDatabaseRef);
    DatabaseReference mProfileRef = FirebaseDatabase.getInstance().getReference(Config.UserDatabaseRef);
    DatabaseReference mRefAdmin = FirebaseDatabase.getInstance().getReference(Config.AdminQRread);

    MessOffData messOffData1;
    QRreadDataStore qRreadDataStore1;

    static List<MessOffData> messOffDataList = new ArrayList<>();
    static List<QRreadDataStore> qRreadDataStoreList = new ArrayList<>();

    static int totalUsers = 0;

    List<String> dates_messOff = new ArrayList<>();
    List<String> uids = new ArrayList<>();
    List<Integer> brkOFF = new ArrayList<>();
    List<Integer> lunOFF = new ArrayList<>();
    List<Integer> snkOFF = new ArrayList<>();
    List<Integer> dinOFF = new ArrayList<>();


    Button bttn_datacompile;
    ProgressDialog progressDialog;
    EditText et_dataCompiled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_compile);

        messOffData1 = new MessOffData();
        qRreadDataStore1 = new QRreadDataStore();

        progressDialog = new ProgressDialog(DataCompileActivity.this);
        progressDialog.setTitle("Contacting Servers...");
        progressDialog.setMessage("Please wait while we fetch the data.");
        progressDialog.setCancelable(false);

        bttn_datacompile = (Button) findViewById(R.id.datacompile_bttn);
        bttn_datacompile.setEnabled(false);
        et_dataCompiled = (EditText) findViewById(R.id.compiledtext);


        progressDialog.show();
        readserverfortime();

        //When the data is Loaded the progress dialog is dismissed
        //Using this as an identifier to get the point when the data has been read
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bttn_datacompile.setEnabled(true);
            }
        });


        bttn_datacompile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(">>>>>> TOTAL OFF(ALL DATES)  - USERS: "+messOffDataList.size());
                System.out.println(">>>>>> TOTAL OFF(ALL DATES):  - ADMIN: "+qRreadDataStoreList.size());

                for(MessOffData messOffData:messOffDataList){
                    if(!dates_messOff.contains(messOffData.date)){
                        dates_messOff.add(messOffData.date);
                    }
                    if(!uids.contains(messOffData.uid)){
                        uids.add(messOffData.uid);
                    }
                }

                for(String dates:dates_messOff){
                    int[] mealoff = {0,0,0,0};
                    for(MessOffData messOffDataforDate:messOffDataList){
                        if(messOffDataforDate.date.equals(dates)){
                            String sf = messOffDataforDate.messOfData;

                            int b1 = Integer.parseInt("" + sf.charAt(0));
                            int l1 = Integer.parseInt("" + sf.charAt(1));
                            int s1 = Integer.parseInt("" + sf.charAt(2));
                            int d1 = Integer.parseInt("" + sf.charAt(3));

                            mealoff[0] += b1;
                            mealoff[1] += l1;
                            mealoff[2] += s1;
                            mealoff[3] += d1;
                        }
                    }

                    brkOFF.add(mealoff[0]);
                    lunOFF.add(mealoff[1]);
                    snkOFF.add(mealoff[2]);
                    dinOFF.add(mealoff[3]);
                }

                //Print the results
                totalUsers = uids.size();
                for(int i =0;  i < dates_messOff.size();i++){
                    String data = "\nDate: "+ dates_messOff.get(i) + "\nOFF:\tB: "+ brkOFF.get(i) +"\tL: "+ lunOFF.get(i) +"\tS: "+ snkOFF.get(i) +"\tD: "+ dinOFF.get(i);
                    et_dataCompiled.append(data);

                    /*
                    System.out.println(">>>>> --------------------------------------------------------------------- <<<<<");
                    System.out.println(">>>>> TOTAL USERS: "+ totalUsers);
                    System.out.println(">>>>> Date: "+ dates_messOff.get(i));
                    System.out.println(">>>>>OFF:\tB: "+ brkOFF.get(i) +"\tL: "+ lunOFF.get(i) +"\tS: "+ snkOFF.get(i) +"\tD: "+ dinOFF.get(i));
                    System.out.println(">>>>>ON:\tB: "+ (totalUsers-brkOFF.get(i)) +"\tL: "+ (totalUsers-lunOFF.get(i)) +"\tS: "+ (totalUsers-snkOFF.get(i)) +"\tD: "+ (totalUsers-dinOFF.get(i)));
                    System.out.println(">>>>>PER:"+
                    "\tB: " + (brkOFF.get(i)/totalUsers)*100 +"%"+
                    "\tL: " + (lunOFF.get(i)/totalUsers)*100 +"%"+
                    "\tS: " + (snkOFF.get(i)/totalUsers)*100 +"%"+
                    "\tD: " + (dinOFF.get(i)/totalUsers)*100 +"%");
                */
                }

            }
        });

    }

    public void readserverfortime() {

        DatabaseReference tref = FirebaseDatabase.getInstance().getReference(Config.serverTimeRef);

        tref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                server_epoch = (Long) snapshot.getValue();
                Date today = new Date(server_epoch);
                SimpleDateFormat ddsimpleDateFormat = new SimpleDateFormat("dd");
                SimpleDateFormat MMsimpleDateFormat = new SimpleDateFormat("MM");
                SimpleDateFormat yyyysimpleDateFormat = new SimpleDateFormat("yyyy");
                day = Integer.parseInt(ddsimpleDateFormat.format(today));
                month = Integer.parseInt(MMsimpleDateFormat.format(today));
                year = Integer.parseInt(yyyysimpleDateFormat.format(today));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ErrorContactingDatabase(databaseError);
            }
        });

        tref.setValue(ServerValue.TIMESTAMP, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                LoadData();
            }
        });
    }
    void ErrorContactingDatabase(DatabaseError e) {
        Toast.makeText(DataCompileActivity.this, "ERROR DB: " + e, Toast.LENGTH_LONG).show();
    }

    int ctr = 0;
    void LoadData() {

        //Load User's Switching Data
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //System.out.println(">>>>> PARENT UID: "+dataSnapshot.getKey());

                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    //System.out.println(">>>>> DATE: "+ds.getKey());

                    ds.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnap) {
                            int keyCount = 0;
                            String uid = "";
                            String messOfData = "";
                            String date = "";
                            String extras = "";
                            String creditPending = "";
                            String status = "";
                            for(DataSnapshot dataSnapshot: dataSnap.getChildren()) {

                                if (dataSnapshot.getKey().equalsIgnoreCase("uid")) {
                                    uid = dataSnapshot.getValue().toString();
                                    keyCount++;
                                } else if (dataSnapshot.getKey().equalsIgnoreCase("date")) {
                                    date = dataSnapshot.getValue().toString();
                                    keyCount++;
                                } else if (dataSnapshot.getKey().equalsIgnoreCase("messOfData")) {
                                    messOfData = dataSnapshot.getValue().toString();
                                    keyCount++;
                                } else if (dataSnapshot.getKey().equalsIgnoreCase("extras")) {
                                    extras = dataSnapshot.getValue().toString();
                                    keyCount++;
                                } else if (dataSnapshot.getKey().equalsIgnoreCase("creditPending")) {
                                    creditPending = dataSnapshot.getValue().toString();
                                    keyCount++;
                                } else if (dataSnapshot.getKey().equalsIgnoreCase("status")) {
                                    status = dataSnapshot.getValue().toString();
                                    keyCount++;
                                }

                                if (keyCount == 5) {
                                    keyCount = 0;
                                    messOffData1 = new MessOffData(uid, date, messOfData, extras, creditPending, status);
                                    messOffDataList.add(messOffData1);
                                    //System.out.println(">>> MESS OFF DATA SIZE: "+messOffDataList.size());
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            ErrorContactingDatabase(databaseError);
                        }
                    });
                }

                ctr++;
                if(ctr == 2 && progressDialog.isShowing()){
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
                ErrorContactingDatabase(databaseError);
            }
        });


        //TODO: COMPLETE THIS
        //Load Scanned Mess Off Data
        mRefAdmin.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //System.out.println(">>>>> PARENT UID: "+dataSnapshot.getKey());

                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    //System.out.println(">>>>> DATE: "+ds.getKey());

                    ds.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnap) {
                            int keyCount = 0;
                            String uid = "";
                            String date = "";
                            String messOfDataEaten = "";
                            String extrasTaken = "";
                            String status = "";

                            for(DataSnapshot dataSnapshot: dataSnap.getChildren()) {

                                if (dataSnapshot.getKey().equalsIgnoreCase("uid")) {
                                    uid = dataSnapshot.getValue().toString();
                                    keyCount++;
                                } else if (dataSnapshot.getKey().equalsIgnoreCase("date")) {
                                    date = dataSnapshot.getValue().toString();
                                    keyCount++;
                                } else if (dataSnapshot.getKey().equalsIgnoreCase("messOfDataEaten")) {
                                    messOfDataEaten = dataSnapshot.getValue().toString();
                                    keyCount++;
                                } else if (dataSnapshot.getKey().equalsIgnoreCase("extrasTaken")) {
                                    extrasTaken = dataSnapshot.getValue().toString();
                                    keyCount++;
                                } else if (dataSnapshot.getKey().equalsIgnoreCase("status")) {
                                    status = dataSnapshot.getValue().toString();
                                    keyCount++;
                                }

                                if (keyCount == 4) {
                                    keyCount = 0;
                                    qRreadDataStore1 = new QRreadDataStore(uid, date, messOfDataEaten, extrasTaken, status);
                                    qRreadDataStoreList.add(qRreadDataStore1);
                                    //System.out.println(">>> MESS OFF DATA [ADMIN] SIZE: "+qRreadDataStoreList.size());
                                }

                            }

                            ctr++;
                            if(ctr == 2 && progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            ErrorContactingDatabase(databaseError);
                        }
                    });
                }

                if(progressDialog.isShowing()){
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
                ErrorContactingDatabase(databaseError);
            }
        });
    }


    //TODO: COMPLETE THIS FUNCTION AND CALL IT
    int getTotalUsers(){

        mProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //COMPLETE THIS AND RETURN THE TOTAL UID COUNT
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return 0;
    }

}
