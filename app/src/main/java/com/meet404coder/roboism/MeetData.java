package com.meet404coder.roboism;

public class MeetData {

    public String uid;
    public String meet_date;
    public String meet_time_from;
    public String meet_time_to;
    public String meet_venue;
    public String agenda;
    public String create_date;
    public String status;
    public String remarks;
    public int    acc_lvl_creator;
    public int    min_acc_lvl;
    public int    max_acc_lvl;
    public String meetID;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public MeetData() {

    }

    public MeetData(String Uid, String Meet_Date, String MeetTimeFrom, String MeetTimeTo, String Venue, String Agenda,
                    String CreateDate, String Status, String Remarks ,
                    int AccessLvlCreator, int MinAccessLvl,int MaxAccessLvl,String MeetID) {

        this.uid = Uid;
        this.meet_date = Meet_Date;
        this.meet_time_from = MeetTimeFrom;
        this.meet_time_to = MeetTimeTo;
        this.meet_venue = Venue;
        this.agenda = Agenda;
        this.create_date = CreateDate;
        this.status = Status;
        this.remarks = Remarks;
        this.acc_lvl_creator = AccessLvlCreator;
        this.min_acc_lvl = MinAccessLvl;
        this.max_acc_lvl = MaxAccessLvl;
        this.meetID = MeetID;
    }
}
