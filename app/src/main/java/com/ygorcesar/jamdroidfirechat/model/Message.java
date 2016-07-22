package com.ygorcesar.jamdroidfirechat.model;

import java.util.HashMap;

public class Message {
    private String email;
    private String message;
    private int type;
    private HashMap<String, Object> time;
    private MapLocation mapLocation;

    public Message() {
    }

    /**
     * @param email
     * @param message
     * @param type 0 - TEXT; 1 - IMAGE; 2 - LOCATION
     * @param time
     */
    public Message(String email, String message, int type, HashMap<String, Object> time) {
        this.email = email;
        this.message = message;
        this.type = type;
        this.time = time;
    }

    public Message(String email, String message, int type, HashMap<String, Object> time, MapLocation mapLocation) {
        this.email = email;
        this.message = message;
        this.type = type;
        this.time = time;
        this.mapLocation = mapLocation;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    public HashMap<String, Object> getTime() {
        return time;
    }

    public MapLocation getMapLocation() {
        return mapLocation;
    }
}
