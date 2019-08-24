package com.meet404coder.roboism;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

/**
 * A login screen that offers login via status/password.
 */
public class LoginActivity extends AppCompatActivity{
    private String verificationCode;
    private static final String TAG = "LoginActivity" ;
    public EditText et_loginId,et_pass;
    public Button bttn_login,bttn_verOTP;
    public ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private PrefManager prefManager;

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefManager = new PrefManager(LoginActivity.this);

        // UI references.
        et_loginId = (EditText) findViewById(R.id.login_id);
        et_pass = (EditText) findViewById(R.id.password);
        bttn_login = (Button) findViewById(R.id.sign_in_button);
        bttn_verOTP = (Button) findViewById(R.id.otpverify_in_button);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Just a moment...");
        progressDialog.setMessage("Please wait while we send you an OTP...");
        progressDialog.setCancelable(false);

        et_pass.setVisibility(View.GONE);
        bttn_verOTP.setVisibility(View.GONE);


        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(LoginActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(LoginActivity.this);
        }
        builder.setTitle("Alert!!!")
                .setMessage("\nThe OTP Authentication might not work on College WiFi.\n\nUse your ques_view_access_level network if you face difficulties.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        //Initiallizing the firebase Authentication Variables
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        if(mAuth.getCurrentUser() == null){
            //Toast.makeText(LoginActivity.this,"User Not Registered!",Toast.LENGTH_LONG).show();
        }else{
            //Toast.makeText(LoginActivity.this,mAuth.getCurrentUser().getUid(),Toast.LENGTH_LONG).show();
            finish();
            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        }

        bttn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLogin();
            }
        });

        //Dismissing the progress dialog before activity finishes otherwise error will be displayed
        if(LoginActivity.this.isFinishing() && progressDialog != null){
            progressDialog.dismiss();
        }

        bttn_verOTP.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, et_pass.getText().toString()) ;
                signInWithPhoneAuthCredential(credential);
            }
        });

        //Dismissing the progress dialog before activity finishes otherwise error will be displayed
        if(LoginActivity.this.isFinishing() && progressDialog != null){
            progressDialog.dismiss();
        }
    }


    boolean FeildsAreFilled(){
        if(et_loginId.getText().toString().isEmpty()){
            et_loginId.setError("Feild Required");
            return false;
        }else if(et_loginId.getText().toString().length()!=10){
            et_loginId.setError("Enter a valid 10 digit ques_view_access_level number.");
            return false;
        }else{
            //The feilds are filled
            return true;
        }
    }

    static boolean mem_possible = false;
    void tryLogin(){
        //Check if the feilds are filled
        if(FeildsAreFilled()) {

            progressDialog.show();

            final String rawmob = et_loginId.getText().toString();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(Config.MembersMobRef);
            myRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        System.out.println(">>>>>> MOBILE NOS: "+data.getValue().toString());
                        if(data.getValue().toString().equals(rawmob)){
                            mem_possible = true;
                            break;
                        }
                    }
                    MemberPossible(mem_possible);
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
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            System.out.println(">>>>>>signInWithCredential:success");
                            progressDialog.dismiss();
                            finish();
                            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                           //System.out.println(">>>>>>>>signInWithCredential:failure" + task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this,"The OTP entered is Wrong!",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }


    void MemberPossible(boolean mempossible) {

        if (mempossible) {

            String phoneNum = "+91" + et_loginId.getText().toString();

// Whenever verification is triggered with the whitelisted number,
// provided it is not set for auto-retrieval, onCodeSent will be triggered.
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNum,
                    30 /*timeout*/,
                    TimeUnit.SECONDS,
                    LoginActivity.this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                            Toast.makeText(LoginActivity.this, "Verification Completed!", Toast.LENGTH_SHORT).show();
                            signInWithPhoneAuthCredential(phoneAuthCredential);
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                            Toast.makeText(LoginActivity.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(s, forceResendingToken);
                            verificationCode = s;
                            Toast.makeText(LoginActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            et_pass.setVisibility(View.VISIBLE);
                            bttn_verOTP.setVisibility(View.VISIBLE);
                            bttn_login.setVisibility(View.GONE);
                            et_loginId.setEnabled(false);
                        }

                    });
        } else {
            //ShowDialog box
            android.app.AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new android.app.AlertDialog.Builder(LoginActivity.this, android.R.style.Theme_DeviceDefault);
            } else {
                builder = new android.app.AlertDialog.Builder(LoginActivity.this);
            }
            builder.setTitle("Unauthorized!")
                    .setMessage("You are not registered as a member.\n\nKindly try again or continue as a guest!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(LoginActivity.this, GuestOrLogin.class));
                            finish();
                        }
                    })
                    .setNegativeButton("Try Agin", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // restart this Activity
                            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        }
    }

}

