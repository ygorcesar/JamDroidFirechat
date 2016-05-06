package com.ygorcesar.jamdroidfirechat.utils;

public class AditionalNotificationData {
    private String chatKey;
    private String userName;
    private String userOneSignalId;

    public AditionalNotificationData(String chatKey, String userName, String userOneSignalId) {
        this.chatKey = chatKey;
        this.userName = userName;
        this.userOneSignalId = userOneSignalId;
    }

    public String getChatKey() {
        return chatKey;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserOneSignalId() {
        return userOneSignalId;
    }
}
