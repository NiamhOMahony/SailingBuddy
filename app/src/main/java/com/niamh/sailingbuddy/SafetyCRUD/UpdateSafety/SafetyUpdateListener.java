package com.niamh.sailingbuddy.SafetyCRUD.UpdateSafety;

//Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io


import com.niamh.sailingbuddy.SafetyCRUD.CreateSafety.Safety;

//Listening for when update is actually clicked so it can implement code
public interface SafetyUpdateListener {

    void onSafetyInfoUpdate(Safety safety);
}
