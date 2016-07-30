package com.ygorcesar.jamdroidfirechat.utils;

public class PushNotificationObject {

    private String to;
    private AdditionalData data;

    public PushNotificationObject(String to, AdditionalData data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public AdditionalData getData() {
        return data;
    }

    public static class AdditionalData {
        private String TITLE;
        private String MSG;
        private String CHAT_KEY;
        private String MSG_KEY;
        private String USER_DISPLAY_NAME;
        private String USER_EMAIL;
        private String USER_FCM_DEVICE_ID;
        private String USER_FCM_DEVICE_ID_SENDER;

        public AdditionalData(String TITLE, String MSG, String CHAT_KEY,String MSG_KEY, String USER_DISPLAY_NAME,
                              String USER_EMAIL, String USER_FCM_DEVICE_ID, String USER_FCM_DEVICE_ID_SENDER) {
            this.TITLE = TITLE;
            this.MSG = MSG;
            this.CHAT_KEY = CHAT_KEY;
            this.MSG_KEY = MSG_KEY;
            this.USER_DISPLAY_NAME = USER_DISPLAY_NAME;
            this.USER_EMAIL = USER_EMAIL;
            this.USER_FCM_DEVICE_ID = USER_FCM_DEVICE_ID;
            this.USER_FCM_DEVICE_ID_SENDER = USER_FCM_DEVICE_ID_SENDER;
        }

        public String getTITLE() {
            return TITLE;
        }

        public String getMSG() {
            return MSG;
        }

        public String getCHAT_KEY() {
            return CHAT_KEY;
        }

        public String getMSG_KEY() {
            return MSG_KEY;
        }

        public String getUSER_DISPLAY_NAME() {
            return USER_DISPLAY_NAME;
        }

        public String getUSER_EMAIL() {
            return USER_EMAIL;
        }

        public String getUSER_FCM_DEVICE_ID() {
            return USER_FCM_DEVICE_ID;
        }

        public String getUSER_FCM_DEVICE_ID_SENDER() {
            return USER_FCM_DEVICE_ID_SENDER;
        }
    }
}
