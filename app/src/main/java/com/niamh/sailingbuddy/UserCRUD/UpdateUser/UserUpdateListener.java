package com.niamh.sailingbuddy.UserCRUD.UpdateUser;

import com.niamh.sailingbuddy.UserCRUD.CreateUser.User;

public interface UserUpdateListener {
    void onUserInfoUpdate(User user, int position);
}
