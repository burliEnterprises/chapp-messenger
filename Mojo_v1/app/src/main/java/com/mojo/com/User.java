package com.mojo.com;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Matteo on 14.03.2017.
 */

@IgnoreExtraProperties
public class User {
    // https://firebase.google.com/docs/database/android/read-and-write

    public String username;
    public String mail;
    public String name;
    public String password;
    public String telefonnummer;

    public User() {
    }

    public User(String username, String password, String mail, String name, String telefon) {
        this.username = username;
        this.mail = mail;
        this.name = name;
        this.password = password;
        telefonnummer = telefon;
    }

}