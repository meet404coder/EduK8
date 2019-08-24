package com.meet404coder.roboism;

public class PollDrafter {

    public String uid;
    public String ques;
    public String options;
    public String status;
    public int ques_view_access_level_min;
    public int res_view_access_level_min;
    public int ques_view_access_level_max;
    public int res_view_access_level_max;
    public String quesID;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public PollDrafter() {

    }

    public PollDrafter(String Uid, String Ques, String Options, int Q_acc_lvl_min,int Q_acc_lvl_max, int R_acc_lvl_min,
                       int R_acc_lvl_max, String Status,String QuesID) {
        this.status = Status;
        this.ques_view_access_level_min = Q_acc_lvl_min;
        this.ques_view_access_level_max = Q_acc_lvl_max;
        this.options = Options;
        this.res_view_access_level_min = R_acc_lvl_min;
        this.res_view_access_level_max = R_acc_lvl_max;
        this.ques = Ques;
        this.uid = Uid;
        this.quesID = QuesID;
    }
}
