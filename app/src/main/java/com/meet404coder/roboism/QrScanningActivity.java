package com.meet404coder.roboism;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class QrScanningActivity extends AppCompatActivity {


    public int year, month, day;
    public long server_epoch;
    int timeUpdatedFlag = 0;

    String UidformQR, hotwordfromQR;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(Config.MessOffDatabaseRef);
    DatabaseReference mRefAdmin = FirebaseDatabase.getInstance().getReference(Config.AdminQRread);
    DatabaseReference mhwRef = FirebaseDatabase.getInstance().getReference(Config.AdminUserHandshakeRef);
    MessOffData messOffData;
    AdminUserColabData adminUserColabData;
    Intent StarterIntent;

    int found_flag = 0;

    static String e = "NONE";


    ProgressDialog progressDialog;
    SurfaceView cameraPreview;
    TextView txtResult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    Boolean autoFocusEnabled = Boolean.TRUE;
    final int RequestCameraPermissionID = 1001;

    QRreadDataStore qRreadDataStore;

    RelativeLayout qrlay, lay_b, lay_l, lay_s, lay_d, all_lay_holder;

    CheckBox cb_b, cb_l, cb_s, cb_d;

    String[] extrasIDs = {};
    String messOffDataStr, extrasIDString = "";

    Animation mAnimation;
    String qrValue = "";

    Button conf_bttn;

    QRreadDataStore qRreadDataStoreFetched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StarterIntent = getIntent();
        setContentView(R.layout.activity_qr_scanning);

        final BTControl btControl = new BTControl(QrScanningActivity.this);

        btControl.enableBT();

        Map<String,String> Dev = btControl.getPairedBTDevices();
        Dev.keySet(); // Gives all the addresses
        for(String ADDRESS: Dev.keySet()) {
            Dev.get(ADDRESS); //Gives the device name
        }

        btControl.setDevice(AddressOfDevice);

        try {
            btControl.openBTSerial();
        } catch (IOException e1) {

        }
        btControl.beginListenForData(new Runnable() {
            @Override
            public void run() {
                System.out.println(">>>DATA GOT FROM BT"+btControl.DataReceived);
            }
        });

        conf_bttn = (Button) findViewById(R.id.bttn_confirm);

        progressDialog = new ProgressDialog(QrScanningActivity.this);
        progressDialog.setTitle("Contacting Server...");
        progressDialog.setMessage("Please wait while we contact our servers");
        progressDialog.setCancelable(false);

        qRreadDataStoreFetched = new QRreadDataStore();
        qRreadDataStore = new QRreadDataStore();

        ImageView qrlineimage = (ImageView) findViewById(R.id.qrlineimgv);

        messOffData = new MessOffData();
        adminUserColabData = new AdminUserColabData();

        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
        txtResult = (TextView) findViewById(R.id.txtResult);
        qrlay = (RelativeLayout) findViewById(R.id.qrpreview);

        lay_b = (RelativeLayout) findViewById(R.id.rel1);
        lay_l = (RelativeLayout) findViewById(R.id.rel2);
        lay_s = (RelativeLayout) findViewById(R.id.rel3);
        lay_d = (RelativeLayout) findViewById(R.id.rel4);
        all_lay_holder = (RelativeLayout) findViewById(R.id.alllayholder);

        cb_b = (CheckBox) findViewById(R.id.list_desc);
        cb_l = (CheckBox) findViewById(R.id.list_desc2);
        cb_s = (CheckBox) findViewById(R.id.list_desc3);
        cb_d = (CheckBox) findViewById(R.id.list_desc4);

        all_lay_holder.setVisibility(View.GONE);
        lay_b.setVisibility(View.GONE);
        lay_l.setVisibility(View.GONE);
        lay_s.setVisibility(View.GONE);
        lay_d.setVisibility(View.GONE);


        cb_b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_b.setTextColor(Color.parseColor("#02A935"));
                    cb_b.setText("TAKEN");
                } else {
                    cb_b.setTextColor(Color.parseColor("#9C27B0"));
                    cb_b.setText("PENDING");
                }
            }
        });

        cb_l.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_l.setTextColor(Color.parseColor("#02A935"));
                    cb_l.setText("TAKEN");
                } else {
                    cb_l.setTextColor(Color.parseColor("#9C27B0"));
                    cb_l.setText("PENDING");
                }
            }
        });

        cb_s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_s.setTextColor(Color.parseColor("#02A935"));
                    cb_s.setText("TAKEN");
                } else {
                    cb_s.setTextColor(Color.parseColor("#9C27B0"));
                    cb_s.setText("PENDING");
                }
            }
        });

        cb_d.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_d.setTextColor(Color.parseColor("#02A935"));
                    cb_d.setText("TAKEN");
                } else {
                    cb_d.setTextColor(Color.parseColor("#9C27B0"));
                    cb_d.setText("PENDING");
                }
            }
        });


        conf_bttn.setEnabled(false);
        qRreadDataStoreFetched = new QRreadDataStore();

        conf_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                int b2 = CheckState(cb_b);
                int l2 = CheckState(cb_l);
                int s2 = CheckState(cb_s);
                int d2 = CheckState(cb_d);

                qRreadDataStore = new QRreadDataStore(uid, day + "/" + month + "/" + year, "0000", "NONE", "SCANNED");
                qRreadDataStore.date = day + "/" + month + "/" + year;
                qRreadDataStore.messOfDataEaten = "" + b2 + l2 + s2 + d2;
                qRreadDataStore.uid = uid;
                qRreadDataStore.extrasTaken = "UNDER DEVELOPMENT";


                mRefAdmin.child(uid).child(day + "-" + month + "-" + year).setValue(qRreadDataStore, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        finish();
                        //startActivity(StarterIntent);
                        startActivity(new Intent(QrScanningActivity.this, QrScanningActivity.class));
                        //finish();
                    }
                });


            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        ImageView qrover = (ImageView) findViewById(R.id.qroverlayiv);
        Picasso.with(QrScanningActivity.this).load(R.drawable.qroverlay).fit().into(qrover);

        //Animate Line for QR
        mAnimation = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 1.0f);
        mAnimation.setDuration(1000);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());

        qrlineimage.setAnimation(mAnimation);


        cameraPreview.getLayoutParams().height = (int) (16 * displayMetrics.widthPixels) / 9;
        // cameraPreview.requestLayout();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(autoFocusEnabled)
                .setRequestedPreviewSize(displayMetrics.widthPixels, displayMetrics.heightPixels)
                .build();

     /* cameraSource =
      cameraSource.Builder = new CameraSource.Builder(this, barcodeDetector)
              .setAutoFocusEnabled(TRUE)
              .setFacing(1); */

        //Add Event
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request permission
                    ActivityCompat.requestPermissions(QrScanningActivity.this,
                            new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();

            }
        });


        barcodeDetector.setProcessor(new com.google.android.gms.vision.Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(com.google.android.gms.vision.Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if (qrcodes.size() != 0) {
                    txtResult.post(new Runnable() {
                        @Override
                        public void run() {
                            //Create vibrate
                            //Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            //vibrator.vibrate(1000);
                            ToneGenerator beep = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                            beep.startTone(ToneGenerator.TONE_CDMA_PIP, 200);
                            txtResult.setText(qrcodes.valueAt(0).displayValue);
                            cameraSource.stop();
                            qrlay.setVisibility(View.GONE);
                            //ADD PROGRESS DIALOG HERE
                            progressDialog.show();
                            qrValue = qrcodes.valueAt(0).displayValue.toString();
                            String[] tempArr = qrValue.split(Config.Splitter);
                            //Toast.makeText(QrScanningActivity.this, ">>>>> QR: " + qrValue + " | VALS: " + Arrays.toString(tempArr), Toast.LENGTH_LONG).show();

                            try {
                                UidformQR = tempArr[0];
                                hotwordfromQR = tempArr[1];
                                //Toast.makeText(QrScanningActivity.this, "UID: " + UidformQR + " HW: " + hotwordfromQR, Toast.LENGTH_LONG).show();
                                readserverfortime();
                                //Toast.makeText(QrScanningActivity.this, ">>>>>READ MessData: " + messOffDataStr + " | extraID: " + "extrasIDs.length" + " | hotWord: " + hotwordfromQR
                                //        + " | QR: " + UidformQR, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(QrScanningActivity.this, "ERROR: " + e, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }


    void LoadData() {
        mRef.child(UidformQR).child(day + "-" + month + "-" + year).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //System.out.println(">>>>> DATA into Child Added1: " + dataSnapshot.getValue());
                //messOffData = dataSnapshot.getValue(MessOffData.class);
                //System.out.println(">>>>> DATA in LOADDATA: "+ds.getValue());
                if (dataSnapshot.getKey().equalsIgnoreCase("messOfData")) {
                    messOffDataStr = dataSnapshot.getValue().toString();
                } else if (dataSnapshot.getKey().equalsIgnoreCase("extras")) {
                    extrasIDString = dataSnapshot.getValue().toString();
                }
                getHW();
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

    void Proceed(String HotwordFetched) {
        if (HotwordFetched.equals(hotwordfromQR)) {
            found_flag = 1;
            //Successful Scan, User Verified
            //messOffDataStr = messOffData.messOfData;
            //String extrasIDString = messOffData.extras;
            if (extrasIDString != null) {
                extrasIDs = extrasIDString.split(":");
            }

            int fb = 0, fl = 0, fs = 0, fd = 0;
            fb = Integer.parseInt("" + messOffDataStr.charAt(0));
            fl = Integer.parseInt("" + messOffDataStr.charAt(1));
            fs = Integer.parseInt("" + messOffDataStr.charAt(2));
            fd = Integer.parseInt("" + messOffDataStr.charAt(3));

            MakeLayoutVisible(lay_b, fb);
            MakeLayoutVisible(lay_l, fl);
            MakeLayoutVisible(lay_s, fs);
            MakeLayoutVisible(lay_d, fd);

            //TODO: Do Whatever From Here
            System.out.println(">>>>> PROCEED MessData: " + messOffDataStr + " | extraID: " + Arrays.toString(extrasIDs)
                    + " | hotWord: " + hotwordfromQR + " | QR: " + UidformQR);

            makeInitialState();
            conf_bttn.setEnabled(true);
        }
    }

    void ErrorContactingDatabase(DatabaseError e) {
        Toast.makeText(QrScanningActivity.this, "ERROR DB: " + e, Toast.LENGTH_LONG).show();
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

    void getHW() {

        mhwRef.child(UidformQR).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equalsIgnoreCase("hotword")) {
                    if (found_flag == 0) {
                        Proceed(dataSnapshot.getValue().toString());
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
                ErrorContactingDatabase(databaseError);
            }
        });
    }

    void MakeLayoutVisible(RelativeLayout relativeLayout, int f) {
        if (f == 0) {
            all_lay_holder.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.VISIBLE);
        } else {
            relativeLayout.setVisibility(View.GONE);
        }
    }

    int CheckState(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            return 1;
        } else {
            return 0;
        }
    }

    void makeInitialState(){

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        mRefAdmin.child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                int keyCount = 0;
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String date = "";
                String messOfDataEaten = "";
                String extrasTaken = "";
                String status = "";


                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equalsIgnoreCase("uid")) {
                        uid = ds.getValue().toString();
                        keyCount++;
                    } else if (ds.getKey().equalsIgnoreCase("date")) {
                        date = ds.getValue().toString();
                        keyCount++;
                    } else if (ds.getKey().equalsIgnoreCase("messOfDataEaten")) {
                        messOfDataEaten = ds.getValue().toString();
                        keyCount++;
                    } else if (ds.getKey().equalsIgnoreCase("extrasTaken")) {
                        extrasTaken = ds.getValue().toString();
                        keyCount++;
                    } else if (ds.getKey().equalsIgnoreCase("status")) {
                        status = ds.getValue().toString();
                        keyCount++;
                    }

                    if (keyCount == 4) {
                        keyCount = 0;
                        QRreadDataStore qRreadDataStoreFetched1 = new QRreadDataStore(uid, date, messOfDataEaten, extrasTaken, status);

                        System.out.println(">>>>>>>> "+qRreadDataStoreFetched1);

                        if (qRreadDataStoreFetched1.date.equals(day + "/" + month + "/" + year)) {

                            if (!(qRreadDataStoreFetched1.messOfDataEaten == null)
                                    || !(qRreadDataStoreFetched1.messOfDataEaten.length() != 4)) {
                                String sf = qRreadDataStoreFetched1.messOfDataEaten;

                                int b1 = Integer.parseInt("" + sf.charAt(0));
                                int l1 = Integer.parseInt("" + sf.charAt(1));
                                int s1 = Integer.parseInt("" + sf.charAt(2));
                                int d1 = Integer.parseInt("" + sf.charAt(3));

                                if (b1 == 1) {cb_b.setChecked(true); cb_b.setEnabled(false);}
                                if (l1 == 1) {cb_l.setChecked(true); cb_l.setEnabled(false);}
                                if (s1 == 1) {cb_s.setChecked(true); cb_s.setEnabled(false);}
                                if (d1 == 1) {cb_d.setChecked(true); cb_d.setEnabled(false);}
                            }
                            if (!(qRreadDataStoreFetched1.extrasTaken == null)
                                    || !(qRreadDataStoreFetched1.extrasTaken.length() < 3)) {
                                e = qRreadDataStoreFetched1.extrasTaken;
                            }

                            progressDialog.dismiss();
                            break;
                        }
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
                ErrorContactingDatabase(databaseError);
            }
        });

    }
}


