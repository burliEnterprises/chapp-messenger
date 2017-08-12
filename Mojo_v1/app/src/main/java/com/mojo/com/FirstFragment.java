package com.mojo.com;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FirstFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPageNo;

    private RecyclerView mRecyclerView;
    private List<Chatrooms> list;
    private ListView lv_allRooms;
    private TextView_Lato iv_search, iv_create;
    private TextView_Lato tv_hi_username;
    private LinearLayout ll_search;
    private ImageView logout;
    private FirebaseAuth firebaseAuth;

    private SharedPreferences prefs;

    //declaratios
    private ArrayList<String> arraylist_raeume;
    private ArrayAdapter<String> adapter_raeume;
    private DatabaseReference databaseReference, dbr_chatrooms, dbr_users;
    private String username, name, email, telefonnummer;

    public FirstFragment() {
        // Required empty public constructor
    }

    public static FirstFragment newInstance(int pageNo) {

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNo = getArguments().getInt(ARG_PAGE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        Stetho.initializeWithDefaults(getActivity());
        lv_allRooms = (ListView) view.findViewById(R.id.roomListView);
        iv_search = (TextView_Lato) view.findViewById(R.id.iv_search);
        iv_create = (TextView_Lato) view.findViewById(R.id.iv_create);
        ll_search = (LinearLayout) view.findViewById(R.id.ll_search);
        logout = (ImageView) view.findViewById(R.id.logout);

        firebaseAuth = FirebaseAuth.getInstance();

        // daten des benutzers aus dem speicher laden
        prefs = this.getActivity().getSharedPreferences("CHAPP_PREFS", Context.MODE_PRIVATE);
        username = prefs.getString("username", "");
        name = prefs.getString("name", "");
        email = prefs.getString("email","");
        telefonnummer = prefs.getString("telefonnummer", "");

        tv_hi_username = (TextView_Lato) view.findViewById(R.id.tv_hi_username);
        tv_hi_username.setText("Hallo " + username);


        arraylist_raeume = new ArrayList<String>();
        adapter_raeume = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arraylist_raeume);
        lv_allRooms.setAdapter(adapter_raeume);

        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
        dbr_chatrooms = databaseReference.child("chatrooms");
        dbr_users = databaseReference.child("users");



        // raumliste wird staendig aktualisiert, key = raumname
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator iterator = dataSnapshot.child("chatrooms").getChildren().iterator();
                //  Iterator = Zeiger, mit dem die Elemente einer Menge durchlaufen werden können (z. B. eine Liste)
                Set<String> set = new HashSet<String>();
                while (iterator.hasNext()) {
                    set.add((String) ((DataSnapshot) iterator.next()).getKey());        // gettet name one by one von der db
                }
                ;
                arraylist_raeume.clear();
                arraylist_raeume.addAll(set);
                adapter_raeume.notifyDataSetChanged();
            }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Hello", "Failed to read value.", error.toException());
                }
        });

        // aufruf eines chatrooms
        lv_allRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), Chat_room.class);
                intent.putExtra("room", ((TextView)view).getText().toString());
                intent.putExtra("user", username);
                startActivity(intent);

            }
        });


// anzeige der suchbar
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ll_search.getVisibility() == View.GONE) {
                    ll_search.setVisibility(View.VISIBLE);
                } else {
                    ll_search.setVisibility(View.GONE);
                }
            }
        });

        // neuer raum erstellen -> alertdialog
        iv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText raumname = new EditText(getActivity());
                new AlertDialog.Builder(view.getContext())
                        .setTitle("New Chatroom")
                        .setMessage("What is the name of the room?")
                        .setView(raumname)
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (raumname.getText().toString().length() > 0) {
                                    CreateRoom(raumname.getText().toString());
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        return view;
    }


    // raumerstellen, update in der db
        private void CreateRoom(String rname) {
                Map<String, Object> map = new  HashMap<String, Object>();
                map.put(rname, "");
                dbr_chatrooms.updateChildren(map);
            }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }





}
