package com.ygorcesar.jamdroidfirechat.model;

import java.util.HashMap;

public class Chat {
    private String email;
    private String message;
    private HashMap<String, Object> time;

    public Chat() {
    }

    /**
     *
     * @param email
     * @param message
     * @param time
     */
    public Chat(String email, String message, HashMap<String, Object> time) {
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
