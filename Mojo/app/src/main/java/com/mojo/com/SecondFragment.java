package com.mojo.com;
    import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SecondFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPageNo;

    private List<Chatrooms> list;
    private ListView lv_allPrivateChats;
    private SharedPreferences prefs;

    //declaratios
    private ArrayList<String> arraylist_contacts;
    private ArrayAdapter<String> adapter_contacts;
    private DatabaseReference databaseReference, dbr_chatrooms, dbr_users;
    private String username, name, email, telefonnummer;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser fbUser;

    private Contacts contacts;
    private Context context;

    public SecondFragment() {
        // Required empty public constructor
    }

    public static SecondFragment newInstance(int pageNo) {

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        SecondFragment fragment = new SecondFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNo = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        Stetho.initializeWithDefaults(getActivity());
        firebaseAuth = FirebaseAuth.getInstance();
        fbUser = firebaseAuth.getCurrentUser();

        lv_allPrivateChats = (ListView) view.findViewById(R.id.personlistView);

        // daten des benutzers aus dem speicher laden
        prefs = this.getActivity().getSharedPreferences("CHAPP_PREFS", Context.MODE_PRIVATE);
        username = prefs.getString("username", "");
        name = prefs.getString("name", "");
        email = prefs.getString("email","");
        telefonnummer = prefs.getString("telefonnummer", "");


        arraylist_contacts = new ArrayList<String>();
        adapter_contacts = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arraylist_contacts);
        lv_allPrivateChats.setAdapter(adapter_contacts);
        contacts = new Contacts();
        context = this.getContext();

        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
        dbr_chatrooms = databaseReference.child("chatrooms");
        dbr_users = databaseReference.child("users");


        // 1. alle kontakte in dem user db werden hgeholt
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator iterator = dataSnapshot.child("users").child(fbUser.getUid()).child("contacts").getChildren().iterator();
                Set<String> set_kontakte = new LinkedHashSet<String>();
                Set<String> set_nummern = new LinkedHashSet<String>();
                Set<String> set_users = new LinkedHashSet<String>();
                while (iterator.hasNext()) {
                    String tempNummer = (String) ((DataSnapshot) iterator.next()).getKey();
                    set_kontakte.add(tempNummer);
                };


                for (int i = 0; i < set_kontakte.size(); i++) {
                    // FEHLEND: AUSLESEN DER NAMEN - oben, geregelt in eigener tabelle mit telefonummer als key und username als value
                        String x = set_kontakte.toArray()[i].toString();      // geht alle elemente durch, holt bei position
                        String y = databaseReference.child("users/username").child(x).getKey();
                        set_final_usernames.add(y);
                };


                /*
                String tempUsername = databaseReference.child("users/username").child(set_kontakte.)
                String tempTelefonnummer = (String) ((DataSnapshot) nummern_users.next()).getValue();
                set_users.add(tempUsername);
                set_nummern.add(tempTelefonnummer);
                //  set.add(contacts.GetNameFromAdressbookNumber(context, tempNummer, getActivity().getContentResolver()));        // holt alle telefonnummern von server, liesst dazugehörige namen aus telefonbuch aus
                Set<String> set_final_usernames = new HashSet<String>();
                set_kontakte.retainAll(set_nummern);
                for (int i = 0; i < set_kontakte.size(); i++) {
                    // FEHLEND: AUSLESEN DER NAMEN - oben, geregelt in eigener tabelle mit telefonummer als key und username als value
                        String x = set_kontakte.toArray()[i].toString();      // geht alle elemente durch, holt bei position i
                        String y = "";
                        for (int j = 0; j < set_nummern.size(); j++) {
                            if (set_nummern.toArray()[j].toString().equals(x)) {
                                y = set_users.toArray()[j].toString();
                                break;
                            }
                        };
                        set_final_usernames.add(y);
                };

                 */
                arraylist_contacts.clear();
                arraylist_contacts.addAll(set_final_usernames);
                adapter_contacts.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });


        // aufruf eines chatrooms
        lv_allPrivateChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), private_chat_.class);
                intent.putExtra("room", ((TextView)view).getText().toString());
                intent.putExtra("user", username);
                startActivity(intent);

            }
        });




        return view;
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
