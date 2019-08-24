package com.meet404coder.roboism;

public class FeedbackData {


    public String uid;
    public String date;
    public String feedback;
    public String topic;
    public String status;
    public String stars;



    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public FeedbackData() {

    }

    public FeedbackData(String Uid, String Date, String Feedback, String Topic, String Status,String Stars) {
        this.uid = Uid;
        this.date = Date;
        this.feedback = Feedback;
        this.topic = Topic;
        this.status = Status;
        this.stars = Stars;
    }
}
