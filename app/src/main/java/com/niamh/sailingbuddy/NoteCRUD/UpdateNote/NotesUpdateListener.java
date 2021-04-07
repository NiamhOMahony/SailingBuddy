package com.niamh.sailingbuddy.NoteCRUD.UpdateNote;

import com.niamh.sailingbuddy.NoteCRUD.CreateNote.Notes;

//Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io


//Listening for when update is actually clicked so it can implement code
public interface NotesUpdateListener {

    void onNoteInfoUpdate(Notes notes, int position);
}
