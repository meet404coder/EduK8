package com.vision.eduk8;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FeedAdapter extends ArrayAdapter<FeedItemData> {

    ArrayList<FeedItemData> dataList;
    Context mContext;

    public FeedAdapter(@NonNull Context context, ArrayList<FeedItemData> data) {
        super(context, R.layout.feed_item, data);
        mContext = context;
        dataList = data;
    }

    public static class ViewHolder {
        TextView txtTitle;
        TextView txtBody;
        TextView txtAuthor;
        ImageView ivThumbsUp;
        ImageView ivThumbsDown;
        ImageView ivMore;
        TextView tvTags;
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
        FeedItemData viewData = dataList.get(position);
        final ViewHolder holder;

        final View resultView;

        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.feed_item, parent, false);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.ftxt_tv_title);
            holder.txtBody = (TextView) convertView.findViewById(R.id.ftxt_tv_body);
            holder.txtAuthor =  (TextView) convertView.findViewById(R.id.ftxt_tv_author);
            holder.ivThumbsUp = (ImageView) convertView.findViewById(R.id.ftxt_iv_upvote);
            holder.ivThumbsDown = (ImageView) convertView.findViewById(R.id.ftxt_iv_downvote);
            holder.ivMore = (ImageView) convertView.findViewById(R.id.ftxt_iv_more);
            holder.tvTags = (TextView) convertView.findViewById(R.id.ftxt_tv_tags);

            resultView = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            resultView = convertView;
        }

        holder.txtTitle.setText(viewData.mTitle);
        holder.txtBody.setText(viewData.mBody);
        holder.txtAuthor.setText(viewData.mAuthor);
        switch(viewData.mLikedStatus) {
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

        String tag = "";
        if (viewData.mTags != null) {
            String[] tags = new String[viewData.mTags.length];
            for (String s : tags) {
                tag += "#"+s+" ";
                Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
            }
            holder.tvTags.setText(tag);
          /*  ArrayAdapter<String> tagGridAdapter = new ArrayAdapter<>(mContext, R.layout.tag_item, tagList);
            holder.gvTagsGrid.setAdapter(tagGridAdapter);
            tagGridAdapter.notifyDataSetChanged(); */
        }


        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.ivThumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    v.setAlpha(0.87f);
                    holder.ivThumbsDown.setAlpha(0.6f);
                }
            }
        });
        holder.ivThumbsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    v.setAlpha(0.87f);
                    holder.ivThumbsUp.setAlpha(0.6f);
                }
            }
        });

        return convertView;
    }
}
