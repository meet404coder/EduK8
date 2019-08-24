package com.meet404coder.roboism;

public class AdminUserColabData {

  public String uid;
  public boolean isadmin;
  public String hotword;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public AdminUserColabData() {

    }

    public AdminUserColabData(String Uid, boolean IsAdmin, String Hotword) {
        this.isadmin = IsAdmin;
        this.uid = Uid;
        this.hotword=Hotword;
    }
}
