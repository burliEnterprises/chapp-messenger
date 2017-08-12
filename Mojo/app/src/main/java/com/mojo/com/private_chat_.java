package com.mojo.com;


        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.AbsListView;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.TextView;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.text.DateFormat;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.HashMap;
        import java.util.HashSet;
        import java.util.Iterator;
        import java.util.Map;
        import java.util.Set;

public class private_chat_ extends AppCompatActivity {
/*
    //Fields for out Views
    Button btn_SendMessage;
    TextView tv_receiveMessage;
    EditText sendMessageText;

    //Database reference
    DatabaseReference rootRoomName, rootRoomName_mine, rootRoomName_partner;

    //String fields
    String roomName;
    String userName, telefonnummer;
    private SharedPreferences prefs;
    private String chatUserName, chatMessage, chatTime;
    DatabaseReference databaseReference;
    private FirebaseUser fbUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_room);

        //views einlesen
        btn_SendMessage = (Button) findViewById(R.id.btn_SendMessage);
        tv_receiveMessage = (TextView) findViewById(R.id.tv_receiveMessage);
        sendMessageText = (EditText) findViewById(R.id.sendMessageText);

        //übergabe aus mainactivity
        roomName = getIntent().getExtras().get("room").toString();
        userName = getIntent().getExtras().get("user").toString();

        // daten des benutzers aus dem speicher laden
        prefs = this.getSharedPreferences("CHAPP_PREFS", Context.MODE_PRIVATE);
        telefonnummer = prefs.getString("telefonnummer", "");

        //oben in der leiste statt activity name room name
        setTitle(roomName);

        // in das verzeichnis des raums wechseln auf der db!
        // 1. alle kontakte in dem user db werden hgeholt

        rootRoomName_partner = FirebaseDatabase.getInstance().getReference().getRoot().child("private-chats").child(roomName + "_" + telefonnummer);
        rootRoomName_mine = FirebaseDatabase.getInstance().getReference().getRoot().child("private-chats").child(telefonnummer + "_" + roomName);


        //nachricht senden
        btn_SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference childRoot = rootRoomName_partner.push();
                DatabaseReference childRoot_mine = rootRoomName_mine.push();
                Map<String, Object> map = new HashMap<String, Object>();

                DateFormat df = new SimpleDateFormat("h:mm a");
                String date = df.format(Calendar.getInstance().getTime());

                map.put("name", userName);
                map.put("message", sendMessageText.getText().toString());
                map.put("time", date);

                childRoot.updateChildren(map);
            }
        });

        // wird in der db ein eintrag in den chat room ordner eingeügt -> aktualisieren
        rootRoomName_partner.addChildEventListener(new ChildEventListener() {
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

        tv_receiveMessage.append(chatUserName + ":" + chatMessage + ":" + chatTime + "\n\n");

    }

*/

    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;
        private FirebaseUser fbUser;
        private FirebaseAuth firebaseAuth;

        //Database reference
        DatabaseReference rootRoomName_mine, rootRoomName_partner;


        //Fields for out Views
    ImageView btn_SendMessage;
    TextView tv_receiveMessage;
    EditText msg;

    //Database reference
    DatabaseReference rootRoomName;

    //String fields
    String roomName;
    String userName;
    private String username, name, email, telefonnummer;
    private String chatUserName, chatMessage, chatTime;
    private ImageView iv_backToChatView;
    private Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_room);

            firebaseAuth = FirebaseAuth.getInstance();
            fbUser = firebaseAuth.getCurrentUser();

            //views einlesen
        btn_SendMessage = (ImageView) findViewById(R.id.send);
        msg = (EditText) findViewById(R.id.msg);
        iv_backToChatView = (ImageView) findViewById(R.id.iv_backToChatView);
        //übergabe aus mainactivity
        roomName = getIntent().getExtras().get("room").toString();
        userName = getIntent().getExtras().get("user").toString();

        // daten des benutzers aus dem speicher laden
        SharedPreferences prefs = this.getApplicationContext().getSharedPreferences("CHAPP_PREFS", Context.MODE_PRIVATE);
        username = prefs.getString("username", "");
        name = prefs.getString("name", "");
        email = prefs.getString("email","");
        telefonnummer = prefs.getString("telefonnummer", "");

        //oben in der leiste statt activity name room name
        setTitle(roomName);

        // in das verzeichnis des raums wechseln auf der db!
        rootRoomName = FirebaseDatabase.getInstance().getReference().getRoot().child("chatrooms").child(roomName);

            rootRoomName_partner = FirebaseDatabase.getInstance().getReference().getRoot().child("private-chats").child(roomName).child(telefonnummer);
            rootRoomName_mine = FirebaseDatabase.getInstance().getReference().getRoot().child("private-chats").child(telefonnummer).child(roomName);


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

                        DatabaseReference childRoot = rootRoomName_partner.push();
                        DatabaseReference childRoot_mine = rootRoomName_mine.push();
                        Map<String, Object> map = new HashMap<String, Object>();

                        DateFormat df = new SimpleDateFormat("h:mm a");
                        String date = df.format(Calendar.getInstance().getTime());

                        map.put("name", userName);
                        map.put("message", msg.getText().toString());
                        map.put("time", date);

                        childRoot.updateChildren(map);
                        childRoot_mine.updateChildren(map);
                }
        });

        i = new Intent(private_chat_.this, SecondFragment.class);
        iv_backToChatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(i);
                finish();

            }
        });

        // wird in der db ein eintrag in den chat room ordner eingeügt -> aktualisieren
        rootRoomName_mine.addChildEventListener(new ChildEventListener() {
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
            side = !side;
            return true;
        }




}