package com.niamh.sailingbuddy.SafetyCRUD.CreateSafety;

//Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io

//Listening for when create is actually clicked so it can implement code
public interface SafetyCreateListener {
    void onSafetyCreated(Safety safety);
}