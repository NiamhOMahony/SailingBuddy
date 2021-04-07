package com.niamh.sailingbuddy.SailingCRUD.CreateSailing;

import android.graphics.Bitmap;

public class Sailing {

    //declaring variables related to my notes attribute
    private long id;
    private String type;
    private String description;
    private String available;
    private String availuser;
    private String fault;
    private String faultuser;
    private String faultdes;
    private Bitmap image;


    //giving them their values
    public Sailing(int id, String type, String description, String available, String availuser, String fault,
                   String faultuser, String faultdes, Bitmap image){

        this.id = id;
        this.type = type;
        this.description = description;
        this.available = available;
        this.availuser = availuser;
        this.fault = fault;
        this.faultuser = faultuser;
        this.faultdes = faultdes;
        this.image = image;


    }

    //Getters & Setters

    //ID
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    //Type
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    //Description
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    //Availability
    public String getAvailable() { return available; }
    public void setAvailable(String available) { this.available = available; }
    //Availability User
    public String getAvailuser() { return availuser; }
    public void setAvailuser(String availuser) { this.availuser = availuser; }
    //Fault
    public String getFault() { return fault; }
    public void setFault(String fault) { this.fault = fault; }
    //Fault User
    public String getFaultuser() { return faultuser; }
    public void setFaultuser(String faultuser) { this.faultuser = faultuser; }
    //Fault Description
    public String getFaultdes() { return faultdes; }
    public void setFaultdes(String faultdes) { this.faultdes = faultdes; }
    //Image
    public Bitmap getImage() { return image; }
    public void setImage(Bitmap image) { this.image = image; }}


