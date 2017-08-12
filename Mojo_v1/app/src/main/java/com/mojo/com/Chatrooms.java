package com.mojo.com;

/**
 * Created by Matteo on 19.03.2017.
 */

public class Chatrooms {
    public String title;

    public Chatrooms() {

    }

    public Chatrooms(String t) {
        this.title = t;
    }

    public String getRoomName() {
        return title;
    }

    public void setRoomName(String movieName) {
        this.title = movieName;
    }
}