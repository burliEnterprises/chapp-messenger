package com.mojo.com;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.JetPlayer;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Matteo on 15.03.2017.
 */

public class Contacts {
    Map<String, String> all_downloaded, alle_deine;
    Map<String, Object> back;
    String uid;
    public Contacts() {

    }


    // holt kontakte, wenn berehctigung erteilt, und schreibt sie in db
    public Map<String,String> ReadKontakte(ContentResolver cr) {
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Map<String,String> alContacts = new HashMap<String,String>();
        if(cursor.moveToFirst())
        {
            do
            {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
                    while (pCur.moveToNext())
                    {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //String contactNumber = "11111111";
                        String name = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        //Invalid key: Dr. Jakob Ott. Keys must not contain '/', '.', '#', '$', '[', or ']'
                        name = name.replace(".", "");
                        name = name.replace("/", "");
                        name = name.replace("#", "");
                        name = name.replace("$", "");
                        name = name.replace("[", "");
                        name = name.replace("]", "");
                        contactNumber = contactNumber.replace(" ", "");
                        contactNumber = standardisiereAUT(contactNumber);       // fügt +43 an alles
                        alContacts.put(name, contactNumber);
                        break;
                    }
                    pCur.close();
                }
            } while (cursor.moveToNext()) ;
        }
        return alContacts;
    }

    public String standardisiereAUT(String nummer) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber swissNumberProto = new Phonenumber.PhoneNumber();
        try {
            swissNumberProto = phoneUtil.parse(nummer, "AT");
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        };
        System.out.println(phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164));
        return phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
    }



    // holt alle nummern von der db, ruft funktion auf wenn abgeschlossen
    public void GetRegistredOnes(Map<String, String> alle_deine_kontakte, String uids) {
        uid = uids;
        alle_deine = alle_deine_kontakte;
        all_downloaded = new LinkedHashMap<String, String>();
        DatabaseReference dbr_nummbern = FirebaseDatabase.getInstance().getReference().getRoot().child("users").child("telefonnummern");

        dbr_nummbern.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    String single_downloaded_nummer = childDataSnapshot.getKey(); //displays the key for the node
                    String single_downloaded_uid = (String) childDataSnapshot.getValue();   //gives the value for given keyname
                    all_downloaded.put(single_downloaded_nummer, single_downloaded_uid);
                };
                 GetYourRegistredOnes(alle_deine, all_downloaded, uid);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

       // return all_downloaded;
    }


    // linkedhashmap als lösung dafür, dass in order ausgegeben wird (in hashmap spielt position des inserts keine rolle)
    // wird aufgerufen wenn kontakte geholt, sucht kontakte, die sowohl lokal als auch auf dem server sind und ladet diese hoch in das userprofil unter "kontakte"
    public void GetYourRegistredOnes ( Map<String,String> deineKontakte, Map<String, String> alleDownloads, String uid ) {
        back = new HashMap<String, Object>();       // kontaktnamen werden als boolean ausgelesen
        Set<String> s = new LinkedHashSet<String>(alleDownloads.keySet());        // key - nummer, value - uid
        Set<String> sss = new LinkedHashSet<String>(alleDownloads.values());        // key - nummer, value - uid
        Set<String> ss = new LinkedHashSet<String>(deineKontakte.values());       // key - name, value - nummer
        ss.retainAll(s);
        for (int i = 0; i < ss.size(); i++) {
            String x = ss.toArray()[i].toString();      // geht alle elemente durch, holt bei position i
            String y = "";
            for (int j = 0; j < s.size(); j++) {
                if (s.toArray()[j].toString().equals(x)) {
                    y = sss.toArray()[j].toString();
                    break;
                }
            }
            back.put(x, y );
        }
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().getRoot();
        dbr.child("users").child(uid).child("contacts").setValue(back);
    }

    public String GetNameFromAdressbookNumber(Context context, String nummer, ContentResolver cr) {
            String name = null;
/*
            // define the columns I want the query to return
            String[] projection = new String[] {
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup._ID};

            // encode the phone number and build the filter URI
            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(nummer));

            // query time
            Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                } else {
                    name = "fail";
                }
                cursor.close();
            } */
        Map<String, String> alleKontakte = ReadKontakte(cr);
        Set<String> name_alle = new LinkedHashSet<String>(alleKontakte.keySet());        // key - nummer, value - uid
        Set<String> nummer_alle = new LinkedHashSet<String>(alleKontakte.values());        // key - nummer, value - uid
        for (int i = 0; i < nummer_alle.size(); i++) {
            String x = nummer_alle.toArray()[i].toString();      // geht alle elemente durch, holt bei position i
            String y = nummer_alle.toArray()[i].toString();
            if (x == nummer) {
                name = y;
                break;
            } else {
                name = "Nummer nicht mehr im Kontaktbuch vorhanden";
            }
        }
            return name;
    }
    }



