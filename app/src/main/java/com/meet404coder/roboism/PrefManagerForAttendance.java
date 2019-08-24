package com.meet404coder.roboism;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefManagerForAttendance {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file meet_date
    public static final String PREF_NAME = "roboism-attendance";

    public PrefManagerForAttendance(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveListToPref(String Date,String TAG_List){
        editor.putString(Date,TAG_List);
        editor.commit();
    }

    public String getTagList(String Date){
        return pref.getString(Date,null);
    }


    //TODO:: SYNC SHARED PREFERENCES TO FIREBASE ELSE THERE WILL BE ERRORS

}
