package com.mojo.com;


import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Chat_room extends AppCompatActivity {


    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;

    //Fields for out Views
    ImageView btn_SendMessage, iv_backToChatView;
    TextView tv_receiveMessage;
    EditText msg;

    //Database reference
    DatabaseReference rootRoomName;

    //String fields
    String roomName;
    String userName;
    private String chatUserName, chatMessage, chatTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_room);

        //views einlesen
        btn_SendMessage = (ImageView) findViewById(R.id.send);
        iv_backToChatView = (ImageView) findViewById(R.id.iv_backToChatView);
        msg = (EditText) findViewById(R.id.msg);

        //übergabe aus mainactivity
        roomName = getIntent().getExtras().get("room").toString();
        userName = getIntent().getExtras().get("user").toString();

        //oben in der leiste statt activity name room name
        setTitle(roomName);

        // in das verzeichnis des raums wechseln auf der db!
        rootRoomName = FirebaseDatabase.getInstance().getReference().getRoot().child("chatrooms").child(roomName);


        listView = (ListView) findViewById(R.id.lv);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.msg);
        // bei enter drücken:
       /* chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        }); */

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //nachricht senden
        btn_SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<String, Object>();

                DateFormat df = new SimpleDateFormat("h:mm a");
                String date = df.format(Calendar.getInstance().getTime());

                map.put("name", userName);
                map.put("message", msg.getText().toString());
                map.put("time", date);

                DatabaseReference childRoot = rootRoomName.push();
                childRoot.updateChildren(map);
            }
        });

        iv_backToChatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(i);
                finish();

            }
        });

        // wird in der db ein eintrag in den chat room ordner eingeügt -> aktualisieren
        rootRoomName.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                update_Message(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                update_Message(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //alle nachrichten in die textview laden mittels append -> listview(?)
    private void update_Message(DataSnapshot dataSnapshot) {

        chatUserName = (String) dataSnapshot.child("name").getValue();
        chatMessage = (String) dataSnapshot.child("message").getValue();
        chatTime = (String) dataSnapshot.child("time").getValue();

       // tv_receiveMessage.append(chatUserName + ":" + chatMessage + ":" + chatTime + "\n\n");
        sendChatMessage(chatUserName, chatMessage, chatTime);
    }

    private boolean sendChatMessage(String name,String message,String time) {
        if (name.equals(userName)) {
            side = true;
        } else {
            side = false;
        };
        chatArrayAdapter.add(new ChatMessage(side, message, name, time, userName));
        chatText.setText("");
        return true;
    }



}