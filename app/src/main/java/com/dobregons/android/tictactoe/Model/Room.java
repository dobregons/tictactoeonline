package com.dobregons.android.tictactoe.Model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by f on 1/11/2017.
 */

@IgnoreExtraProperties
public class Room {

    public String idRoom;
    public String descriptionRoom;
    public Boolean available;
    public List<User> listUser;

    public Room() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Room(String idRoom, String descriptionRoom, Boolean available, List<User> listUser) {
        this.idRoom = idRoom;
        this.descriptionRoom = descriptionRoom;
        this.available = available;
        this.listUser = listUser;
    }

}

