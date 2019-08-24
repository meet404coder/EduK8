package com.meet404coder.roboism;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ReadBTandMarkAttendance extends AppCompatActivity {

    public int year, month, day;
    public long server_epoch;
    static boolean tag_found = false;
    
    EditText et_data_list,et_data_rec;
    RelativeLayout bt_options_lay,server_options_lay;

    TextView tv_date;

    ProgressDialog progressDialog;
    TextView myLabel;
    EditText myTextbox;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    LinkedTagToMember TAGData;

    ProgressDialog wait;
    static List<String> tagsList;

    PrefManagerForAttendance prefManager;

    static int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_bt_mark_attendance);

        prefManager = new PrefManagerForAttendance(ReadBTandMarkAttendance.this);
        tagsList = new LinkedList<>();

        progressDialog = new ProgressDialog(ReadBTandMarkAttendance.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Contacting Servers!");
        progressDialog.setMessage("Please wait a while...");

        final Button openButton = (Button) findViewById(R.id.open);
        Button sendButton = (Button) findViewById(R.id.send);
        Button closeButton = (Button) findViewById(R.id.close);
        myLabel = (TextView) findViewById(R.id.label);
        myTextbox = (EditText) findViewById(R.id.entry);


        
        
        //Setting Up the server_mode options
        server_options_lay = (RelativeLayout) findViewById(R.id.server_mode);
        server_options_lay.setVisibility(View.VISIBLE);
        Button bttn_realtime = (Button) findViewById(R.id.realtime_bttn);
        Button bttn_offline = (Button) findViewById(R.id.offline_bttn);
        ImageView im = (ImageView) findViewById(R.id.logoim);
        Picasso.with(ReadBTandMarkAttendance.this).load(R.drawable.blk_logo).fit().into(im);

        //Setting Up the options_bt 
        bt_options_lay = (RelativeLayout) findViewById(R.id.options_bt);
        bt_options_lay.setVisibility(View.GONE);
        tv_date = (TextView) findViewById(R.id.tv_info_date);
        final Button bttn_close_conn = (Button) findViewById(R.id.bt_close_connect_bttn);
        Button bttn_ready = (Button) findViewById(R.id.bt_ready_bttn);
        Button bttn_master_cmd = (Button) findViewById(R.id.bt_master_cmd_bttn);
        Button bttn_save = (Button) findViewById(R.id.bt_save_list);
        Button bttn_upload = (Button) findViewById(R.id.bt_upload_list);
        ImageView im2 = (ImageView) findViewById(R.id.logoim1);
        Picasso.with(ReadBTandMarkAttendance.this).load(R.drawable.blk_logo).fit().into(im2);
        et_data_list = (EditText) findViewById(R.id.data_list_et);
        et_data_rec = (EditText) findViewById(R.id.data_rec);


        //Setting the default date as today
        readSystemForDate();
        tv_date.setText("DATE: "+day+"/"+month+"/"+year);


        //Checking the mode selected
        bttn_realtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = 0;SetLayoutForBT();
            }
        });

        bttn_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = 1;SetLayoutForBT();
            }
        });

        bttn_close_conn.setVisibility(View.GONE);

        final Button bttn_open = (Button) findViewById(R.id.bt_open_bttn);
        bttn_open.setVisibility(View.VISIBLE);
        bttn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wait = new ProgressDialog(ReadBTandMarkAttendance.this);
                wait.setTitle("Connecting...");
                wait.setCancelable(false);
                wait.show();
                findBT(mode);
                bttn_open.setVisibility(View.GONE);
                bttn_close_conn.setVisibility(View.VISIBLE);
            }
        });


        bttn_master_cmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAccessLevelandInitateConfig();
            }
        });

        //Save The list
        bttn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag_list = et_data_list.getText().toString();
                String date = day+"/"+month+"/"+year;
                prefManager.saveListToPref(date,tag_list);
                Toast.makeText(ReadBTandMarkAttendance.this,"Attendance for "+date+" saved!",Toast.LENGTH_LONG).show();
            }
        });

        //To Upload the data to firebase

        bttn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ReadBTandMarkAttendance.this);
                SharedPreferences sharedPreferences = ReadBTandMarkAttendance.this.getSharedPreferences(prefManager.PREF_NAME,
                        Context.MODE_PRIVATE);
                Map<String,?> entries = sharedPreferences.getAll();
                final Set<String> keys = entries.keySet();
                final String[] options = keys.toArray(new String[keys.size()]);
                builder.setTitle("Select the Date to Upload\n");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String dateSelected = options[which];
                        String rawData = prefManager.getTagList(dateSelected);
                        String[] tagsToUpload = rawData.split("\n");
                        progressDialog.setMessage("Uploading....\nPlease Wait.");
                        progressDialog.show();
                        for(String tag : tagsToUpload){
                            UploadTags(tag);
                        }
                        progressDialog.dismiss();
                        Toast.makeText(ReadBTandMarkAttendance.this,"Attendance Uploaded!",Toast.LENGTH_LONG).show();
                    }
                });
                builder.show();
            }
        });


        //To Change Date
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReadBTandMarkAttendance.this,
                        datePickerListener, year, month, day);

                readSystemForDate();
                datePickerDialog.getDatePicker().init(year,month,day,datePickerDialog);
                datePickerDialog.setCancelable(true);
                datePickerDialog.show();
            }
        });

        //Ping the BT device
        bttn_ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendData("RR");
                    Toast.makeText(ReadBTandMarkAttendance.this,"Pinged!",Toast.LENGTH_LONG).show();
                } catch (IOException e) {

                }
            }
        });

        //Open Button
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findBT(0);
            }
        });

        //Send Button
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendData(myTextbox.getText().toString());
                } catch (IOException ex) {
                }
            }
        });

        //Close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    closeBT();
                } catch (IOException ex) {
                }
            }
        });

        bttn_close_conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    closeBT();
                    bttn_close_conn.setVisibility(View.GONE);
                    bttn_open.setVisibility(View.VISIBLE);
                } catch (IOException ex) {
                }
            }
        });

        bttn_open.setVisibility(View.VISIBLE);
    }


    Set<BluetoothDevice> pairedDevices;

    static int mde = 0;
    void findBT(int mode) {
    mde = mode;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            myLabel.setText("No bluetooth adapter available");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        } else {

            pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {

                final String[] options = new String[pairedDevices.size()];
                int i = 0;
                for (BluetoothDevice dev : pairedDevices) {
                    options[i] = dev.getName();
                    i++;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select the Paired Device\n");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (BluetoothDevice device : pairedDevices) {
                            if (device.getName().equals(options[which])) {
                                mmDevice = device;
                                try {
                                    Method getUUidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
                                    try {
                                        ParcelUuid[] uuids = (ParcelUuid[]) getUUidsMethod.invoke(mBluetoothAdapter, null);
                                        for (ParcelUuid uuid : uuids) {
                                            //System.out.println(">>>>>>>>> UUID: " + uuid);
                                        }
                                    } catch (IllegalAccessException e) {


                                    } catch (InvocationTargetException e) {

                                    }
                                } catch (NoSuchMethodException e) {

                                }


                                try {
                                    openBT();
                                } catch (IOException e) {

                                }
                                break;
                            }
                        }
                    }
                });
                builder.show();
            } else {
                android.app.AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new android.app.AlertDialog.Builder(ReadBTandMarkAttendance.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new android.app.AlertDialog.Builder(ReadBTandMarkAttendance.this);
                }
                builder.setTitle("No Paired Device Found!!")
                        .setMessage("\nPlease pair the device and then open the app.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }

    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        if(wait.isShowing()){
            wait.dismiss();
        }

        Toast.makeText(ReadBTandMarkAttendance.this,"Connected and Synced with the device!",Toast.LENGTH_LONG).show();
        myLabel.setText("Bluetooth Opened");

        sendData("R");
    }

    void beginListenForData() {
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    if(mde == 0) {
                                        //realtime
                                        check_isTagRegistered(data);
                                    }else if(mde == 1){
                                        //Offline
                                        if(!data.equals("\n")) {
                                            SetDataIntoFields(data);
                                        }
                                    }

                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData(String msg) throws IOException {
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        myLabel.setText("Data Sent");
    }

    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        myLabel.setText("Bluetooth Closed");
    }


    static String TAG_SCANNED;

    void TAGReceived(String TAG) {
        final String tag_rec = TAG.trim();
        DatabaseReference memberProfileRef = FirebaseDatabase.getInstance().getReference(Config.MemberProfileTAGDataRef);
        memberProfileRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                LinkedTagToMember tempdata = dataSnapshot.getValue(LinkedTagToMember.class);

                String tag = tempdata.tagID.trim();

                TAG_SCANNED = tag;

                if (tag.equals(tag_rec)) {
                    TAGData = tempdata;
                    tag_found = true;
                    //GOT the details of the person having that TAG
                    //Now mark the attendance
                    //readserverforDate();
                    ProceedToMarkAttendance();
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

        //As Value Event Listner is Gaurenteed to be executed after the child event lisner hence i use it
        //as a method to check if all the data has been read and disable the progressdialog
        memberProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                /*
                if(!tag_found){
                    try {
                        sendData("NR");
                    } catch (IOException e) {

                    }
                }
                */
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void ErrorReadingDatabase() {
        //TODO:: Complete this function
        final Dialog dialog = new Dialog(ReadBTandMarkAttendance.this);
        dialog.setContentView(R.layout.dialog_internet_error);
        dialog.setTitle("Attention !");
        dialog.show();
    }

    public void readserverforDate() {
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

                ProceedToMarkAttendance();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ErrorReadingDatabase();
            }
        });

        tref.setValue(ServerValue.TIMESTAMP);
    }

    void readSystemForDate(){
        Date today = Calendar.getInstance().getTime();

        SimpleDateFormat ddsimpleDateFormat = new SimpleDateFormat("dd");
        SimpleDateFormat MMsimpleDateFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yyyysimpleDateFormat = new SimpleDateFormat("yyyy");

        day = Integer.parseInt(ddsimpleDateFormat.format(today));
        month = Integer.parseInt(MMsimpleDateFormat.format(today));
        year = Integer.parseInt(yyyysimpleDateFormat.format(today));
    }

    void ProceedToMarkAttendance() {
        String date = day + "/" + month + "/" + year;
        AttendanceEntry attendanceEntry = new AttendanceEntry(date, TAGData.uid, TAGData.name, TAGData.admno);

        //firebase data store
        DatabaseReference memberProfileRef = FirebaseDatabase.getInstance().getReference(Config.AttendanceRef);
        memberProfileRef.child(date.replace('/', '-')).child(attendanceEntry.uid).setValue(attendanceEntry).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Susseccfully Marked Member's Attendance
                    try {
                        markedAttendance();
                    } catch (IOException e) {

                    }
                } else {
                    //User Data not stored
                    try {
                        notMarkedAttendance();
                    } catch (IOException e) {

                    }
                }
            }
        });

    }

    void markedAttendance() throws IOException {

        if (!tagsList.contains(TAG_SCANNED)) {
            tagsList.add(TAG_SCANNED);
            if(mde == 0) {
                sendData("SR");
            }
        }

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    void notMarkedAttendance() throws IOException {
        if(mde == 0) {
            sendData("UR");
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast.makeText(ReadBTandMarkAttendance.this, "Check Internet Connection!", Toast.LENGTH_LONG).show();
    }


    void check_isTagRegistered(String Tag) {
        final String tag_rec = Tag.trim();
        DatabaseReference memberProfileRef = FirebaseDatabase.getInstance().getReference(Config.MemberProfileTAGDataRef);
        memberProfileRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                LinkedTagToMember tempdata = dataSnapshot.getValue(LinkedTagToMember.class);

                String tag = tempdata.tagID.trim();

                if (tag.equals(tag_rec)) {
                    //The tag is registered!
                    if (!tagsList.contains(tag_rec)) {
                        TAGReceived(tag_rec);
                    }else{
                        //If already marked attendance for the same session
                        try {
                            sendData("DR");
                        } catch (IOException e) {

                        }
                    }
                }else{
                    try {
                        sendData("NR");
                    } catch (IOException e) {

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

            }
        });
    }

    void SetLayoutForBT(){
        bt_options_lay.setVisibility(View.VISIBLE);
        server_options_lay.setVisibility(View.GONE);
    }

    void SetDataIntoFields(String data){
        final String dataR = data;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Stuff that updates the UI
                et_data_rec.setText(dataR);

                if(et_data_list.getText().toString().toLowerCase().contains("data appears here")){
                    et_data_list.setText("");
                }

                et_data_list.append(dataR+"\n");
                try {
                    sendData("SR");
                } catch (IOException e) {

                }
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


    void UploadTags(String tag){

        DatabaseReference memberProfileRef = FirebaseDatabase.getInstance().getReference(Config.MemberProfileTAGDataRef);
        memberProfileRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                LinkedTagToMember tempdata = dataSnapshot.getValue(LinkedTagToMember.class);

                String tag = tempdata.tagID.trim();

                if (tag.equals(tag)) {
                    //The tag is registered!
                        TAGReceived(tag);
                }else{

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

    static int accessLevel = -1;
    void getAccessLevelandInitateConfig(){
        DatabaseReference memberProfileRef = FirebaseDatabase.getInstance().getReference(Config.MemberProfileTAGDataRef);
        memberProfileRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                LinkedTagToMember tempdata = dataSnapshot.getValue(LinkedTagToMember.class);
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (uid.equals(tempdata.uid)) {
                    try {
                        accessLevel = tempdata.accessLevel;
                        if(accessLevel>=Config.ALLOW_BT_DEVICE_CONFIG_ABOVE){

                            AlertDialog.Builder builder  = new AlertDialog.Builder(ReadBTandMarkAttendance.this);
                            builder.setTitle("Enter Data to Send\n");
                            final EditText input = new EditText(ReadBTandMarkAttendance.this);
                            builder.setView(input);
                            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        sendData(input.getText().toString());
                                    } catch (IOException e) {

                                    }
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                        }else{
                            Toast.makeText(ReadBTandMarkAttendance.this,"Need Higher Access Level to Enter this Segment!",Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){

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

            }
        });
    }
}
