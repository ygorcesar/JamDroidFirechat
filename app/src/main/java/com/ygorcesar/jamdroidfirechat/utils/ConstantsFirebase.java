package com.ygorcesar.jamdroidfirechat.utils;

public final class ConstantsFirebase {

    /**
     * Constants para locais Firebase
     */
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_USER_FRIENDS = "userFriends";
    public static final String FIREBASE_LOCATION_CHAT = "chat";
    public static final String FIREBASE_LOCATION_CHAT_GLOBAL = "chatGlobal";

    public static final String FIREBASE_TOPIC_CHAT_GLOBAL_TO = "/topics/chatGlobal";
    public static final String FIREBASE_TOPIC_CHAT_GLOBAL = "chatGlobal";

    /**
     * Constants para propriedades dos objetos Firebase
     */
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED = "timestampLastChanged";
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_EMAIL = "email";
    public static final String FIREBASE_PROPERTY_NAME = "name";
    public static final String FIREBASE_PROPERTY_ONLINE = "online";
    public static final String FIREBASE_PROPERTY_USER_DEVICE_ID = "fcmUserDeviceId";
    public static final String FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD = "hasLoggedInWithPassword";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED_REVERSE = "timestampLastChangedReverse";

    public static final String FIREBASE_PROPERTY_MESSAGE_STATUS = "status";

    /**
     * Constants para Login com Firebase
     */
    public static final String GOOGLE_PROVIDER = "google";
    public static final String FACEBOOK_PROVIDER = "facebook";

    public static final String PROVIDER_DATA_EMAIL = "email";
    public static final String PROVIDER_DATA_DISPLAY_NAME = "displayName";
    public static final String PROVIDER_DATA_PROFILE_IMAGE_URL = "profileImageURL";

    public static final String CHAT_GLOBAL_HELPER = "Global";

    /**
     * Messages Types
     * 0 - TEXT
     * 1 - IMAGE
     * 2 - MAP LOCATION
     */
    public static final int MESSAGE_TYPE_TEXT = 0;
    public static final int MESSAGE_TYPE_IMAGE = 1;
    public static final int MESSAGE_TYPE_LOCATION = 2;

    /**
     * Message Status
     * 0 - PENDING
     * 1 - SENDED
     * 2 - RECEIVED
     */
    public static final int MESSAGE_STATUS_PENDING = 0;
    public static final int MESSAGE_STATUS_SENDED = 1;
    public static final int MESSAGE_STATUS_RECEIVED = 2;
}
