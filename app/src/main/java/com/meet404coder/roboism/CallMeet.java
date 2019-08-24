package com.meet404coder.roboism;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CallMeet extends AppCompatActivity {

    public int year, month, day;
    public int day_today,month_today,year_today;
    public int hour_from = -1, min_from = -1, hour_to = -1,min_to = -1;

    MeetData meetData;
    
    SeekBar sb_al_min, sb_al_max;
    ProgressDialog progressDialog;
    TextView tv_date, tv_time_from, tv_time_to,tv_qal_min,tv_qal_max;
    EditText tv_venue,tv_agenda,tv_rem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_meet);

        progressDialog = new ProgressDialog(CallMeet.this);
        progressDialog.setCancelable(true);
        progressDialog.setTitle("Contacting Servers...");
        progressDialog.setMessage("Please Wait...");

        tv_date = (TextView) findViewById(R.id.tv_info_date);
        tv_time_from = (TextView) findViewById(R.id.tv_info_time_from);
        tv_time_to = (TextView) findViewById(R.id.tv_info_time_to);
        tv_venue = (EditText) findViewById(R.id.tv_info_venue);
        tv_agenda = (EditText) findViewById(R.id.tv_info_agenda);
        tv_rem = (EditText) findViewById(R.id.tv_info_rem);
        tv_qal_min = (TextView) findViewById(R.id.tv_qal_min);
        tv_qal_max = (TextView) findViewById(R.id.tv_qal_max);


        sb_al_min = (SeekBar) findViewById(R.id.access_level_bar_min);
        sb_al_max = (SeekBar) findViewById(R.id.access_level_bar_max);


        //To Change Date
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CallMeet.this,
                        datePickerListener, year, month, day);
                readSystemForDate();
                datePickerDialog.getDatePicker().init(year,month,day,datePickerDialog);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()+1000);
                datePickerDialog.setCancelable(true);
                datePickerDialog.show();
            }
        });
        
        //To Change Time
        tv_time_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CallMeet.this,
                        timePickerListner_from,hour_from,min_from,false);
                timePickerDialog.setCancelable(true);
                timePickerDialog.show();
            }
        });

        tv_time_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CallMeet.this,
                        timePickerListner_to,hour_to,min_to,false);
                timePickerDialog.setCancelable(true);
                timePickerDialog.show();
            }
        });        
        
        
        
        DispSeekerVal(sb_al_min, tv_qal_min,"Min(Inclusive):",0);
        DispSeekerVal(sb_al_max, tv_qal_max,"Max(Inclusive):",1);

        sb_al_min.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_qal_min.setText("Min(Inclusive): " + sb_al_min.getProgress());
                sb_al_max.setProgress(sb_al_min.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sb_al_max.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(sb_al_max.getProgress()<sb_al_min.getProgress()){
                    sb_al_max.setProgress(sb_al_min.getProgress());
                }else {
                    tv_qal_max.setText("Max(Inclusive): " + sb_al_max.getProgress());
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
            public void onClick(final View v) {

                progressDialog.show();
                setToday();

                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final String create_date = day_today+"/"+month_today+"/"+year_today;
                final String meet_date = day+"/"+month+"/"+year;
                final int al_min = sb_al_min.getProgress();
                final int al_max = sb_al_max.getProgress();
                final String time_from = hour_from+":"+min_from;
                final String time_to = hour_to+":"+min_to;
                final String venue = tv_venue.getText().toString();
                final String rem = tv_rem.getText().toString();
                final String agenda = tv_agenda.getText().toString();
                final String status = Config.CallMeetStatusPending;

                //TODO: Check Data Validity
                if(day!=0){
                    if(hour_from >= 0 && min_from >= 0){
                        if(hour_to >= 0 && min_to >= 0){
                            if(venue.length()>=2){
                                //Checked the required values


                                meetData = new MeetData(uid,meet_date,time_from,time_to,venue,
                                        agenda,create_date,status,rem,0,al_min,al_max,"MeetID");

                                android.app.AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    builder = new android.app.AlertDialog.Builder(CallMeet.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                                } else {
                                    builder = new android.app.AlertDialog.Builder(CallMeet.this);
                                }
                                builder.setTitle("Issue?")
                                        .setCancelable(false)
                                        .setMessage("\nKindly Check The details before issuing.\n" +
                                                "\nDate: [ "+meetData.meet_date+"]\n"+
                                                "\nFrom: ["+ meetData.meet_time_from +"] To: [" + meetData.meet_time_to+"]\n"+
                                                "Venue: ["+ meetData.meet_venue+"]\n"+
                                                "Agenda: ["+ meetData.agenda+"]\n"+
                                                "Remarks: ["+ meetData.remarks+"]\n"+
                                                "Access Level: ["+meetData.min_acc_lvl+"] to ["+meetData.max_acc_lvl+"]"
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
                                                DatabaseReference db = FirebaseDatabase.getInstance().getReference(Config.MemberProfileTAGDataRef);
                                                db.addChildEventListener(new ChildEventListener() {
                                                    @Override
                                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                        LinkedTagToMember tagToMember = new LinkedTagToMember();
                                                        tagToMember = dataSnapshot.getValue(LinkedTagToMember.class);
                                                        if(tagToMember.uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                            final int CreatorAccessLevel = tagToMember.accessLevel;
                                                            meetData = new MeetData(uid,meet_date,time_from,time_to,venue,
                                                                    agenda,create_date,status,rem,CreatorAccessLevel,al_min,al_max,"MeetID");
                                                            PutToDB(meetData);
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
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();

                            }else{
                                if(progressDialog.isShowing()){progressDialog.dismiss();}
                                Toast.makeText(CallMeet.this,"Enter The meeting Venue!",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            if(progressDialog.isShowing()){progressDialog.dismiss();}
                            Toast.makeText(CallMeet.this,"Choose The meeting time till!",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        if(progressDialog.isShowing()){progressDialog.dismiss();}
                        Toast.makeText(CallMeet.this,"Choose The meeting time From!",Toast.LENGTH_LONG).show();
                    }
                }else{
                    if(progressDialog.isShowing()){progressDialog.dismiss();}
                    Toast.makeText(CallMeet.this,"Choose a Meeting Date!",Toast.LENGTH_LONG).show();
                }



                /*

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
                        final int qal_min = sb_al_min.getProgress();

                        //get r_access_level_max
                        final int ral_max = r_sb_max.getProgress();

                        if (ques.length() < 5) {
                            et_ques.setText(ques);
                            Toast.makeText(CallMeet.this, "Enter a valid Question!", Toast.LENGTH_LONG).show();
                        } else {
                            //The Question is entered
                            String op = "";
                            for(int i=0;i<options.split(":").length;i++){op+="("+(i+1)+") "+options.split(":")[i]+"\n";}
                            android.app.AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new android.app.AlertDialog.Builder(CallMeet.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                            } else {
                                builder = new android.app.AlertDialog.Builder(CallMeet.this);
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
                                            pollDrafter = new PollDrafter(uid, ques, options, qal_min,qal_max, ral_min,ral_max, "Open");
                                            //firebase data store
                                            DatabaseReference memberProfileRef = FirebaseDatabase.getInstance().getReference(Config.PollingDrafterRef);
                                            memberProfileRef.child(uid).push().setValue(pollDrafter).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        //Susseccfully Generated Member's Profile
                                                        progressDialog.dismiss();
                                                        Toast.makeText(CallMeet.this, "Published Successfully!", Toast.LENGTH_LONG).show();
                                                        finish();
                                                    } else {
                                                        //User Data not stored
                                                        progressDialog.dismiss();
                                                        Toast.makeText(CallMeet.this, "Check Internet Connection!", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(CallMeet.this, "Enter Atleast 2 Options!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();}
                    Toast.makeText(CallMeet.this, "Enter a Valid Question!", Toast.LENGTH_LONG).show();
                }

                */
            }
        });
    }

    void DispSeekerVal(SeekBar seekBar, final TextView disp_text_view, final String denote_string,int minOrmax){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                disp_text_view.setText(denote_string+ " " + seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth + 1;
            day = selectedDay;
            tv_date.setText("DATE: "+day+"/"+month+"/"+year);
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListner_from = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_from = hourOfDay;
            min_from = minute;
            tv_time_from.setText("TIME FROM: "+hour_from+":"+min_from);
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListner_to = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_to = hourOfDay;
            min_to = minute;
            tv_time_to.setText("TIME TO: "+hour_to+":"+min_to);
        }
    };
    
    void readSystemForDate(){
        Date today = Calendar.getInstance().getTime();

        SimpleDateFormat ddsimpleDateFormat = new SimpleDateFormat("dd");
        SimpleDateFormat MMsimpleDateFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yyyysimpleDateFormat = new SimpleDateFormat("yyyy");

        day = Integer.parseInt(ddsimpleDateFormat.format(today));
        month = Integer.parseInt(MMsimpleDateFormat.format(today));
        year = Integer.parseInt(yyyysimpleDateFormat.format(today));
    }

    void setToday(){
        Date today = Calendar.getInstance().getTime();

        SimpleDateFormat ddsimpleDateFormat = new SimpleDateFormat("dd");
        SimpleDateFormat MMsimpleDateFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yyyysimpleDateFormat = new SimpleDateFormat("yyyy");

        day_today   = Integer.parseInt(ddsimpleDateFormat.format(today));
        month_today = Integer.parseInt(MMsimpleDateFormat.format(today));
        year_today = Integer.parseInt(yyyysimpleDateFormat.format(today));
    }

    void PutToDB(MeetData meetData){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(Config.MeetsRef);
        String meetID = db.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
        meetData.meetID = meetID;
        db.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(meetID).setValue(meetData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Data Saved
                    Toast.makeText(CallMeet.this,"Issued!",Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    //Data not saved
                    Toast.makeText(CallMeet.this,"Failed!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
}
