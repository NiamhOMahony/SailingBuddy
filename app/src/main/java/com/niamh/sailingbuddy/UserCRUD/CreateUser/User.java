package com.niamh.sailingbuddy.UserCRUD.CreateUser;

import android.graphics.Bitmap;

public class User {

    //private variables
    int _id;
    String name;
    String email_id;
    String mobile_number;
    String password;
    String type;
    Bitmap image;




    // Empty constructor
    public User(){

    }
    // constructor
    public User(int id, String name, String email_id,String mobile_number, String password, String type, Bitmap image){
        this._id = id;
        this.name = name;
        this.email_id=email_id;
        this.mobile_number=mobile_number;
        this.password = password;
        this.type =type;
        this.image = image;
    }


    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // setting  first name
    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name =name;
    }

    public String getEmailId() {
        return email_id;
    }
    public void setEmailId(String email_id){
        this.email_id =email_id;
    }
    public String getMobNo() {
        return mobile_number;
    }

    public void setMobNo(String mobile_number){
        this.mobile_number=mobile_number;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}

