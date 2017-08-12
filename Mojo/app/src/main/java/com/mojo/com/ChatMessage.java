package com.mojo.com;

/**
 * Created by Matteo on 18.04.2017.
 */
public class ChatMessage {
    public boolean side;
    public String message, author, date, dein_name;

    public ChatMessage(boolean side, String message, String author, String date, String dein_name) {
        super();
        this.side = side;
        this.message = message;
        this.author = author;
        this.date = date;
        this.dein_name = dein_name;
    }
}