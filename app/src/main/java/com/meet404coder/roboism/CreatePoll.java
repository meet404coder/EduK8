package com.meet404coder.roboism;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreatePoll extends AppCompatActivity {

    String ques;
    String options = "";
    PollDrafter pollDrafter;
    SeekBar q_sb_min, r_sb_min,q_sb_max, r_sb_max;
    ProgressDialog progressDialog;
    TextView tv_qal_min, tv_ral_min,tv_qal_max, tv_ral_max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        final EditText et_ques  = (EditText) findViewById(R.id.enterques_et);

        progressDialog = new ProgressDialog(CreatePoll.this);
        progressDialog.setCancelable(true);
        progressDialog.setTitle("Contacting Servers...");
        progressDialog.setMessage("Please Wait...");

        EditText et_op1 = (EditText) findViewById(R.id.op_et_1);
        EditText et_op2 = (EditText) findViewById(R.id.op_et_2);
        EditText et_op3 = (EditText) findViewById(R.id.op_et_3);
        EditText et_op4 = (EditText) findViewById(R.id.op_et_4);
        EditText et_op5 = (EditText) findViewById(R.id.op_et_5);

        tv_qal_min = (TextView) findViewById(R.id.tv_qal_min);
        tv_ral_min = (TextView) findViewById(R.id.tv_ral_min);
        tv_qal_max = (TextView) findViewById(R.id.tv_qal_max);
        tv_ral_max = (TextView) findViewById(R.id.tv_ral_max);

        final EditText[] options_et = {et_op1,et_op2,et_op3,et_op4,et_op5};

        q_sb_min = (SeekBar) findViewById(R.id.ques_access_level_bar_min);
        r_sb_min = (SeekBar) findViewById(R.id.res_access_level_bar_min);
        q_sb_max = (SeekBar) findViewById(R.id.ques_access_level_bar_max);
        r_sb_max = (SeekBar) findViewById(R.id.res_access_level_bar_max);

        r_sb_min.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_qal_min.setText("Min(Inclusive): " + r_sb_min.getProgress());
                r_sb_max.setProgress(r_sb_min.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        r_sb_max.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(r_sb_max.getProgress()<r_sb_min.getProgress()){
                    r_sb_max.setProgress(r_sb_min.getProgress());
                }else {
                    tv_qal_max.setText("Max(Inclusive): " + r_sb_max.getProgress());
                }
            }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    });


        q_sb_min.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_qal_min.setText("Min(Inclusive): " + q_sb_min.getProgress());
                q_sb_max.setProgress(q_sb_min.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        q_sb_max.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(q_sb_max.getProgress()<q_sb_min.getProgress()){
                    q_sb_max.setProgress(q_sb_min.getProgress());
                }else {
                    tv_qal_max.setText("Max(Inclusive): " + q_sb_max.getProgress());
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Button bttn_publish = (Button) findViewById(R.id.publish_bttn);
        bttn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                options = "";
                progressDialog.show();
                //get Uid
                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                //get ques
                ques = et_ques.getText().toString();
                if(ques.length()>5) {
                    //get options
                    for (int i = 0; i < 4; i++) {
                        if (options_et[i].getText().toString().length() > 1) {
                            options += options_et[i].getText().toString() + ":";
                        }
                    }
                    if (options_et[4].getText().toString().length() > 1) {
                        options = ":" + options_et[4].getText().toString();
                    }
                    //Check That atleast 2 options are filled
                    if (options.split(":").length >= 2) {

                        //get q_access_level_min
                        final int qal_min = q_sb_min.getProgress();
                        //get r_access_level_min
                        final int ral_min = r_sb_min.getProgress();
                        //get q_access_level_max
                        final int qal_max = q_sb_max.getProgress();
                        //get r_access_level_max
                        final int ral_max = r_sb_max.getProgress();

                        if (ques.length() < 5) {
                            et_ques.setText(ques);
                            Toast.makeText(CreatePoll.this, "Enter a valid Question!", Toast.LENGTH_LONG).show();
                        } else {
                            //The Question is entered
                            String op = "";
                            for(int i=0;i<options.split(":").length;i++){op+="("+(i+1)+") "+options.split(":")[i]+"\n";}
                            android.app.AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new android.app.AlertDialog.Builder(CreatePoll.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                            } else {
                                builder = new android.app.AlertDialog.Builder(CreatePoll.this);
                            }
                            builder.setTitle("Publish?")
                                    .setCancelable(false)
                                    .setMessage("\nKindly Check The details before publishing.\n" +
                                            "\nQuestion:\n(*) "+ques+"\n"+
                                            "\nOptions:\n"+ op + "\n"+
                                            "Ques View Access Level: "+ qal_min+"\n"+
                                            "Results View Access Level: "+ral_min
                                    )
                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressDialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue
                                            pollDrafter = new PollDrafter(uid, ques, options, qal_min,qal_max, ral_min,ral_max, "Open","QID");
                                            //firebase data store
                                            DatabaseReference memberProfileRef = FirebaseDatabase.getInstance().getReference(Config.PollingDrafterRef);
                                            String QuesID = memberProfileRef.child(uid).push().getKey();
                                            pollDrafter.quesID = QuesID;
                                            memberProfileRef.child(uid).child(QuesID).setValue(pollDrafter).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        //Susseccfully Generated Member's Profile
                                                        progressDialog.dismiss();
                                                        Toast.makeText(CreatePoll.this, "Published Successfully!", Toast.LENGTH_LONG).show();
                                                        finish();
                                                    } else {
                                                        //User Data not stored
                                                        progressDialog.dismiss();
                                                        Toast.makeText(CreatePoll.this, "Check Internet Connection!", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }
                    } else {if(progressDialog.isShowing()){
                        progressDialog.dismiss();}
                        Toast.makeText(CreatePoll.this, "Enter Atleast 2 Options!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    if(progressDialog.isShowing()){
                    progressDialog.dismiss();}
                    Toast.makeText(CreatePoll.this, "Enter a Valid Question!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
