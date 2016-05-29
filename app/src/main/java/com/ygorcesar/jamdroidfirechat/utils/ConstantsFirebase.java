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
    public static final String FIREBASE_PROPERTY_USER_DEVICE_ID = "fcmUserDeviceId";
    public static final String FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD = "hasLoggedInWithPassword";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED_REVERSE = "timestampLastChangedReverse";

    /**
     * Constants para Login com Firebase
     */
    public static final String GOOGLE_PROVIDER = "google";
    public static final String FACEBOOK_PROVIDER = "facebook";

    public static final String PROVIDER_DATA_EMAIL = "email";
    public static final String PROVIDER_DATA_DISPLAY_NAME = "displayName";
    public static final String PROVIDER_DATA_PROFILE_IMAGE_URL = "profileImageURL";

    public static final String CHAT_GLOBAL_HELPER = "Global";
}
