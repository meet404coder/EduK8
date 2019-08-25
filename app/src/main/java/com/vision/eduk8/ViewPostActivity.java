package com.vision.eduk8;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.mail.iap.ByteArray;

public class ViewPostActivity extends AppCompatActivity {

    String title, body, author, tags;
    byte[] byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        Bundle b = getIntent().getBundleExtra("bundle");
        title = b.getString("title");
        body = b.getString("body");
        author = b.getString("author");
        tags = b.getString("tags");
        byteArray = b.getByteArray("bitmap");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        TextView tv1 = findViewById(R.id.tvTitle);
        TextView tv2 = findViewById(R.id.tvBody);
        TextView tv3 = findViewById(R.id.tvTags);
        TextView tv4 = findViewById(R.id.tvAuthor);
        ImageView iv = findViewById(R.id.ivView);

        tv1.setText(title);
        tv2.setText(body);
        tv3.setText(tags);
        tv4.setText(author);
        iv.setImageBitmap(bitmap);
    }
}
