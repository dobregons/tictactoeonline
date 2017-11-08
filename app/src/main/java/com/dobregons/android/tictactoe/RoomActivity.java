package com.dobregons.android.tictactoe;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dobregons.android.tictactoe.Domain.FirebaseHelper;
import com.dobregons.android.tictactoe.Domain.RoomListAdapter;
import com.dobregons.android.tictactoe.Model.Room;

/**
 * Created by f on 6/11/2017.
 */

public class RoomActivity extends AppCompatActivity
{
    private FirebaseHelper helper;
    private RoomListAdapter roomListAdapter;
    public RoomActivity()
    {
        helper = FirebaseHelper.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_list);

        String userName = this.getIntent().getStringExtra("USER_NAME");
        TextView tvName = (TextView)findViewById(R.id.txtUserName);
        tvName.setText("Bienvenido " + userName);

        //final ListView listView = (ListView)listView.findViewById(R.id.room_list);
        final ListView listView = (ListView) findViewById(R.id.room_list);
        roomListAdapter = new RoomListAdapter(helper.getRoom(), RoomActivity.this, R.layout.room_list);
        listView.setAdapter(roomListAdapter);
        roomListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(roomListAdapter.getCount() - 1);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Room roomSelected = (Room) roomListAdapter.getItem(arg2);
                // TODO Auto-generated method stub
                Log.d("############","Items " +  arg2 + " " + arg3 );
            }

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        roomListAdapter.cleanup();
    }
}
