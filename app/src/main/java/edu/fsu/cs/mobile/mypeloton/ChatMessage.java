package edu.fsu.cs.mobile.mypeloton;


import android.util.Log;

import java.util.Date;

public class ChatMessage {
    private String text;
    private long time;
    private String user;
    private String recipient;

    public ChatMessage(String Messagetext, String Messageuser,String recipient){
        this.text = Messagetext;
        this.user = Messageuser;
        this.recipient = recipient;
        this.time = new Date().getTime();
    }

    public ChatMessage(){}

    public String getText() {
       // Log.i("Messenger",text);
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUser() {
       // Log.i("Messenger",user);
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
