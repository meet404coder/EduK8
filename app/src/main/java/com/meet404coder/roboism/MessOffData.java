package com.meet404coder.roboism;

public class MessOffData
//        implements Parcelable
{

  public String uid;
  public String messOfData;
  public String date;
  public String extras;
  public String creditPending;
  public String status;



    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public MessOffData() {

    }

    public MessOffData(String Uid, String Date,String MessOfData, String Extras, String CreditPending, String Status) {
        this.uid = Uid;
        this.date = Date;
        this.messOfData = MessOfData;
        this.extras = Extras;
        this.creditPending = CreditPending;
        this.status = Status;
    }

    /*
        public MessOffData(Parcel in) {
        this.uid = in.readString();
        this.date = in.readString();
        this.messOfData = in.readString();
        this.extras = in.readString();
        this.creditPending = in.readString();
        this.status = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(date);
        dest.writeString(messOfData);
        dest.writeString(extras);
        dest.writeString(creditPending);
        dest.writeString(status);

    }

    public static final Parcelable.Creator<MessOffData> CREATOR = new Parcelable.Creator<MessOffData>()
    {
        public MessOffData createFromParcel(Parcel in)
        {
            return new MessOffData(in);
        }
        public MessOffData[] newArray(int size)
        {
            return new MessOffData[size];
        }
    };

    */
}
