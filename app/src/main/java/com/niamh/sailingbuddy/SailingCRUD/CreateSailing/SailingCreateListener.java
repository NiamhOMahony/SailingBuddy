package com.niamh.sailingbuddy.SailingCRUD.CreateSailing;

//Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io

import com.niamh.sailingbuddy.SailingCRUD.CreateSailing.Sailing;

//Listening for when create is actually clicked so it can implement code
public interface SailingCreateListener {
    void onSailingCreated(Sailing sailing);
}