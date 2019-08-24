package com.vision.eduk8;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file meet_date
    private static final String PREF_NAME = "gzro-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    public static final String QRsavedPath = "";



    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public String getsetQRsavedPath(){
        return pref.getString(QRsavedPath,null);
    }

    public void setQRsavedPath(String savedPath) {
        editor.putString(QRsavedPath, savedPath);
        editor.commit();
    }

    //TODO:: SYNC SHARED PREFERENCES TO FIREBASE ELSE THERE WILL BE ERRORS

}
