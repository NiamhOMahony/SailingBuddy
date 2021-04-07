package com.niamh.sailingbuddy.NoteCRUD.CreateNote;

/*
 * Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io
 */

import android.graphics.Bitmap;

public class Notes {
    //declaring variables related to my notes attribute
    private long id;
    private String title;
    private String subtitle;
    private String note;
    private String dateTime;

    //giving them their values
    public Notes(int id, String title, String subtitle, String note, String dateTime) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.note = note;
        this.dateTime =dateTime;



    }

    //Getters & Setters

    //ID
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    //Title
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    //subtitle
    public String getSubtitle() {
        return subtitle;
    }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    //note
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    //Date
    public String getDateTime(){return dateTime;}
    public void setDateTime(String dateTime){this.dateTime = dateTime;}

}