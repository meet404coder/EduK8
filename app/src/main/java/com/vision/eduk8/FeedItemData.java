package com.vision.eduk8;

import java.util.Arrays;

public class FeedItemData {

    public String title, body, author, uid;
    public String tags;
    public int likedStatus;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public FeedItemData() {

    }

    public FeedItemData(String title, String body, String author, String[] tags, int likedStatus, String uid) {
        this.title = title;
        this.uid = uid;
        this.body = body;
        this.author = author;
        this.likedStatus = likedStatus;
        this.tags = Arrays.toString(tags);
    }
}
