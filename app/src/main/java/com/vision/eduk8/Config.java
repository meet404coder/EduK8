package com.vision.eduk8;

import android.os.Environment;

public class Config {

    /*
    * Offline Private File Names:
    * UserData.txt
    */

    //Firebase Reference to Existing Members Mobile
    public static String MembersMobRef = "RoboISM Members Mobile";

    //Firebase Reference to Existing Members Profile
    public static String MemberProfileRef = "RoboISM Members Profile";

    //Firebase Reference to Polling Drafter
    public static String PollingDrafterRef = "Polling Drafter";

    //Firebase Server Time Reference
    public static String serverTimeRef = "Server Time";

    //Firebase Meeting Reference
    public static String MeetsRef = "Meets";

    //Firebase Reference to Existing Members TAG
    public static String MemberProfileTAGDataRef = "RoboISM Members TAG Data";

    //Firebase Reference to Attendance
    public static String AttendanceRef = "Attendance Data";

    //Allow the BT device Config on access level 4 or higher
    public static int ALLOW_BT_DEVICE_CONFIG_ABOVE = 4;

    //Call Meet status
    public static String CallMeetStatusPending = "PENDING";


    //Firebase User Mess Of Data Management
    public static String MessOffDatabaseRef = "Mess Off Database";

    //Firebase Total Mess Of Data Management
    public static String TotalMessOffbyUserDatabaseRef = "TOTAL Mess OFF Count";

    //Firebase Meal Credits Worth Data Management
    public static String MessCreditRef = "Meal Off Credits";

    //Firebase User Database Management
    public static String UserDatabaseRef = "User Database";


    //Firebase Feedback Reference
    public static String FeedbackRef = "Feedback Database";

    //Firebase References to Mess Menu
    public static String MenuRef = "Mess Menu";


    //Firebase References to AdminUserHandshake
    public static String AdminUserHandshakeRef = "HotWordAndIsAdmin";

    //Firebase References to AdminQRRead
    public static String AdminQRread = "AdminQRreadData";

    //DateCreditStatus Strings
    public static String DatePassed = "SUCCESSFUL";
    public static String DateNotYetPassed = "PENDING";

    //FeedbackStatus Strings
    public static String FeedbackStatusComplete = "CLOSED";
    public static String FeedbackStatusPending = "PENDING";

    //Splitter for QR
    public static String Splitter = "::";


    //Download and Files Path
    public static String downloadFileLocation = Environment.getExternalStorageDirectory().getPath() + "/Files(FYK)/DownloadedFiles(FYK)/";
    public static String seniorDataFileLocation = Environment.getExternalStorageDirectory().getPath() + "/Files(FYK)/SeniorData_Images/";


    //Managing the Admin Options button
    public static boolean IsAdmin = false;

    //Firebase References to store Employee Location and Firm
    public static String FirmsRef = "Firms";
    public static String EmployeesRef = "Employees";
    public static String EmployeesDetailsRef = "Details";
    public static String EmployeeLocationRef = "Employees Last Location";
    public static String EmployeeAttendenceRef = "Attendence";

    public static int TRACKER_ON = 0;



    public static final String EMAIL = "sendiitdhanbad@gmail.com";
    public static final String PASSWORD = "Prakhar123";
    public static final String TOEMAIL = "contact.fyk.iitd@gmail.com"; // change this to iitismhubextreme@gmail.com
    public static int chk_emailsent=0;
    public static int chk_introComplete=0;
    public static int chk_LoginDataSaveSuccessfull=0;
    public static int chk_tab=0;
    public static String sec="Not Specified";
    public static int chk_not_opened=0;
    public static  int chk_not=0;


}


