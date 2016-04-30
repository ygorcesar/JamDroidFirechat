package com.ygorcesar.jamdroidfirechat.model;

import java.util.HashMap;

public class Message {
    private String email;
    private String message;
    private HashMap<String, Object> time;

    public Message() {
    }

    /**
     * @param email
     * @param message
     * @param time
     */
    public Message(String email, String message, HashMap<String, Object> time) {
        this.email = email;
        this.message = message;
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, Object> getTime() {
        return time;
    }
}
