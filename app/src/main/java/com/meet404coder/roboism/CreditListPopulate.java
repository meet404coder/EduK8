package com.meet404coder.roboism;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CreditListPopulate extends AppCompatActivity {

    private List<MessOffData> messOffDataList;
    ListView mListView;
    TextView tv_totalCredits;

    ProgressDialog progressDialog;

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String uid = mAuth.getCurrentUser().getUid();

    MessOffData messOffDataObj;

    float totalCredits;

    DatabaseReference mMessOffRef = FirebaseDatabase.getInstance().getReference(Config.MessOffDatabaseRef);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.credit_listpopulate_layout);


        messOffDataList = new ArrayList<>();

        mListView = (ListView) findViewById(R.id.listView);
        tv_totalCredits = (TextView) findViewById(R.id.totalcredits);

        progressDialog = new ProgressDialog(CreditListPopulate.this);
        progressDialog.setTitle("Just a moment...");
        progressDialog.setMessage("Please wait while we fetch the Data...");
        progressDialog.setCancelable(false);

        progressDialog.show();

        ChildEventListener childEventListenerForCredits = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int keyCount = 0;
                String uid = "";
                String messOfData = "";
                String date = "";
                String extras = "";
                String creditPending = "";
                String status = "";
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equalsIgnoreCase("uid")) {
                        uid = ds.getValue().toString();
                        keyCount++;
                    } else if (ds.getKey().equalsIgnoreCase("date")) {
                        date = ds.getValue().toString();
                        keyCount++;
                    } else if (ds.getKey().equalsIgnoreCase("messOfData")) {
                        messOfData = ds.getValue().toString();
                        keyCount++;
                    } else if (ds.getKey().equalsIgnoreCase("extras")) {
                        extras = ds.getValue().toString();
                        keyCount++;
                    } else if (ds.getKey().equalsIgnoreCase("creditPending")) {
                        creditPending = ds.getValue().toString();
                        keyCount++;
                    } else if (ds.getKey().equalsIgnoreCase("status")) {
                        status = ds.getValue().toString();
                        keyCount++;
                    }

                    if (keyCount == 5) {
                        keyCount = 0;
                        if(status.equalsIgnoreCase(Config.DatePassed)) {
                            messOffDataObj = new MessOffData(uid, date, messOfData, extras, creditPending, status);
                            totalCredits = totalCredits + Float.parseFloat(messOffDataObj.creditPending);
                            messOffDataList.add(messOffDataObj);
                        }
                    }
                }

                Proceed();
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
                HandledbError();
            }
        };

        mMessOffRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(CreditListPopulate.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(CreditListPopulate.this);
                    }
                    builder.setTitle("No Credits Yet!")
                            .setMessage("\nSwitch-off meals to earn credits.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMessOffRef.child(uid).addChildEventListener(childEventListenerForCredits);

    }


    private void setTheAdapter() {
        CreditDetailsListAdapter mydblistAdapter = new CreditDetailsListAdapter(CreditListPopulate.this, this.messOffDataList);
        mListView.setAdapter(mydblistAdapter); // Populate the listView with data
        progressDialog.dismiss();
    }

    void HandledbError() {
        final Dialog dialog = new Dialog(CreditListPopulate.this);
        dialog.setContentView(R.layout.dialog_internet_error);
        dialog.setTitle("Attention !");
        dialog.show();
    }

    void Proceed() {

        tv_totalCredits.setText("Total Credits: " + totalCredits);
        setTheAdapter();
    }
}
