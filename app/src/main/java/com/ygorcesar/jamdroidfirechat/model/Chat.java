package com.ygorcesar.jamdroidfirechat.model;

public class Chat {
    private String email;
    private String message;

    public Chat() {
    }

    public Chat(String email, String message) {
        this.email = email;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }
}
