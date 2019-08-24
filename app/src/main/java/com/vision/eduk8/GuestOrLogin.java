package com.vision.eduk8;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class GuestOrLogin extends AppCompatActivity {

    Button bttn_guest,bttn_login;
    ImageView logoView;

    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_or_login);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(GuestOrLogin.this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCancelable(false);

        bttn_guest = (Button) findViewById(R.id.guest_bttn);
        bttn_login = (Button) findViewById(R.id.login_bttn);
        logoView = (ImageView) findViewById(R.id.logoim);

        Picasso.with(GuestOrLogin.this).load(R.drawable.blk_logo).fit().into(logoView);

        bttn_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();
                guestLogin();
            }
        });

        bttn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                startActivity(new Intent(GuestOrLogin.this,LoginActivity.class));
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                finish();
            }
        });

    }

    void guestLogin(){

        mAuth.signInAnonymously().addOnCompleteListener(GuestOrLogin.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Successful Guest Login

                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    startActivity(new Intent(GuestOrLogin.this,Dashboard.class));
                    finish();

                }else{
                    //Guest Login Unsuccessful

                    ShowDialog("Unsuccessful!",
                            "Unable to SignIN as Guest.\n\nMake Sure you have an active Internet Connection!");
                }
            }
        });
    }

    void ShowDialog(String title,String message){
        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(GuestOrLogin.this, android.R.style.Theme_DeviceDefault);
        } else {
            builder = new android.app.AlertDialog.Builder(GuestOrLogin.this);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with this page and do nothing!
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
