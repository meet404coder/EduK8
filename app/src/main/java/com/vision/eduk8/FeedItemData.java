package com.vision.eduk8;

import java.util.Arrays;

public class FeedItemData {

    public String title, body, author, uid, url, mPid;
    public String tags;
    public int upvotes, downvotes, itemType, likedStatus;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public FeedItemData() {

    }

    public FeedItemData(String title, String body, String author, String[] tags, String url, String uid, int type, int up, int down, int status, String pid) {
        this.title = title;
        this.uid = uid;
        this.body = body;
        this.author = author;
        this.url = url;
        this.tags = Arrays.toString(tags);
        this.itemType = type;
        this.upvotes = up;
        this.downvotes = down;
        this.likedStatus = status;
        this.mPid = pid;
    }
}
