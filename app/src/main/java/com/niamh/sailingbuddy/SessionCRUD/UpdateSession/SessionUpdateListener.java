package com.niamh.sailingbuddy.SessionCRUD.UpdateSession;


import com.niamh.sailingbuddy.SessionCRUD.CreateSession.Session;

public interface SessionUpdateListener {
    void onSessionInfoUpdate(Session session, int position);
}
