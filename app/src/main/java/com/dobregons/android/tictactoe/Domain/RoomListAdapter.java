package com.dobregons.android.tictactoe.Domain;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.dobregons.android.tictactoe.Model.Room;
import com.dobregons.android.tictactoe.R;
import com.google.firebase.database.Query;

/**
 * Created by f on 6/11/2017.
 */

public class RoomListAdapter extends FirebaseListAdapter<Room> {
    public RoomListAdapter(Query ref, Activity activity, int layout) {
        super(ref, Room.class, layout, activity);
    }
    private TextView txtSala;
    private TextView txtCountUser;
    private TextView txtUser;


    @Override
    protected void populateView(View view, Room room) {
        // Map a Chat object to an entry in our listview

        txtSala = (TextView) view.findViewById(R.id.txtSala);
        txtSala.setText("Sala: "+ room.descriptionRoom);

//        txtSala = (TextView) view.findViewById(R.id.txtCountUser);
//        txtSala.setText("Usuarios : "+ room.listUser.size());

        txtSala = (TextView) view.findViewById(R.id.txtCountUser);
        txtSala.setText("Creador: "+ room.listUser.get(0).userName);

    }
}

