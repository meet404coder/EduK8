package com.meet404coder.roboism;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class PollingActivity extends AppCompatActivity {

    ListView listView;
    String question[] = {"Question 1 mhfxjhfxjfxjfxjfhxgfxhgfxgfxhxhxjhxgxhgxhgxhfxjjhgchgcjhgchgcjcjhcjhgcjhgc?","Question 2"};
    String options[] =
            {"OP1:OP2","OP_A:OP_B:OP_C"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PollingActivity.this,CreatePoll.class));
            }
        });


        listView = (ListView) findViewById(R.id.polling_list);
        PollingListAdapter gridAdapter = new PollingListAdapter(PollingActivity.this, question, options);
        listView.setAdapter(gridAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        break;
                    }
                }

                //Statements that you want to be executed no matter what has been selected
            }
        });

    }

}
