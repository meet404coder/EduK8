package com.vision.eduk8;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.chip.ChipGroup;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import com.github.okdroid.checkablechipview.CheckableChipView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TagsActivity extends AppCompatActivity {

    CheckableChipView chip1, chip2, chip3, chip4, chip5;
    Button submit;
    ArrayList<String> tags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        chip1 = (CheckableChipView) findViewById(R.id.chip1);
        chip2 = (CheckableChipView) findViewById(R.id.chip2);
        chip3 = (CheckableChipView) findViewById(R.id.chip3);
        chip4 = (CheckableChipView) findViewById(R.id.chip4);
        chip5 = (CheckableChipView) findViewById(R.id.chip5);
        submit = findViewById(R.id.submit_tags);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=0;
                if (chip1.isChecked()) {
                    tags.add(chip1.getText().toString());
                    i++;
                }
                if (chip2.isChecked()) {
                    tags.add(chip1.getText().toString());
                    i++;
                }
                if (chip3.isChecked()) {
                    tags.add(chip1.getText().toString());
                    i++;
                }
                if (chip4.isChecked()) {
                    tags.add(chip1.getText().toString());
                    i++;
                }
                if (chip5.isChecked()) {
                    tags.add(chip5.getText().toString());
                    i++;
                }

                // Set tags

                FirebaseDatabase.getInstance().getReference("Notes").child(
                        FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Tags").setValue(tags);

                Intent intent = new Intent(TagsActivity.this, Dashboard.class);
                startActivity(intent);
            }
        });


    }
}
