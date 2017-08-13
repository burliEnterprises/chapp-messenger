package com.mojo.com;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mojo.com.recyclerview.Chatroom;
import com.mojo.com.recyclerview.ChatroomAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Dytstudio.
 */

public class FragmentChatrooms extends Fragment implements ChatroomAdapter.ViewHolder.ClickListener{
    private RecyclerView mRecyclerView;
    private ChatroomAdapter mAdapter;
    private DatabaseReference databaseReference, dbr_chatrooms, dbr_users;
    private FirebaseAuth firebaseAuth;
    private ArrayList<String> arraylist_raeume;
    List<Chatroom> data;

    public FragmentChatrooms(){
        setHasOptionsMenu(true);
    }
    public void onCreate(Bundle a){
        super.onCreate(a);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatrooms, null, false);

        getActivity().supportInvalidateOptionsMenu();
        ((MainActivity)getActivity()).changeTitle(R.id.toolbar, "Chatrooms");

        arraylist_raeume = new ArrayList<String>();
        data = new ArrayList<>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ChatroomAdapter(getContext(),data,this);
        mRecyclerView.setAdapter (mAdapter);


        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
        dbr_chatrooms = databaseReference.child("chatrooms");
        dbr_users = databaseReference.child("users");
        // raumliste wird staendig aktualisiert, key = raumname
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator iterator = dataSnapshot.child("chatrooms").getChildren().iterator();
                //  Iterator = Zeiger, mit dem die Elemente einer Menge durchlaufen werden können (z. B. eine Liste)
                Set xy = new HashSet();
                while (iterator.hasNext()) {
                    String room =  ((DataSnapshot) iterator.next()).getKey();        // gettet name one by one von der db
                    Log.d("xy", room);
                    Chatroom chatroom = new Chatroom();
                    chatroom.setName(room);
                    @DrawableRes int img= R.drawable.userpic;
                    chatroom.setImage(img);
                    xy.add(chatroom);
                };
                data.clear();
                data.addAll(xy);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });

        return view;
    }
    public List<Chatroom> setData(){
        List<Chatroom> data = new ArrayList<>();
       // String name[]= {"Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris", "Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris" };
        //@DrawableRes int img[]= {R.drawable.userpic , R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4 , R.drawable.userpic , R.drawable.user1, R.drawable.user2, R.drawable.user3, R.drawable.user4 };
        @DrawableRes int img= R.drawable.userpic;

        for (int i = 0; i<arraylist_raeume.size(); i++){
            Chatroom chatroom = new Chatroom();
            chatroom.setName(arraylist_raeume.get(i));
            chatroom.setImage(img);
            data.add(chatroom);
        }
        return data;
    }

    @Override
    public void onItemClicked (int position) {

    }

    @Override
    public boolean onItemLongClicked (int position) {
        toggleSelection(position);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection (position);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add, menu);
    }
}
