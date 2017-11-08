package com.dobregons.android.tictactoe.Domain;

import android.util.Log;

import com.dobregons.android.tictactoe.Model.Room;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by f on 1/11/2017.
 */

public class FirebaseHelper {
    private DatabaseReference dataReference;
    private static final String ROOMS_PATH = "Rooms";
    private boolean response;

    private static class SingletonHolder {
        private static final FirebaseHelper INSTANCE = new FirebaseHelper();
    }

    public static FirebaseHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public FirebaseHelper() {
        this.dataReference = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDataReference() {
        return dataReference;
    }

    public DatabaseReference getRoomById(String idRoom) {
        DatabaseReference roomReference = null;
        if (idRoom != null) {
            roomReference = dataReference.getRoot().child(ROOMS_PATH).child(idRoom);
        }

        return roomReference;
    }

    public void insertRooms(Room room) {
        if (room != null) {
            room.idRoom = java.util.UUID.randomUUID().toString();
            //boolean abc = verifyRoomName(room.descriptionRoom);
            dataReference.child(ROOMS_PATH).child(room.idRoom).setValue(room);
        }
    }

    public DatabaseReference getRoom() {
        return dataReference.child(ROOMS_PATH);
    }

    public boolean verifyRoomName(final String roomName){
        if(roomName != null){

            DatabaseReference data = dataReference.child(ROOMS_PATH);
            data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Room> listRoom = new ArrayList<Room>();
                Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Room room = postSnapshot.getValue(Room.class);
                    //Log.e("Get Data", post.());
                    listRoom.add(room);
                }
                response = false;

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(ROOMS_PATH, "Failed to read value.", error.toException());
            }


        });

            return response;
        }
        return false;

    }

}
