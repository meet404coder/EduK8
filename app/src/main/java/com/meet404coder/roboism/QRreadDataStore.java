package com.meet404coder.roboism;

public class QRreadDataStore {

    public String uid;
    public String messOfDataEaten;
    public String date;
    public String extrasTaken;
    public String status;


    public QRreadDataStore() {

    }

    public QRreadDataStore(String Uid, String Date,String MessOfDataEaten, String ExtrasTaken, String Status) {

        this.uid = Uid;
        this.date = Date;
        this.messOfDataEaten = MessOfDataEaten;
        this.extrasTaken = ExtrasTaken;
        this.status = Status;
    }

}
