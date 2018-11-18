package edu.fsu.cs.mobile.mypeloton;

public class DisplayRequest {
    private String email;
    private String userID;

    public DisplayRequest(String email, String userID){
        this.email = email;
        this.userID = userID;
    }

    public String getEmail(){ return email; }

    public String getUserID() { return userID; }

}
