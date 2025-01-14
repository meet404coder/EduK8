package com.vision.eduk8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FeedAdapter extends ArrayAdapter<FeedItemData> {

    ArrayList<FeedItemData> dataList;
    Context mContext;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    public FeedAdapter(@NonNull Context context, ArrayList<FeedItemData> data) {
        super(context, R.layout.feed_item, data);
        mContext = context;
        dataList = data;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public FeedItemData getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FeedItemData viewData = dataList.get(position);
        final ViewHolder holder;

        final View resultView;

        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.feed_item, parent, false);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.ftxt_tv_title);
            holder.txtBody = (TextView) convertView.findViewById(R.id.ftxt_tv_body);
            holder.txtAuthor = (TextView) convertView.findViewById(R.id.ftxt_tv_author);
            holder.ivThumbsUp = (ImageView) convertView.findViewById(R.id.ftxt_iv_upvote);
            holder.ivThumbsDown = (ImageView) convertView.findViewById(R.id.ftxt_iv_downvote);
            holder.ivMore = (ImageView) convertView.findViewById(R.id.ftxt_iv_more);
            holder.tvTags = (TextView) convertView.findViewById(R.id.ftxt_tv_tags);
            holder.ivView = (ImageView) convertView.findViewById(R.id.feed_image);
            holder.vdView = (VideoView) convertView.findViewById(R.id.feed_video);
            holder.upvotes = (TextView) convertView.findViewById(R.id.upvote_count);
            holder.downvotes = (TextView) convertView.findViewById(R.id.downvote_count);

            resultView = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            resultView = convertView;
        }

        holder.txtTitle.setText(viewData.title);
        holder.txtBody.setText(viewData.body);
        holder.txtAuthor.setText(viewData.author);
        switch (viewData.likedStatus) {
            case 0:
                holder.ivThumbsUp.setAlpha(0.6f);
                holder.ivThumbsDown.setAlpha(0.87f);
                break;
            case 1:
                holder.ivThumbsUp.setAlpha(0.87f);
                holder.ivThumbsDown.setAlpha(0.6f);
                break;
            case 2:
                holder.ivThumbsUp.setAlpha(0.6f);
                holder.ivThumbsDown.setAlpha(0.6f);
                break;
        }

        switch (viewData.itemType) {
            case 0:
                holder.ivView.setVisibility(View.GONE);
                holder.vdView.setVisibility(View.GONE);
                break;
            case 1:
                holder.vdView.setVisibility(View.GONE);
                holder.ivView.setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(viewData.url).into(holder.ivView);
                break;
            case 2:
                holder.ivView.setVisibility(View.GONE);
                holder.vdView.setVisibility(View.VISIBLE);
                break;
        }

        String tag = "";
        String[] tags = viewData.tags.replace("[", "").replace("]", "").replace(" ", "").split(",");

        for (String s : tags) {
            tag += "#" + s + " ";
        }

        holder.tvTags.setText(tag);

        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.ivThumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int upvote = 0, downvote = 0;
                final Boolean[] update = {true, true};
                if (v != null) {
                    if (v.getAlpha() == 0.6f) {
                        v.setAlpha(0.87f);
                        //  mRef.child("Posts").child(viewData.mPid).child("upvotes").setValue(viewData.upvotes + 1);
                        upvote = 1;
                        mRef.child(Config.MemberProfileRef).child(viewData.uid).child("Posts").child(viewData.mPid).child("upvoted").setValue("1");
                        if (holder.ivThumbsDown.getAlpha() == 0.87f) {
                            // mRef.child("Posts").child(viewData.mPid).child("downvotes").setValue(viewData.downvotes - 1);
                            downvote = -1;
                        }
                    } else {
                        v.setAlpha(0.6f);
                        //  mRef.child("Posts").child(viewData.mPid).child("upvotes").setValue(viewData.upvotes - 1);
                        upvote = -1;
                        mRef.child(Config.MemberProfileRef).child(viewData.uid).child("Posts").child(viewData.mPid).child("upvoted").setValue("2");
                    }
                    final int finalUpvote = upvote;
                    mRef.child("Posts").child(viewData.mPid).child("upvotes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                if (update[0]) {
                                    long count = (Long) dataSnapshot.getValue();
                                    count += finalUpvote;
                                    mRef.child("Posts").child(viewData.mPid).child("upvotes").setValue(count);
                                    holder.upvotes.setText(Long.toString(count));
                                    update[0] = false;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Exception Occured");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    final int finalDownvote = downvote;
                    mRef.child("Posts").child(viewData.mPid).child("downvotes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                if (update[1]) {
                                    long count = (Long) dataSnapshot.getValue();
                                    count += finalDownvote;
                                    mRef.child("Posts").child(viewData.mPid).child("downvotes").setValue(count);
                                    holder.downvotes.setText(Long.toString(count));
                                    update[1] = false;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Exception Occured");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    holder.ivThumbsDown.setAlpha(0.6f);
                }
            }
        });
        holder.ivThumbsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int upvote = 0, downvote = 0;
                final Boolean[] update = {true, true};
                if (v != null) {
                    if (v.getAlpha() == 0.6f) {
                        v.setAlpha(0.87f);
                      //  mRef.child("Posts").child(viewData.mPid).child("downvotes").setValue(viewData.downvotes + 1);
                        downvote = 1;
                        mRef.child(Config.MemberProfileRef).child(viewData.uid).child("Posts").child(viewData.mPid).child("upvoted").setValue("0");
                        if (holder.ivThumbsUp.getAlpha() == 0.87f) {
                          //  mRef.child("Posts").child(viewData.mPid).child("upvotes").setValue(viewData.upvotes - 1);
                            upvote = -1;
                        }
                    } else {
                        v.setAlpha(0.6f);
                      //  mRef.child("Posts").child(viewData.mPid).child("downvotes").setValue(viewData.downvotes - 1);
                        downvote = -1;
                        mRef.child(Config.MemberProfileRef).child(viewData.uid).child("Posts").child(viewData.mPid).child("upvoted").setValue("2");
                    }
                    final int finalUpvote = upvote;
                    mRef.child("Posts").child(viewData.mPid).child("upvotes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                if (update[0]) {
                                    long count = (Long) dataSnapshot.getValue();
                                    count += finalUpvote;
                                    mRef.child("Posts").child(viewData.mPid).child("upvotes").setValue(count);
                                    holder.upvotes.setText(Long.toString(count));
                                    update[0] = false;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Exception Occured");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    final int finalDownvote = downvote;
                    mRef.child("Posts").child(viewData.mPid).child("downvotes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                if (update[1]) {
                                    long count = (Long) dataSnapshot.getValue();
                                    count += finalDownvote;
                                    mRef.child("Posts").child(viewData.mPid).child("downvotes").setValue(count);
                                    holder.downvotes.setText(Long.toString(count));
                                    update[1] = false;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Exception Occured");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    holder.ivThumbsUp.setAlpha(0.6f);
                }
            }
        });

        holder.txtBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(holder.txtTitle.getText().toString())
                        .setMessage(holder.txtBody.getText().toString())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        holder.txtAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(holder.txtTitle.getText().toString())
                        .setMessage(holder.txtBody.getText().toString())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        holder.txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(holder.txtTitle.getText().toString())
                        .setMessage(holder.txtBody.getText().toString())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        holder.ivView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(holder.txtTitle.getText().toString())
                        .setMessage(holder.txtBody.getText().toString())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        holder.vdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(holder.txtTitle.getText().toString())
                        .setMessage(holder.txtBody.getText().toString())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        TextView txtTitle;
        TextView txtBody;
        TextView txtAuthor;
        ImageView ivThumbsUp;
        ImageView ivThumbsDown;
        ImageView ivMore;
        TextView tvTags;
        ImageView ivView;
        VideoView vdView;
        TextView upvotes;
        TextView downvotes;
    }
}
