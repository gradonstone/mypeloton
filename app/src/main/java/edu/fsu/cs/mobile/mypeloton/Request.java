package edu.fsu.cs.mobile.mypeloton;

public class Request {

    public String userID;
    public String email;
    public String ride_type;
    public int distance;
    public int time;
    //1 for active request, 0 for inactive request
    public int active;

    public Request(){}

    public Request(String userID, String email, String ride_type, int distance, int time, int active)
    {
        this.userID = userID;
        this.email = email;
        this.ride_type = ride_type;
        this.distance = distance;
        this.time = time;
        this.active = active;
    }

    public String getUserID()
    { return userID; }

    public String getEmail()
    { return email; }

    public String getRide_type()
    { return ride_type; }

    public int getDistance()
    { return distance; }

    public int getTime()
    { return time; }

    public int getActive()
    { return active; }

}
