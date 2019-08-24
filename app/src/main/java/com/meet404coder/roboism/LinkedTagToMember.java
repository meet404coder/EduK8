package com.meet404coder.roboism;

public class LinkedTagToMember {

    public String uid;
    public String mobile;
    public String name;
    public String tagID;
    public String admno;
    public int accessLevel;


    public LinkedTagToMember() {

    }

    public LinkedTagToMember(String Uid, String Name,String Admno,String Mobile, String TagID,int AccessLevel) {
        this.uid = Uid;
        this.mobile = Mobile;
        this.admno = Admno;
        this.name = Name;
        this.tagID = TagID;
        this.accessLevel = AccessLevel;
    }

}
