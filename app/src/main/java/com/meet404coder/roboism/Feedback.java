package com.meet404coder.roboism;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Feedback extends AppCompatActivity {


    public int year, month, day;
    public long server_epoch;
    int timeUpdatedFlag = 0;

    Spinner topic_spin;
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String uid = mAuth.getCurrentUser().getUid();
    DatabaseReference mFeedbackRef = FirebaseDatabase.getInstance().getReference(Config.FeedbackRef);
    String topicSelected,Rating = "0",feedback;
    RatingBar ratingBar;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_feedback);

        Button b = (Button) findViewById(R.id.bug_bttn);
        topic_spin = (Spinner) findViewById(R.id.spinner_topic_select);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Rating = "0"+rating;
            }
        });

        progressDialog = new ProgressDialog(Feedback.this);
        progressDialog.setTitle("Just one more moment");
        progressDialog.setMessage("Have fun while we complete your request...");


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();

                String displayName = mAuth.getCurrentUser().getDisplayName();

                EditText et_bug = (EditText) findViewById(R.id.bug_text);
                View snackView = findViewById(android.R.id.content);

                topicSelected = topic_spin.getSelectedItem().toString();

                feedback = et_bug.getText().toString()+ "\nBy : " + displayName;
                if (feedback.length() < (4+ ("\nBy : " + displayName).length())) {
                    Snackbar.make(snackView, "Please Enter a better description.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else if(topicSelected.isEmpty()){
                    Snackbar.make(snackView, "Please Select a Topic.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else if(ratingBar.getRating()<0.5){
                    Snackbar.make(snackView, "Please Select a Rating.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    readserverfortime();
                }


                /*
                SendMail sendMail = new SendMail(Feedback.this,Config.TOEMAIL,"Bug Report", bug);
                sendMail.execute();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (Config.chk_emailsent == 1) {
                            progressDialog.dismiss();
                            final Dialog dialog = new Dialog(Feedback.this);
                            dialog.setContentView(R.layout.thankyou);
                            dialog.setTitle("Thank You");
                            dialog.show();
                            Toast.makeText(Feedback.this, "Success! Just A few more seconds while we receive your report.", Toast.LENGTH_LONG).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Feedback.this, "Please Check Your Internet Connection !", Toast.LENGTH_LONG).show();
                            final Dialog dialog = new Dialog(Feedback.this);
                            dialog.setContentView(R.layout.dialog_internet_error);
                            dialog.setTitle("Attention !");
                            dialog.show();

                        }
                    }
                }, 15000);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    }
                }, 15200);
                
                */
                
                
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

                timeUpdatedFlag = 1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                HandleErrorReadingData();
            }
        });

        tref.setValue(ServerValue.TIMESTAMP, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Proceed();
            }
        });
    }


    void HandleErrorReadingData() {
        Toast.makeText(Feedback.this, "Please Check Your Internet Connection !", Toast.LENGTH_LONG).show();
        final Dialog dialog = new Dialog(Feedback.this);
        dialog.setContentView(R.layout.dialog_internet_error);
        dialog.setTitle("Attention !");
        dialog.show();
    }

    void Proceed(){

        if(timeUpdatedFlag == 1) {

            FeedbackData feedbackData = new FeedbackData(uid,
                    day + "/" + month + "/" + year,
                    feedback,
                    topicSelected,
                    Config.FeedbackStatusPending,
                    Rating);

            mFeedbackRef.child(day + "-" + month + "-" + year).push().setValue(feedbackData, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    progressDialog.dismiss();
                    final Dialog dialog = new Dialog(Feedback.this);
                    dialog.setContentView(R.layout.thankyou);
                    dialog.setTitle("Thank You");
                    dialog.show();
                }
            });

        }else{
            progressDialog.dismiss();
            HandleErrorReadingData();
        }
    }
}