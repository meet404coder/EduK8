package com.vision.eduk8;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class E_IDCard extends AppCompatActivity {

    PrefManager prefManager;
    private final static int QRCODEWIDH = 500;

    UserProfile userProfile = new UserProfile();
    String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    ProgressDialog progressDialog;

    ImageView qrView;
    Bitmap qrImageGenerated;
    ProgressBar waitprg;

    TextView tv_name,tv_admno,tv_desig,tv_dep,tv_email,tv_mob,tv_hostel,tv_room,tv_charge, tv_posts;

    String idQRString = "ERROR 404";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_idcard);

        prefManager = new PrefManager(E_IDCard.this);

        tv_name  = (TextView) findViewById(R.id.id_name);
        tv_admno = (TextView) findViewById(R.id.id_admno);
        tv_desig = (TextView) findViewById(R.id.id_desig);
        tv_dep   = (TextView) findViewById(R.id.id_dep);
        tv_email = (TextView) findViewById(R.id.id_email);
        tv_mob   = (TextView) findViewById(R.id.id_mob);
        tv_hostel= (TextView) findViewById(R.id.id_hostel);
        tv_room  = (TextView) findViewById(R.id.id_room);
        tv_charge= (TextView) findViewById(R.id.id_charges);
        tv_posts = (TextView) findViewById(R.id.id_posts);

        tv_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(E_IDCard.this, MyPostsActivity.class));
            }
        });

        progressDialog = new ProgressDialog(E_IDCard.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Contacting Database!");
        progressDialog.setMessage("Please wait a while...");
        progressDialog.show();

        waitprg = (ProgressBar) findViewById(R.id.waitprogressbar1);
        waitprg.getIndeterminateDrawable().setColorFilter(0xFF000000, android.graphics.PorterDuff.Mode.MULTIPLY);

        qrView = (ImageView) findViewById(R.id.qrview);

        DatabaseReference memberProfileRef = FirebaseDatabase.getInstance().getReference(Config.MemberProfileRef);
        memberProfileRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                UserProfile tempprofile = dataSnapshot.getValue(UserProfile.class);

                if(tempprofile.uid.equals(Uid)){
                    userProfile = tempprofile;
                    tv_name.setText(userProfile.name);
                    tv_admno.setText(userProfile.admno);
                    tv_desig.setText(userProfile.designation);
                    tv_dep.setText(userProfile.department);
                    tv_email.setText(userProfile.email);
                    tv_mob.setText(userProfile.mobile);
                    tv_hostel.setText(userProfile.hostel);
                    tv_room.setText(userProfile.room);
                    tv_charge.setText(""+userProfile.charges);
                    idQRString = "(RoboISM):"+userProfile.name+":"+userProfile.admno;
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
                if(progressDialog.isShowing()) {

                    System.out.println(">>>>>>> USERPROFILE: "+userProfile);

                    generateQR(idQRString);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                    Toast.makeText(E_IDCard.this,"Permission Required for loading QR!",Toast.LENGTH_LONG).show();
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
            Picasso.with(E_IDCard.this).load(imageUri).into(qrView);
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
                        saveQRandgetUri(E_IDCard.this, qrImageGenerated);


                    } catch (WriterException e) {
                        Toast.makeText(E_IDCard.this, "Error" + e, Toast.LENGTH_LONG).show();
                    }
                }
            }, 1 * 200);
        }
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

}
