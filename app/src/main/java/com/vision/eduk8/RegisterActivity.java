package com.vision.eduk8;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {
    private PrefManager prefManager;


    public UserProfile userdata;
    String uid;
    String name;
    String admno;
    String email;
    String mobile;
    String hostel, designation, department;
    String room;
    String[] tags = null;
    String firebaseID = FirebaseInstanceId.getInstance().getToken();

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
        pd = new ProgressDialog(RegisterActivity.this);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        mobile = mAuth.getCurrentUser().getPhoneNumber();

        setContentView(R.layout.activity_register);


        pd.setCancelable(false);
        pd.setTitle("Contacting Servers!");
        pd.setMessage("Please Wait...");
        pd.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Config.MemberProfileRef);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    finishAffinity();//This will finish all previous activity and then open dashboard
                    Intent startDash = new Intent(RegisterActivity.this, TagsActivity.class);
                    startActivity(startDash);
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

        //As Value Event Listner is Gaurenteed to be executed after the child event lisner hence i use it
        //as a method to check if all the data has been read and disable the progressdialog
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(pd.isShowing()) {
                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button b = (Button) findViewById(R.id.login_bttn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog;

                EditText et_name = (EditText) findViewById(R.id.login_name);
                EditText et_admno = (EditText) findViewById(R.id.login_admno);
                EditText et_room = (EditText) findViewById(R.id.login_room);
                EditText et_email = (EditText) findViewById(R.id.login_email);
                Spinner spin_desig = (Spinner) findViewById(R.id.login_desig);
                Spinner spin_hostel = (Spinner) findViewById(R.id.login_hostel);
                Spinner spin_dep = (Spinner) findViewById(R.id.login_dep);


                name = et_name.getText().toString();
                admno = et_admno.getText().toString();
                room = et_room.getText().toString();
                email = et_email.getText().toString();
                designation = spin_desig.getSelectedItem().toString();
                department = spin_dep.getSelectedItem().toString();
                hostel = spin_hostel.getSelectedItem().toString();


                ImageView loginimg = (ImageView) findViewById(R.id.login_imgview);
                View snackView = findViewById(android.R.id.content);


                if (name.length() < 2) {
                    Snackbar.make(snackView, "Please Enter a Valid Name", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    loginimg.setImageResource(R.drawable.login_error);
                } else if (admno.length() < 4) {
                    Snackbar.make(snackView, "Please Enter a Valid Admission No.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    loginimg.setImageResource(R.drawable.login_error);
                } else if (room.length() < 3) {
                    //its named status but now its ques_view_access_level number
                    Snackbar.make(snackView, "Go on check your No.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    loginimg.setImageResource(R.drawable.login_error);
                } else if (!(email.contains(".") && email.contains("@"))) {
                    Snackbar.make(snackView, "Please Check your E-mail Once Again", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    loginimg.setImageResource(R.drawable.login_error);
                } else if (!(designation.length() > 3)) {
                    Snackbar.make(snackView, "Designation must be of more than 3 Characters.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    loginimg.setImageResource(R.drawable.login_error);
                } else {
                    progressDialog = new ProgressDialog(RegisterActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setTitle("Just one more moment!");
                    progressDialog.setMessage("Have fun while we unlock the app...");
                    progressDialog.show();


                    userdata = new UserProfile(uid, name, admno, email, department, mobile, hostel, room, designation, firebaseID,0);

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userdata.admno)
                                .build();
                        user.updateProfile(profileUpdates);
                    }

                    //firebase data store
                    DatabaseReference memberProfileRef = FirebaseDatabase.getInstance().getReference(Config.MemberProfileRef);
                    memberProfileRef.child(userdata.uid).child("tags").setValue(tags);
                    memberProfileRef.child(userdata.uid).setValue(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Susseccfully Generated Member's Profile
                                progressDialog.dismiss();
                                finishAffinity();//This will finish all previous activity and then open dashboard
                                Intent startDash = new Intent(RegisterActivity.this, TagsActivity.class);
                                startActivity(startDash);
                            } else {
                                //User Data not stored
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Check Internet Connection!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });

    }
}
