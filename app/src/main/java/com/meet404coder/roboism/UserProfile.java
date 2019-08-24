package com.meet404coder.roboism;

public class UserProfile {

    public String uid;
    public String name;
    public String admno;
    public String email;
    public String mobile;
    public String hostel;
    public String room;
    public String firebaseID;
    public String department;
    public String designation;
    public int charges;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public UserProfile() {

    }

    public UserProfile(String Uid, String Name, String Admno, String Email, String Department, String Mobile, String Hostel,
                       String Room,String Designation ,String FirebaseID,int Charges) {
        this.email = Email;
        this.department = Department;
        this.mobile = Mobile;
        this.admno = Admno;
        this.hostel = Hostel;
        this.name = Name;
        this.room = Room;
        this.uid = Uid;
        this.designation = Designation;
        this.firebaseID = FirebaseID;
        this.charges = Charges;
    }
}
