package com.niamh.sailingbuddy.NoteCRUD.CreateNote;

//Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io

//Listening for when create is actually clicked so it can implement code
public interface NotesCreateListener {
    void onNoteCreated(Notes notes);
}
