package com.mojo.com.recyclerview;

import android.support.annotation.DrawableRes;

/**
 * Created by Dytstudio.
 */

public class Chatroom {
    String name;
    int image;

    public int getImage(){
        return image;
    }
    public void setImage(@DrawableRes int img){
        image = img;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
