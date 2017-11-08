package com.dobregons.android.tictactoe.Model;

/**
 * Created by f on 1/11/2017.
 */

public class User {
    public String idUser;
    public String userName;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String idUser, String userName) {

        this.idUser = idUser;
        this.userName = userName;
    }
}
