package com.vision.eduk8;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyPostsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ListView lv;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        lv =  findViewById(R.id.myposts);

        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid();

        final ArrayList<FeedItemData> mFeedList = new ArrayList<>();
        final ArrayList<String> mpidList = new ArrayList<>();
        final FeedAdapter mFeedAdapter = new FeedAdapter(this, mFeedList);

        lv.setAdapter(mFeedAdapter);
        mFeedAdapter.notifyDataSetChanged();

        mRef.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               /* Toast.makeText(Dashboard.this, dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                Log.e("Lullz", dataSnapshot.getKey());*/
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    System.out.println("Lullz " + ds.getKey());
                    try {
                        System.out.println(ds.getValue().toString());
                        final FeedItemData fid = ds.getValue(FeedItemData.class);
                        if (fid != null ) {
                            System.out.println(fid.uid);
                            System.out.println(mUid);
                            final int[] upvoted = new int[1];
                            mRef.child(Config.MemberProfileRef).child(fid.uid).child("Posts").child(fid.mPid).child("upvoted").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapsh) {
                                    upvoted[0] = Integer.parseInt(dataSnapsh.getValue().toString());
                                    fid.likedStatus = upvoted[0];
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                                if (!mpidList.contains(fid.mPid)) {
                                    mpidList.add(fid.mPid);
                                    mFeedList.add(fid);
                                }
                            /*    else {
                                    mFeedList.remove(mPids.lastIndexOf(fid.mPid));
                                    mFeedList.add(mPids.lastIndexOf(fid.mPid), fid);
                                }*/



                            mFeedAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }

        });
    }
}
