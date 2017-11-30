package com.ygorcesar.jamdroidfirechat.utils;


import java.util.ArrayList;
import java.util.List;

public class Singleton {
    private static final String TAG = "Singleton";
    private static Singleton ourInstance = new Singleton();
    private int mNumMessages;
    private List<String> mNotificationMessages;

    private Singleton() {
        mNumMessages = 0;
        mNotificationMessages = new ArrayList<>();
    }

    public static Singleton getInstance() {
        return ourInstance;
    }

    public int getNumMessages() {
        mNumMessages++;
        return mNumMessages;
    }

    public List<String> getNotificationMessages() {
        return mNotificationMessages;
    }

    public void clearMessagesAndNumber(){
        mNumMessages = 0;
        mNotificationMessages.clear();
    }

    public void addMessage(String message){
        mNotificationMessages.add(message);
    }
}
