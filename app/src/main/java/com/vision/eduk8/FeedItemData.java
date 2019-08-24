package com.vision.eduk8;

public class FeedItemData {
    String mTitle, mBody, mAuthor;
    String[] mTags;
    Integer mLikedStatus;

    FeedItemData(String title, String body, String author, String[] tags, Integer likedStatus) {
        mTitle = title;
        mBody = body;
        mAuthor = author;
        mTags = new String[tags.length];
        mLikedStatus = likedStatus;
    }
}
