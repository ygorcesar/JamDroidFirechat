package com.ygorcesar.jamdroidfirechat.model;

import java.util.HashMap;

public class User {
    private String name;
    private String email;
    private String photoUrl;
    private HashMap<String, Object> timestampJoined;
    private boolean hasLoggedInWithPassword;

    public User() {
    }

    /**
     * @param name
     * @param email
     * @param photoUrl
     * @param timestampJoined
     */
    public User(String name, String email, String photoUrl, HashMap<String, Object> timestampJoined) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.timestampJoined = timestampJoined;
        this.hasLoggedInWithPassword = false;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    public boolean isHasLoggedInWithPassword() {
        return hasLoggedInWithPassword;
    }
}
