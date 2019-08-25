package com.vision.eduk8;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ScanActivity extends AppCompatActivity {

    Bitmap photo = null;

    EditText scannedText, tagsText, titleText;
    ImageView scannedImage;
    Button submit;
    String name = " ";
    DatabaseReference mRef;
    Boolean namefetched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        scannedText = (EditText) findViewById(R.id.scannedText);
        tagsText = (EditText) findViewById(R.id.enterTags);
        titleText = (EditText) findViewById(R.id.enterTitle);
        scannedImage = (ImageView) findViewById(R.id.list_img4);
        submit = (Button) findViewById(R.id.submit);

        String uid;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            System.out.println(uid);
            mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child(Config.MemberProfileRef).child(uid).child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue().toString() != null) {
                        name = dataSnapshot.getValue().toString();
                        namefetched = true;
                    }
                    System.out.println(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            //Add code for user not found
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 2);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            scannedImage.setImageBitmap(photo);

            TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

            Frame imageFrame = new Frame.Builder()
                    .setBitmap(photo)
                    .build();

            String imageText = "";

            SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);

            for (int i = 0; i < textBlocks.size(); i++) {
                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                imageText += textBlock.getValue();
            }

            final String finalImageText = imageText;
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uid = null;
                    scannedText.setText(finalImageText);
                    System.out.println(tagsText.getText().toString());
                    String[] tagstr = tagsText.getText().toString().split(",");
                    for (int i = 0; i < tagstr.length; i++) {
                        tagstr[i] = tagstr[i].toLowerCase();
                        System.out.println(tagstr[i]);
                        Toast.makeText(ScanActivity.this, tagstr[i], Toast.LENGTH_SHORT).show();
                    }
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FeedItemData data = new FeedItemData(titleText.getText().toString(),
                                scannedText.getText().toString(),
                                name,
                                tagstr,
                                2,
                                uid);
                        String postId = Calendar.getInstance().getTime().toString();
                        System.out.println(uid);
                        System.out.println(postId);
                        System.out.println(name);

                        mRef.child("Posts").child(postId).setValue(data);
                        mRef.child("RoboISM Members Profile").child(uid).child("Posts").child(postId).setValue(postId);

                        startActivity(new Intent(ScanActivity.this, Dashboard.class));
                    }
                }
            });

            Toast.makeText(this, imageText, Toast.LENGTH_SHORT).show();
        }
    }
}

