package com.dobregons.android.tictactoe;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.dobregons.android.tictactoe.Domain.FirebaseHelper;
import com.dobregons.android.tictactoe.Model.Room;
import com.dobregons.android.tictactoe.Model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.value;

/**
 * Created by f on 29/10/2017.
 */

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>
{
    private static final String TAG = "";
    private EditText roomNameEdit;
    private EditText userNameEdit;
    private FirebaseHelper helper;
    private DatabaseReference roomReference;


    public MainActivity()
    {
        helper = FirebaseHelper.getInstance();
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.home_screen);
        //display the home_screen during 3 seconds,
        new CountDownTimer(3000,1000){
            @Override
            public void onTick(long millisUntilFinished){}

            @Override
            public void onFinish(){
                //set the new Content of your activity
                MainActivity.this.setContentView(R.layout.activity_layout);
            }
        }.start();

        setContentView(R.layout.activity_layout);
    }


    //Called when the user clicks the Forget Password Text View
    public void singleGame(View view)
    {
        Intent intent = new Intent(this, AndroidTicTacToeActivity.class);
        startActivity(intent);
    }

    //Called when the user clicks the Forget Password Text View
    public void createRoom(View view)
    {
        //Handle the PopUp Window
        final Button btnOpenPopup = (Button)findViewById(R.id.quick_button);
        LayoutInflater layoutInflater
                = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.popup_room, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);

        Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
        Button btnCreateRoom = (Button)popupView.findViewById(R.id.create_room);
        btnDismiss.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
            }});

        btnCreateRoom.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(final View v) {

                //GetValue editTExts
                roomNameEdit = (EditText)popupView.findViewById(R.id.room_name);
                userNameEdit = (EditText)popupView.findViewById(R.id.user_name);
                //CreateRoomFirebase
                User user = new User();
                user.userName = userNameEdit.getText().toString();
                List<User> listUser = new ArrayList<>();
                listUser.add(user);
                Room room = new Room();
                room.descriptionRoom = roomNameEdit.getText().toString();
                room.listUser = listUser;
                room.available = true;
                //Call FirebaseHelper
                helper.insertRooms(room);
                roomReference = helper.getRoomById(room.idRoom);

                //setProgressBarIndeterminateVisibility(true);
                roomReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        Room room = dataSnapshot.getValue(Room.class);
                        if(room.listUser.size() == 2)
                        {
                            //getLoaderManager().destroyLoader(123);
                            //setProgressBarIndeterminateVisibility(false);
                            Intent intent = new Intent(v.getContext(), RoomActivity.class);
                            intent.putExtra("USER_NAME", userNameEdit.getText().toString());
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "La sala que intenta crear ya cuenta con dos usuarios", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                            //setProgressBarIndeterminateVisibility(true);
                            Intent intent = new Intent(v.getContext(), RoomActivity.class);
                            intent.putExtra("USER_NAME", userNameEdit.getText().toString());
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "Sala creada correctamente", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                popupWindow.dismiss();
            }});

        popupWindow.showAtLocation(btnOpenPopup,1,0,0);
        popupWindow.setFocusable(true);
        popupWindow.update();
    }

    //Called when the user clicks the Forget Password Text View
    public void joinRoom(View view)
    {
        //Handle the PopUp Window
        final Button btnOpenPopup = (Button)findViewById(R.id.quick_button);
        LayoutInflater layoutInflater
                = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.popup_user, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);

        Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
        Button btnJoinRoom = (Button)popupView.findViewById(R.id.join_room);
        btnDismiss.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
            }});

        btnJoinRoom.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                //GetValue editTExts
                userNameEdit = (EditText)popupView.findViewById(R.id.user_name);
                Intent intent = new Intent(v.getContext(), RoomActivity.class);
                intent.putExtra("USER_NAME", userNameEdit.getText().toString());
                startActivity(intent);
                popupWindow.dismiss();
            }});

        popupWindow.showAtLocation(btnOpenPopup,1,0,0);
        popupWindow.setFocusable(true);
        popupWindow.update();



    }

}

