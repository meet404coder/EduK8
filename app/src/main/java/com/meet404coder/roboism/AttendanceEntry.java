package com.meet404coder.roboism;

public class AttendanceEntry {

    public String uid;
    public String date;
    public String name;
    public String admno;


    public AttendanceEntry() {

    }

    public AttendanceEntry(String Date,String Uid, String Name, String Admno) {
        this.uid = Uid;
        this.admno = Admno;
        this.name = Name;
        this.date = Date;
    }

}
