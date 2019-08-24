package com.meet404coder.roboism;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
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

public class MessOffOption extends AppCompatActivity {

    public int year, month, day;
    public int no_of_days_off_allowed = 15;
    public long server_epoch;
    int timeUpdatedFlag = 0;

    Button bttn_dateChooser;
    TextView datetv,tv_menu_b,tv_menu_l,tv_menu_s,tv_menu_d;
    Switch sw_brk, sw_lun, sw_din, sw_snk;

    int b = 0, l = 0, d = 0, s = 0;
    Date messOffDate;
    float credit_b = 0, credit_l = 0, credit_s = 0, credit_d = 0;

    MessOffData messOffDataObj;
    int flag_credits_read = 0;

    ProgressDialog progressDialog;

    String hostelname;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(Config.MessOffDatabaseRef);
    DatabaseReference mMenuRef = FirebaseDatabase.getInstance().getReference(Config.MenuRef);
    DatabaseReference mPersonalDetails = FirebaseDatabase.getInstance()
            .getReference(Config.UserDatabaseRef).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    DatabaseReference mMessCredRef = FirebaseDatabase.getInstance().getReference(Config.MessCreditRef);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_off_option);
        readserverfortime();

        progressDialog = new ProgressDialog(MessOffOption.this);
        progressDialog.setTitle("Contacting Database...");
        progressDialog.setMessage("Please wait while we fetch the data!");
        progressDialog.setCancelable(false);

        sw_brk = (Switch) findViewById(R.id.switch_breakfast);
        sw_lun = (Switch) findViewById(R.id.switch_lunch);
        sw_snk = (Switch) findViewById(R.id.switch_snacks);
        sw_din = (Switch) findViewById(R.id.switch_dinner);
        datetv = (TextView) findViewById(R.id.datetv);

        tv_menu_b = (TextView) findViewById(R.id.list_desc);
        tv_menu_l = (TextView) findViewById(R.id.list_desc2);
        tv_menu_s = (TextView) findViewById(R.id.list_desc3);
        tv_menu_d = (TextView) findViewById(R.id.list_desc4);

        sw_brk.setEnabled(false);
        sw_lun.setEnabled(false);
        sw_snk.setEnabled(false);
        sw_din.setEnabled(false);

        sw_brk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!sw_brk.isChecked()) {
                    sw_brk.setText("OFF");
                    sw_brk.setTextColor(Color.RED);
                    b = 1; //1 means the switch is closed, the mess is off
                } else {
                    sw_brk.setText("ON");
                    sw_brk.setTextColor(Color.parseColor("#0F7D0F"));
                    b = 0; //0 means the mess is on,switch is open
                }

                if (flag_credits_read == 1) {
                    UpdateDataBase();
                }

            }
        });

        sw_lun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!sw_lun.isChecked()) {
                    sw_lun.setText("OFF");
                    sw_lun.setTextColor(Color.RED);
                    l = 1; //1 means the switch is closed, the mess is off
                } else {
                    sw_lun.setText("ON");
                    sw_lun.setTextColor(Color.parseColor("#0F7D0F"));
                    l = 0; //0 means the mess is on,switch is open
                }

                if (flag_credits_read == 1) {
                    UpdateDataBase();
                }

            }
        });


        sw_snk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!sw_snk.isChecked()) {
                    sw_snk.setText("OFF");
                    sw_snk.setTextColor(Color.RED);
                    s = 1; //1 means the switch is closed, the mess is off
                } else {
                    sw_snk.setText("ON");
                    sw_snk.setTextColor(Color.parseColor("#0F7D0F"));
                    s = 0; //0 means the mess is on,switch is open
                }

                if (flag_credits_read == 1) {
                    UpdateDataBase();
                }

            }
        });


        sw_din.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!sw_din.isChecked()) {
                    sw_din.setText("OFF");
                    sw_din.setTextColor(Color.RED);
                    d = 1; //1 means the switch is closed, the mess is off
                } else {
                    sw_din.setText("ON");
                    sw_din.setTextColor(Color.parseColor("#0F7D0F"));
                    d = 0; //0 means the mess is on,switch is open
                }

                if (flag_credits_read == 1) {
                    UpdateDataBase();
                }

            }
        });


        bttn_dateChooser = (Button) findViewById(R.id.datechooser_bttn);
        bttn_dateChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Proceed(timeUpdatedFlag);
            }
        });


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equalsIgnoreCase("breakfast")) {
                    credit_b = Float.parseFloat(dataSnapshot.getValue().toString());
                } else if (dataSnapshot.getKey().equalsIgnoreCase("lunch")) {
                    credit_l = Float.parseFloat(dataSnapshot.getValue().toString());
                } else if (dataSnapshot.getKey().equalsIgnoreCase("snacks")) {
                    credit_s = Float.parseFloat(dataSnapshot.getValue().toString());
                } else if (dataSnapshot.getKey().equalsIgnoreCase("dinner")) {
                    credit_d = Float.parseFloat(dataSnapshot.getValue().toString());
                }

                flag_credits_read = 1;
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
                Toast.makeText(MessOffOption.this, "Error Reading Data!", Toast.LENGTH_LONG).show();
                ErrorReadingDatabase();
                finish();
            }
        };

        mMessCredRef.addChildEventListener(childEventListener);

    }

    public void readserverfortime() {

        DatabaseReference tref = FirebaseDatabase.getInstance().getReference(Config.serverTimeRef);

        tref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                server_epoch = (Long) snapshot.getValue();
                timeUpdatedFlag = 1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ErrorReadingDatabase();
            }
        });

        tref.setValue(ServerValue.TIMESTAMP);
    }


    public void Proceed(int timeUpdatedFlag) {

        if (timeUpdatedFlag == 1) {
            //System.out.println(">>>>>> Time Server: " + server_epoch + " | System: " + cur_time);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, datePickerListener, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(server_epoch + 24 * 60 * 60 * 1000);
            datePickerDialog.getDatePicker().setMaxDate(server_epoch + no_of_days_off_allowed * 24 * 60 * 60 * 1000);
            datePickerDialog.setCancelable(false);
            datePickerDialog.show();

        } else {
            ErrorReadingDatabase();
        }
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth + 1;
            day = selectedDay;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {

                messOffDate = simpleDateFormat.parse(day + "/" + month + "/" + year);
                datetv.setText(day + "/" + month + "/" + year);
                Toast.makeText(MessOffOption.this, "Date Set\n" + day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();

                progressDialog.show();
                RefreshSwitches();

                mPersonalDetails.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        hostelname = userProfile.hostel;
                        UpdateMenu();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } catch (ParseException e) {
                Toast.makeText(MessOffOption.this, "Error!\n" + e, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    };

    void ErrorReadingDatabase() {
        //TODO:: Complete this function
        final Dialog dialog = new Dialog(MessOffOption.this);
        dialog.setContentView(R.layout.dialog_internet_error);
        dialog.setTitle("Attention !");
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    void UpdateDataBase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String messOfData = "" + b + l + s + d;
        String pendingCredits = (b * credit_b + l * credit_l + s * credit_s + d * credit_d) + "";
        String date = day + "/" + month + "/" + year;
        messOffDataObj = new MessOffData(uid, date, messOfData, null, pendingCredits, Config.DateNotYetPassed);
        mRef.child(uid).child(day + "-" + month + "-" + year).setValue(messOffDataObj);
    }

    void RefreshSwitches() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRef.child(uid).child(day + "-" + month + "-" + year).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    sw_brk.setText("ON");
                    sw_brk.setTextColor(Color.parseColor("#0F7D0F"));
                    sw_brk.setChecked(true);
                    sw_lun.setText("ON");
                    sw_lun.setTextColor(Color.parseColor("#0F7D0F"));
                    sw_lun.setChecked(true);
                    sw_snk.setText("ON");
                    sw_snk.setTextColor(Color.parseColor("#0F7D0F"));
                    sw_snk.setChecked(true);
                    sw_din.setText("ON");
                    sw_din.setTextColor(Color.parseColor("#0F7D0F"));
                    sw_din.setChecked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRef.child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MessOffData fetchedData = dataSnapshot.getValue(MessOffData.class);

                if (fetchedData.date.equals(day + "/" + month + "/" + year)){

                    String fetchedmessoff = fetchedData.messOfData;
                int fb = 0, fl = 0, fs = 0, fd = 0;
                fb = Integer.parseInt("" + fetchedmessoff.charAt(0));
                fl = Integer.parseInt("" + fetchedmessoff.charAt(1));
                fs = Integer.parseInt("" + fetchedmessoff.charAt(2));
                fd = Integer.parseInt("" + fetchedmessoff.charAt(3));

                //1 means mess is off

                if (fb == 1) {
                    sw_brk.setText("OFF");
                    sw_brk.setTextColor(Color.RED);
                    sw_brk.setChecked(false);
                } else {
                    sw_brk.setText("ON");
                    sw_brk.setTextColor(Color.parseColor("#0F7D0F"));
                    sw_brk.setChecked(true);
                }


                if (fl == 1) {
                    sw_lun.setText("OFF");
                    sw_lun.setTextColor(Color.RED);
                    sw_lun.setChecked(false);
                } else {
                    sw_lun.setText("ON");
                    sw_lun.setTextColor(Color.parseColor("#0F7D0F"));
                    sw_lun.setChecked(true);
                }


                if (fs == 1) {
                    sw_snk.setText("OFF");
                    sw_snk.setTextColor(Color.RED);
                    sw_snk.setChecked(false);
                } else {
                    sw_snk.setText("ON");
                    sw_snk.setTextColor(Color.parseColor("#0F7D0F"));
                    sw_snk.setChecked(true);
                }


                if (fd == 1) {
                    sw_din.setText("OFF");
                    sw_din.setTextColor(Color.RED);
                    sw_din.setChecked(false);
                } else {
                    sw_din.setText("ON");
                    sw_din.setTextColor(Color.parseColor("#0F7D0F"));
                    sw_din.setChecked(true);
                }

            }
            /*
            else{
                        sw_brk.setText("ON");
                        sw_brk.setTextColor(Color.parseColor("#0F7D0F"));
                        sw_brk.setChecked(true);
                        sw_lun.setText("ON");
                        sw_lun.setTextColor(Color.parseColor("#0F7D0F"));
                        sw_lun.setChecked(true);
                        sw_snk.setText("ON");
                        sw_snk.setTextColor(Color.parseColor("#0F7D0F"));
                        sw_snk.setChecked(true);
                        sw_din.setText("ON");
                        sw_din.setTextColor(Color.parseColor("#0F7D0F"));
                        sw_din.setChecked(true);

                }
              */
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
                ErrorReadingDatabase();
            }
        });

        sw_brk.setEnabled(true);
        sw_lun.setEnabled(true);
        sw_snk.setEnabled(true);
        sw_din.setEnabled(true);
    }


    /*
    int total_b,total_l,total_s,total_d;
    void UpdateTotalCount(int br, int lu, int sn, int dn){

        mTotalRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equalsIgnoreCase("breakfast")) {
                    total_b = Integer.parseInt(dataSnapshot.getValue().toString());
                } else if (dataSnapshot.getKey().equalsIgnoreCase("lunch")) {
                    total_l = Integer.parseInt(dataSnapshot.getValue().toString());
                } else if (dataSnapshot.getKey().equalsIgnoreCase("snacks")) {
                    total_s = Integer.parseInt(dataSnapshot.getValue().toString());
                } else if (dataSnapshot.getKey().equalsIgnoreCase("dinner")) {
                    total_d = Integer.parseInt(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equalsIgnoreCase("breakfast")) {
                    total_b = Integer.parseInt(dataSnapshot.getValue().toString());
                } else if (dataSnapshot.getKey().equalsIgnoreCase("lunch")) {
                    total_l = Integer.parseInt(dataSnapshot.getValue().toString());
                } else if (dataSnapshot.getKey().equalsIgnoreCase("snacks")) {
                    total_s = Integer.parseInt(dataSnapshot.getValue().toString());
                } else if (dataSnapshot.getKey().equalsIgnoreCase("dinner")) {
                    total_d = Integer.parseInt(dataSnapshot.getValue().toString());
                }
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


        Map<String,Integer> totalmap = new HashMap<>();
        totalmap.put("Breakfast",total_b);
        totalmap.put("Lunch",total_l);
        totalmap.put("Snacks",total_s);
        totalmap.put("Dinner",total_d);

        mTotalRef.child(day + "-" + month + "-" + year).setValue(totalmap);
    }
    */
    String m_b = "* ",m_l = "* ",m_s = "* ",m_d = "* ";
    String dayname;
    void UpdateMenu(){
        m_b = "";m_l = "";m_s = "";m_d = "";
        SimpleDateFormat dateFormatToGetDayName = new SimpleDateFormat("EEEE");
        SimpleDateFormat simpleDateFormatToConvertDate = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date today = simpleDateFormatToConvertDate.parse(day + "/" + month + "/" + year);
            dayname = dateFormatToGetDayName.format(today);
        } catch (ParseException e) {

        }

        datetv.setText(dayname+" , "+day + "-" + month + "-" + year);

        //System.out.println(">>>>>>> MENU DETAILS: res_view_access_level: "+hostelname+" | Dayname: "+dayname);

        mMenuRef.child(hostelname).child(dayname).child("Breakfast").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String common="",nonV="",V="";
                for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                    //   Toast.makeText(MainActivity.this, foodSnapshot.getValue().toString(), Toast.LENGTH_LONG).show();
                    if (foodSnapshot.getKey().toString().equals("Common")) {
                        m_b += ("COMMON:\n" + foodSnapshot.getValue().toString());
                        common = ("COMMON:\n" + foodSnapshot.getValue().toString());
                    } else if (foodSnapshot.getKey().toString().equals("Non-Veg")) {
                        m_b += ("\n\nNON-VEG:\n" + foodSnapshot.getValue().toString());
                        nonV = ("\n\nNON-VEG:\n" + foodSnapshot.getValue().toString());
                    } else if (foodSnapshot.getKey().toString().equals("Veg")) {
                        m_b += ("\n\nVEG:\n" + foodSnapshot.getValue().toString());
                        V = ("\n\nVEG:\n" + foodSnapshot.getValue().toString());
                    }
                }
                ColourMenu(tv_menu_b,m_b,common,nonV,V);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ErrorReadingDatabase();
            }
        });


        mMenuRef.child(hostelname).child(dayname).child("Lunch").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String common="",nonV="",V="";
                for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                    //   Toast.makeText(MainActivity.this, foodSnapshot.getValue().toString(), Toast.LENGTH_LONG).show();
                    if (foodSnapshot.getKey().toString().equals("Common")) {
                        m_l += ("COMMON:\n" + foodSnapshot.getValue().toString());
                        common = ("COMMON:\n" + foodSnapshot.getValue().toString());
                    } else if (foodSnapshot.getKey().toString().equals("Non-Veg")) {
                        m_l += ("\n\nNON-VEG:\n" + foodSnapshot.getValue().toString());
                        nonV = ("\n\nNON-VEG:\n" + foodSnapshot.getValue().toString());
                    } else if (foodSnapshot.getKey().toString().equals("Veg")) {
                        m_l += ("\n\nVEG:\n" + foodSnapshot.getValue().toString());
                        V = ("\n\nVEG:\n" + foodSnapshot.getValue().toString());
                    }
                }
                ColourMenu(tv_menu_l,m_l,common,nonV,V);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ErrorReadingDatabase();
            }
        });


        mMenuRef.child(hostelname).child(dayname).child("Snacks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String common="",nonV="",V="";
                for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                    //   Toast.makeText(MainActivity.this, foodSnapshot.getValue().toString(), Toast.LENGTH_LONG).show();
                    if (foodSnapshot.getKey().toString().equals("Common")) {
                        m_s += ("COMMON:\n" + foodSnapshot.getValue().toString());
                        common = ("COMMON:\n" + foodSnapshot.getValue().toString());
                    } else if (foodSnapshot.getKey().toString().equals("Non-Veg")) {
                        m_s += ("\n\nNON-VEG:\n" + foodSnapshot.getValue().toString());
                        nonV = ("\n\nNON-VEG:\n" + foodSnapshot.getValue().toString());
                    } else if (foodSnapshot.getKey().toString().equals("Veg")) {
                        m_s += ("\n\nVEG:\n" + foodSnapshot.getValue().toString());
                        V = ("\n\nVEG:\n" + foodSnapshot.getValue().toString());
                    }
                }
                ColourMenu(tv_menu_s,m_s,common,nonV,V);
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ErrorReadingDatabase();
            }
        });


        mMenuRef.child(hostelname).child(dayname).child("Dinner").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String common="",nonV="",V="";
                for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                    //   Toast.makeText(MainActivity.this, foodSnapshot.getValue().toString(), Toast.LENGTH_LONG).show();
                    if (foodSnapshot.getKey().toString().equals("Common")) {
                        m_d += ("COMMON:\n" + foodSnapshot.getValue().toString());
                        common = ("COMMON:\n" + foodSnapshot.getValue().toString());
                    } else if (foodSnapshot.getKey().toString().equals("Non-Veg")) {
                        m_d += ("\n\nNON-VEG:\n" + foodSnapshot.getValue().toString());
                        nonV = ("\n\nNON-VEG:\n" + foodSnapshot.getValue().toString());
                    } else if (foodSnapshot.getKey().toString().equals("Veg")) {
                        m_d += ("\n\nVEG:\n" + foodSnapshot.getValue().toString());
                        V = ("\n\nVEG:\n" + foodSnapshot.getValue().toString());
                    }
                }
                ColourMenu(tv_menu_d,m_d,common,nonV,V);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ErrorReadingDatabase();
            }
        });

    }

    void ColourMenu(TextView textView,String CompleteText,String common,String nonV,String V){

        Spannable spannable = new SpannableString(CompleteText);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#02A935")),
                (common.length()+nonV.length()), (common.length()+nonV.length()+V.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")),
                (common.length()), (common.length()+nonV.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")),
                (common.length()), (common.length()+nonV.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannable, TextView.BufferType.SPANNABLE);
    }
}
