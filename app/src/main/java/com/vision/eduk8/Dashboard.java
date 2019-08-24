package com.vision.eduk8;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;


public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Boolean isMember = false;

    PrefManager prefManager;
    FirebaseAuth mAuth;

    ImageView qrView;
    Bitmap qrImageGenerated;
    ProgressBar waitprg;

    Button refreshQrBttn;
    String qrText;

    ProgressDialog progressDialog;
    DatabaseReference mhwRef = FirebaseDatabase.getInstance().getReference(Config.AdminUserHandshakeRef);

    private final static int QRCODEWIDH = 500;



//    ListView poll_lv,meet_lv,req_lv,news_lv,mvp_lv;
//    List<MeetData> meetDataList = new LinkedList<>();
//    List<PollDrafter> pollDataList = new LinkedList<>();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        prefManager = new PrefManager(Dashboard.this);
        waitprg = (ProgressBar) findViewById(R.id.waitprogressbar);
        waitprg.getIndeterminateDrawable().setColorFilter(0xFF000000, android.graphics.PorterDuff.Mode.MULTIPLY);
        refreshQrBttn = (Button) findViewById(R.id.refreshqr_bttn);

        qrView = (ImageView) findViewById(R.id.qrview);

        mAuth = FirebaseAuth.getInstance();
        waitprg.setVisibility(View.VISIBLE);

        prefManager.setFirstTimeLaunch(false);


//        poll_lv = (ListView) findViewById(R.id.polling_list);
//        meet_lv = (ListView) findViewById(R.id.meet_list_view);
//        req_lv = (ListView) findViewById(R.id.req_list_view);
//        news_lv = (ListView) findViewById(R.id.news_list_view);
//        mvp_lv = (ListView) findViewById(R.id.mvp_list_view);

        progressDialog = new ProgressDialog(Dashboard.this);
        progressDialog.setTitle("Contacting Servers...");
        progressDialog.setMessage("Please wait while we Contact our Database...");
        progressDialog.setCancelable(false);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this, ScanActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //Checking the signin method
        int signinmeth = CheckSignInMethod();

        //Returns 1 if phone
        //Returns 0 for Anonymous or otherwise
        //Returns -1 for not run for loop

        if(signinmeth == 1){
            isMember = true;
        }else if(signinmeth == 0){
            isMember = false;
        }

        Toast.makeText(Dashboard.this,"IS MEMBER: "+isMember,Toast.LENGTH_LONG).show();

        meetingLoaderThread.start();
        pollingLoaderThread.start();

        /*
        getHW();

        refreshQrBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                prefManager.setQRsavedPath(null);
                getHW();
            }
        });

        */

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    HWREADPROCEED();

                } else {
                    // permission denied, boo!
                    Toast.makeText(Dashboard.this, "Write Permission Denied.\nPlease Grant Permission to load QR faster.", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_updatedetails) {
            startActivity(new Intent(Dashboard.this,RegisterActivity.class));
            return true;
        }
        if (id == R.id.action_feedback) {
            startActivity(new Intent(Dashboard.this,Feedback.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_isAdmin) {
            String[] options = {"Scan QR", "Compile Data","Open BT Read/Write","Polling","Meet Call"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select an option\n");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == 0){ //Scan QR is selected
                        progressDialog.show();
                        CheckIsAdmin();
                    }else if(which ==1){ //Compile Data is selected
//                        startActivity(new Intent(Dashboard.this,DataCompileActivity.class));
                    }else if(which ==2){ //Compile Data is selected
//                       startActivity(new Intent(Dashboard.this,ReadBTandMarkAttendance.class));
                    }else if(which == 3){
//                        startActivity(new Intent(Dashboard.this,PollingActivity.class));
                    }else if(which == 4){
//                        startActivity(new Intent(Dashboard.this,MeetManagement.class));
                    }
                }
            });
            builder.show();


        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(Dashboard.this, E_IDCard.class));
        } else if (id == R.id.nav_slideshow) {
//            startActivity(new Intent(Dashboard.this, MessOffOption.class));
        } else if (id == R.id.nav_logout) {
            android.app.AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new android.app.AlertDialog.Builder(Dashboard.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new android.app.AlertDialog.Builder(Dashboard.this);
            }
            builder.setTitle("Logout?!")
                    .setMessage("\nAre you sure to logout and exit?.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            prefManager.setFirstTimeLaunch(true);
                            mAuth.signOut();
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }else if(id == R.id.nav_credits){
            final Dialog dialog = new Dialog(Dashboard.this);
            dialog.setContentView(R.layout.credits);
            dialog.setTitle("Hello There !");
            dialog.show();
            dialog.setOnKeyListener(new Dialog.OnKeyListener(){

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if(keyCode==KeyEvent.KEYCODE_BACK){
                        dialog.dismiss();
                    }
                    return true;
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    int clicked = 0;

    int ShowDialog(String title, String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(Dashboard.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
        } else {
            builder = new AlertDialog.Builder(Dashboard.this);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        clicked = 1;
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        clicked = -1;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return clicked;
    }

    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRCODEWIDH, QRCODEWIDH, null
            );
        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }


    public Uri saveQRandgetUri(Context context, Bitmap bitmap) {
        //This function generates as well as saves the QR Code
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "QRCode", null);
                    prefManager.setQRsavedPath(path);
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    return Uri.parse(path);

                } else {
                    Toast.makeText(Dashboard.this,"Permission Required for loading QR!",Toast.LENGTH_LONG).show();
                    //Log.v(TAG,"Permission is revoked");
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
            else { //permission is automatically granted on sdk<23 upon installation
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "QRCode", null);
                prefManager.setQRsavedPath(path);
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                return Uri.parse(path);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        return Uri.parse("android.resource://com.vision.eduk8/drawable/internet_error.png");
    }


    String qrtext;

    void generateQR(String text) {

        qrtext = text;

        if (prefManager.getsetQRsavedPath() != null) {
            Uri imageUri = Uri.parse(prefManager.getsetQRsavedPath());
            Picasso.with(Dashboard.this).load(imageUri).into(qrView);
            waitprg.setVisibility(View.GONE);

        } else {
            Handler oooHandler = new Handler();
            oooHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {

                        qrImageGenerated = TextToImageEncode(qrtext);
                        qrView.setImageBitmap(qrImageGenerated);
                        waitprg.setVisibility(View.GONE);
                        if(progressDialog.isShowing()){
                        progressDialog.dismiss();}
                        saveQRandgetUri(Dashboard.this, qrImageGenerated);


                    } catch (WriterException e) {
                        Toast.makeText(Dashboard.this, "Error" + e, Toast.LENGTH_LONG).show();
                    }
                }
            }, 1 * 200);
        }
    }


    void HandleNetError() {
        //TODO:: Fill this function to handle the net related errors
        if(!Dashboard.this.isFinishing()) {
            final Dialog dialog = new Dialog(Dashboard.this);
            dialog.setContentView(R.layout.dialog_internet_error);
            dialog.setTitle("Attention !");
            dialog.show();
        }
    }

    AdminUserColabData adminUserColabData = new AdminUserColabData();

    void CheckIsAdmin() {
        DatabaseReference mAdminUserColabandAndIsAdminRef = FirebaseDatabase.getInstance().getReference(Config.AdminUserHandshakeRef);
        mAdminUserColabandAndIsAdminRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        adminUserColabData = dataSnapshot.getValue(AdminUserColabData.class);
                        if (adminUserColabData.isadmin) {
                            //user is Admin, Open Scanner
                            progressDialog.dismiss();
//                            startActivity(new Intent(Dashboard.this, QrScanningActivity.class));
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Dashboard.this, "You Are Not Authorised!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        HandleNetError();
                    }
                });
    }

    String hw;
    String getHW() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mhwRef.child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equalsIgnoreCase("hotword")) {
                    hw =  dataSnapshot.getValue().toString();
                    HWREADPROCEED();
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

        return hw;
    }

    void ErrorContactingDatabase(DatabaseError e) {
        if(!Dashboard.this.isFinishing()){
        Toast.makeText(Dashboard.this, "ERROR DB: " + e, Toast.LENGTH_LONG).show();
        final Dialog dialog = new Dialog(Dashboard.this);
        dialog.setContentView(R.layout.dialog_internet_error);
        dialog.setTitle("Attention !");
        dialog.show();
        }
    }

    void HWREADPROCEED(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        qrText = uid + "::" + hw;
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    generateQR(qrText);

                } else {
                    //Log.v(TAG,"Permission is revoked");
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
            else { //permission is automatically granted on sdk<23 upon installation
                generateQR(qrText);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    int sign_method = -1;
    int CheckSignInMethod(){

        //Returns 1 if phone
        //Returns 0 for Anonymous or otherwise
        //Returns -1 for not run for loop

        for(UserInfo userInfo:mAuth.getCurrentUser().getProviderData()){
            if(userInfo.getProviderId().equalsIgnoreCase("phone")){
                //user has signed in using phone
                sign_method = 1;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(Config.MembersMobRef);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot ds1:dataSnapshot.getChildren()){
                        for (DataSnapshot ds : ds1.getChildren()) {
                            System.out.println(">>ACCESS CHK>>>> " + ds.getValue().toString());
                            if (ds.getKey().equalsIgnoreCase("Status")) {
                                if (ds.getValue().toString().equalsIgnoreCase("Allowed")) {
                                    //Allow user to access the app
                                } else if (ds.getValue().toString().equals("Restricted")) {
                                    //logout user and display a warning
                                    android.app.AlertDialog.Builder builder;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        builder = new android.app.AlertDialog.Builder(Dashboard.this, android.R.style.Theme_DeviceDefault);
                                    } else {
                                        builder = new android.app.AlertDialog.Builder(Dashboard.this);
                                    }
                                    builder.setTitle("Access Revoked!")
                                            .setMessage("\nYour access to this app has been revoked.\n" +
                                                    "\nKindly contact the club officials ASAP!")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // continue with logout
                                                    mAuth.signOut();
                                                    System.exit(0);
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }
                            }
                        }
                    }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            }
            else{
                sign_method = 0;
            }
        }
        return sign_method;
    }

    Thread meetingLoaderThread = new Thread(new Runnable() {
        @Override
        public void run() {
//            meetDataList.clear();
//            DatabaseReference db = FirebaseDatabase.getInstance().getReference(Config.MeetsRef);
//            db.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    meetDataList.clear();
//                    for(DataSnapshot ds1:dataSnapshot.getChildren()) {
//                        for(DataSnapshot ds:ds1.getChildren()) {
//                            MeetData meetData = ds.getValue(MeetData.class);
//                            if (!meetDataList.contains(meetData)) {
//                                meetDataList.add(meetData);
//                            }
//                        }
//                    }
//
//                    //Done reading meets data, Proceed to use this info
//                    DoneLoadingMeetData();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
        }
    });
//
//    void DoneLoadingMeetData(){
//        for (int i = 0; i < meetDataList.size(); i++) {
//            System.out.println(">>>> DASH MEET DATA: " + meetDataList.get(i).meetID);
//        }
//    }


    Thread pollingLoaderThread = new Thread(new Runnable() {
        @Override
        public void run() {
//            pollDataList.clear();
//            DatabaseReference db = FirebaseDatabase.getInstance().getReference(Config.PollingDrafterRef);
//            db.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    pollDataList.clear();
//                    for(DataSnapshot ds1:dataSnapshot.getChildren()) {
//                        for(DataSnapshot ds:ds1.getChildren()) {
//                            PollDrafter pollData = ds.getValue(PollDrafter.class);
//                            if (!pollDataList.contains(pollData)) {
//                                pollDataList.add(pollData);
//                            }
//                        }
//                    }
//
//                    //Done reading meets data, Proceed to use this info
//                    DoneLoadingPollData();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
        }
    });

//    void DoneLoadingPollData(){
//        for (int i = 0; i < pollDataList.size(); i++) {
//            System.out.println(">>>> DASH POLL DATA: " + pollDataList.get(i).quesID);
//        }
//    }
}
